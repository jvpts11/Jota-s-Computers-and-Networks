package net.jota.computers_and_networks.network;

import net.jota.computers_and_networks.block.custom.enums.ComputerType;
import net.jota.computers_and_networks.network.computers.ServerComputer;
import net.jota.computers_and_networks.network.enums.OperationStatus;
import net.jota.computers_and_networks.network.enums.ResourceType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.*;

public class LogisticNetwork {
    private final UUID id;
    private final Map<UUID, NetworkComputer> computers;
    private final NetworkBuffer buffer;
    private UUID mainframeId;
    private boolean isValid;

    public LogisticNetwork() {
        this.id = UUID.randomUUID();
        this.computers = new HashMap<>();
        this.buffer = new NetworkBuffer();
        this.isValid = false;
        NetworkCore.registerNetwork(this);
    }

    public boolean addComputer(NetworkComputer computer) {
        if (computers.containsKey(computer.getId())) return false;

        computers.put(computer.getId(), computer);
        updateNetworkValidity();
        return true;
    }

    public boolean removeComputer(UUID computerId) {
        NetworkComputer removed = computers.remove(computerId);
        if (removed != null) {
            if (mainframeId.equals(computerId)) {
                mainframeId = null;
            }
            updateNetworkValidity();
            return true;
        }
        return false;
    }

    public void updateNetworkValidity() {
        long mainframeCount = computers.values().stream()
                .filter(comp -> comp.getType() == ComputerType.MAINFRAME)
                .count();

        this.isValid = mainframeCount == 1;

        if (isValid) {
            mainframeId = computers.values().stream()
                    .filter(comp -> comp.getType() == ComputerType.MAINFRAME)
                    .findFirst()
                    .map(NetworkComputer::getId)
                    .orElse(null);
        }
    }

    public int getNetworkTransferRate() {
        return getMainframe().map(mainframe -> {
            int baseSpeed = mainframe.getUploadSpeed();
            float gpuMultiplier = mainframe.getGPUMultiplier();
            return (int) (baseSpeed * gpuMultiplier);
        }).orElse(0);
    }

    public int getTotalBufferCapacity() {
        return getMainframe().map(mainframe ->
                mainframe.getRAMCapacity() * 1024
        ).orElse(0);
    }

    public boolean executeOperation(NetworkOperation operation) {
        if (!isValid) return false;

        if (operation.getResourceType() == ResourceType.ITEM) {
            ItemStack itemStack = operation.getItem();
            if (itemStack.isEmpty()) {
                operation.setStatus(OperationStatus.FAILED);
                operation.setFailureReason("Invalid item");
                return false;
            }

            Item item = itemStack.getItem();
            int available = getItemStorage().getOrDefault(item, 0);
            int requested = operation.getResource().getAmount();

            if (available < requested) {
                operation.setStatus(OperationStatus.FAILED);
                operation.setFailureReason("Not enough " + item.getDescription().getString());
                return false;
            }

            if (needMultipleSources(item, requested)) {
                return executeMultiSourceOperation(operation);
            }
        }

        return buffer.processOperation(operation, this);
    }

    private boolean needMultipleSources(Item item, int requestedAmount) {
        Map<UUID, Integer> sources = findItemSources(item);
        return sources.values().stream().anyMatch(amount -> amount < requestedAmount);
    }

    private boolean executeMultiSourceOperation(NetworkOperation operation) {
        Item item = operation.getItem().getItem();
        int remaining = operation.getResource().getAmount();

        Map<UUID, Integer> sources = findItemSources(item);

        for (Map.Entry<UUID, Integer> entry : sources.entrySet()) {
            if (remaining <= 0) break;

            int amountToTake = Math.min(remaining, entry.getValue());
            NetworkOperation subOp = NetworkOperation.createItemTransfer(
                    new ItemStack(item, amountToTake),
                    entry.getKey(),
                    operation.getDestination(),
                    operation.getSender()
            );

            buffer.processOperation(subOp, this);
            remaining -= amountToTake;
        }

        return true;
    }

    private Map<UUID, Integer> findItemSources(Item item) {
        Map<UUID, Integer> sources = new HashMap<>();

        getComputers().values().stream()
                .filter(computer -> computer instanceof ServerComputer)
                .map(computer -> (ServerComputer) computer)
                .forEach(server -> {
                    int count = server.getItemCount(item);
                    if (count > 0) {
                        sources.put(server.getId(), count);
                    }
                });

        return sources;
    }

    public int getFluidTransferRate() {
        return getMainframe().map(mainframe -> {
            int baseSpeed = mainframe.getUploadSpeed() * 10;
            float gpuMultiplier = mainframe.getGPUMultiplier();
            return (int) (baseSpeed * gpuMultiplier);
        }).orElse(0);
    }

    public int getTotalFluidBufferCapacity() {
        return getMainframe().map(mainframe ->
                mainframe.getRAMCapacity() * 10
        ).orElse(0);
    }

    public Map<Item, Integer> getItemStorage() {
        Map<Item, Integer> items = new HashMap<>();

        getComputers().values().stream()
                .filter(computer -> computer instanceof ServerComputer)
                .map(computer -> (ServerComputer) computer)
                .forEach(server -> {
                    for (int i = 0; i < server.getSlots(); i++) {
                        ItemStack stack = server.getStackInSlot(i);
                        if (!stack.isEmpty()) {
                            items.merge(stack.getItem(), stack.getCount(), Integer::sum);
                        }
                    }
                });

        return items;
    }

    public Map<ItemStack, Integer> getItemStorageWithNBT() {
        Map<ItemStack, Integer> items = new HashMap<>();

        getComputers().values().stream()
                .filter(computer -> computer instanceof ServerComputer)
                .map(computer -> (ServerComputer) computer)
                .forEach(server -> {
                    for (int i = 0; i < server.getSlots(); i++) {
                        ItemStack stack = server.getStackInSlot(i);
                        if (!stack.isEmpty()) {
                            items.merge(stack, stack.getCount(), Integer::sum);
                        }
                    }
                });

        return items;
    }

    public Map<Fluid, Integer> getFluidStorage() {
        Map<Fluid, Integer> fluids = new HashMap<>();

        getComputers().values().stream()
                .filter(computer -> computer instanceof ServerComputer)
                .map(computer -> (ServerComputer) computer)
                .forEach(server -> {
                    server.getFluidTanks().forEach(tank -> {
                        FluidStack fluidStack = tank.getFluid();
                        if (!fluidStack.isEmpty()) {
                            fluids.merge(fluidStack.getFluid(), fluidStack.getAmount(), Integer::sum);
                        }
                    });
                });

        return fluids;
    }

    public int getTotalFluidCapacity() {
        return getComputers().values().stream()
                .filter(computer -> computer instanceof ServerComputer)
                .mapToInt(NetworkComputer::getFluidStorageCapacity)
                .sum();
    }

    public int getUsedFluidCapacity() {
        return getComputers().values().stream()
                .filter(computer -> computer instanceof ServerComputer)
                .mapToInt(NetworkComputer::getUsedFluidCapacity)
                .sum();
    }

    // Getters
    public Map<UUID, NetworkComputer> getComputers() {
        return Collections.unmodifiableMap(computers);
    }
    public UUID getId() { return id; }
    public boolean isValid() { return isValid; }
    public NetworkBuffer getBuffer() { return buffer; }
    public Optional<NetworkComputer> getMainframe() {
        return Optional.ofNullable(computers.get(mainframeId));
    }
}
