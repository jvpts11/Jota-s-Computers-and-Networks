package net.jota.computers_and_networks.network;

import net.jota.computers_and_networks.network.computers.ServerComputer;
import net.jota.computers_and_networks.network.enums.OperationStatus;
import net.jota.computers_and_networks.network.enums.OperationType;
import net.jota.computers_and_networks.network.enums.ResourceType;
import net.jota.computers_and_networks.network.network_logic.interfaces.IFluidHandlerDestination;
import net.jota.computers_and_networks.network.network_logic.interfaces.IFluidHandlerSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.*;

public class NetworkBuffer {
    private final Map<UUID, BufferResource> resources;
    private final Queue<NetworkOperation> operationQueue;
    private final Map<ResourceType, Integer> transferRates;

    public NetworkBuffer() {
        this.resources = new HashMap<>();
        this.operationQueue = new LinkedList<>();
        this.transferRates = new HashMap<>();
        this.transferRates.put(ResourceType.ITEM, 0);
        this.transferRates.put(ResourceType.FLUID, 0); // Em mb/tick
    }

    public boolean processOperation(NetworkOperation operation, LogisticNetwork network) {
        operationQueue.add(operation);
        return true;
    }

    public void tick(LogisticNetwork network) {
        updateTransferRates(network);
        processOperationQueue(network);
        processActiveTransfers(network);
        cleanupCompletedTransfers();
    }

    private void updateTransferRates(LogisticNetwork network) {
        transferRates.put(ResourceType.ITEM, network.getNetworkTransferRate());
        transferRates.put(ResourceType.FLUID, network.getFluidTransferRate());
    }

    private void processOperationQueue(LogisticNetwork network) {
        NetworkComputer mainframe = network.getMainframe().orElse(null);
        if (mainframe == null) return;

        int maxOperationsPerTick = mainframe.getUploadSpeed();
        int processed = 0;

        while (!operationQueue.isEmpty() && processed < maxOperationsPerTick) {
            NetworkOperation operation = operationQueue.poll();
            if (operation != null) {
                startTransfer(operation, network);
                processed++;
            }
        }
    }

    private void startTransfer(NetworkOperation operation, LogisticNetwork network) {
        BufferResource bufferResource = new BufferResource(operation.getId(), operation.getResource());
        resources.put(operation.getId(), bufferResource);
        operation.setStatus(OperationStatus.PROCESSING);
    }

    private void processActiveTransfers(LogisticNetwork network) {
        for (BufferResource buffer : resources.values()) {
            if (!buffer.isTransferComplete()) {
                processTransfer(buffer, network);
            }
        }
    }

    private void processTransfer(BufferResource buffer, LogisticNetwork network) {
        ResourceType type = buffer.getResource().getType();
        int transferRate = transferRates.get(type);

        switch (type) {
            case ITEM:
                processItemTransfer(buffer, network, transferRate);
                break;
            case FLUID:
                processFluidTransfer(buffer, network, transferRate);
                break;
        }
    }

    private void processItemTransfer(BufferResource buffer, LogisticNetwork network, int transferRate) {
        int amountToTransfer = Math.min(buffer.getRemainingAmount(), transferRate);

        NetworkComputer sourceComputer = network.getComputers().get(buffer.getResource().getSource());
        NetworkComputer destComputer = network.getComputers().get(buffer.getResource().getDestination());

        if (sourceComputer instanceof ServerComputer itemSource &&
                destComputer instanceof ServerComputer itemDest) {

            ItemStack itemToTransfer = buffer.getItemStack();
            itemToTransfer.setCount(amountToTransfer);

            ItemStack extracted = itemSource.extractItem(0, amountToTransfer, true);
            if (!extracted.isEmpty() && extracted.getCount() >= amountToTransfer) {
                ItemStack remaining = itemDest.insertItem(0, itemToTransfer, true);
                if (remaining.getCount() < amountToTransfer) {
                    int actuallyTransferred = amountToTransfer - remaining.getCount();

                    itemSource.extractItem(0, actuallyTransferred, false);

                    ItemStack toInsert = itemToTransfer.copy();
                    toInsert.setCount(actuallyTransferred);
                    itemDest.insertItem(0, toInsert, false);

                    buffer.transfer(actuallyTransferred);
                }
            }
        }
    }

    private void cleanupCompletedTransfers() {
        Iterator<Map.Entry<UUID, BufferResource>> iterator = resources.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, BufferResource> entry = iterator.next();
            if (entry.getValue().isTransferComplete()) {
                iterator.remove();

                // TODO: Notify the operation was completed
                System.out.println("Operation completed: " + entry.getKey());
            }
        }
    }

    private void processFluidTransfer(BufferResource buffer, LogisticNetwork network, int transferRate) {
        FluidStack fluidToTransfer = buffer.getFluidStack();
        int amountToTransfer = Math.min(fluidToTransfer.getAmount(), transferRate);

        NetworkComputer sourceComputer = network.getComputers().get(buffer.getResource().getSource());
        NetworkComputer destComputer = network.getComputers().get(buffer.getResource().getDestination());

        if (sourceComputer instanceof IFluidHandlerSource fluidSource &&
                destComputer instanceof IFluidHandlerDestination fluidDest) {

            FluidStack extracted = fluidSource.extractFluid(fluidToTransfer, amountToTransfer, true);
            if (extracted.getAmount() >= amountToTransfer) {
                FluidStack remaining = fluidDest.insertFluid(extracted, true);
                if (remaining.getAmount() < amountToTransfer) {
                    int actuallyTransferred = amountToTransfer - remaining.getAmount();

                    fluidSource.extractFluid(fluidToTransfer, actuallyTransferred, false);
                    fluidDest.insertFluid(extracted, false);

                    buffer.transfer(actuallyTransferred);
                }
            }
        }
    }
}
