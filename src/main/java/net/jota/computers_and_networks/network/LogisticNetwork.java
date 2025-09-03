package net.jota.computers_and_networks.network;

import net.jota.computers_and_networks.block.custom.enums.ComputerType;
import net.jota.computers_and_networks.network.computers.ServerComputer;
import net.jota.computers_and_networks.network.enums.OperationStatus;
import net.jota.computers_and_networks.network.enums.ResourceType;
import net.jota.computers_and_networks.network.interfaces.NetworkDevice;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LogisticNetwork {
    private final UUID id;
    private final Map<UUID, NetworkDevice> devices; // Todos os dispositivos
    private final NetworkBuffer buffer;
    private UUID mainframeId;
    private boolean isValid;

    public LogisticNetwork() {
        this.id = UUID.randomUUID();
        this.devices = new HashMap<>();
        this.buffer = new NetworkBuffer();
        this.isValid = false;
        NetworkCore.registerNetwork(this);
    }

    public boolean addDevice(NetworkDevice device) {
        if (devices.containsKey(device.getId())) return false;
        if (!device.canJoinNetwork(this)) return false;

        devices.put(device.getId(), device);

        if (device instanceof NetworkComputer computer) {
            if (computer.getType() == ComputerType.MAINFRAME) {
                if (mainframeId != null) return false;
                mainframeId = computer.getId();
            }
        }

        updateNetworkValidity();
        device.onNetworkJoin(this);
        return true;
    }

    public boolean removeDevice(UUID deviceId) {
        NetworkDevice removed = devices.remove(deviceId);
        if (removed != null) {
            if (mainframeId != null && mainframeId.equals(deviceId)) {
                mainframeId = null;
            }
            updateNetworkValidity();
            removed.onNetworkLeave();
            return true;
        }
        return false;
    }

    public void updateNetworkValidity() {
        long mainframeCount = devices.values().stream()
                .filter(device -> device instanceof NetworkComputer)
                .map(device -> (NetworkComputer) device)
                .filter(comp -> comp.getType() == ComputerType.MAINFRAME)
                .count();

        this.isValid = mainframeCount == 1;
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
                operation.setFailureReason("Not enough " + getItemName(item));
                return false;
            }

            if (needMultipleSources(item, requested)) {
                return executeMultiSourceOperation(operation);
            }
        }

        return buffer.processOperation(operation, this);
    }

    private String getItemName(Item item) {
        try {
            return item.getDescription().getString();
        } catch (Exception e) {
            return "Unknown Item";
        }
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

        for (NetworkDevice device : devices.values()) {
            if (device instanceof ServerComputer server) {
                int count = server.getItemCount(item);
                if (count > 0) {
                    sources.put(device.getId(), count);
                }
            }
        }

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

        for (NetworkDevice device : devices.values()) {
            if (device instanceof ServerComputer server) {
                for (int i = 0; i < server.getSlots(); i++) {
                    ItemStack stack = server.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        items.merge(stack.getItem(), stack.getCount(), Integer::sum);
                    }
                }
            }
        }

        return items;
    }

    public Map<ItemStack, Integer> getItemStorageWithNBT() {
        Map<ItemStack, Integer> items = new HashMap<>();

        for (NetworkDevice device : devices.values()) {
            if (device instanceof ServerComputer server) {
                for (int i = 0; i < server.getSlots(); i++) {
                    ItemStack stack = server.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        items.merge(stack, stack.getCount(), Integer::sum);
                    }
                }
            }
        }

        return items;
    }

    public Map<Fluid, Integer> getFluidStorage() {
        Map<Fluid, Integer> fluids = new HashMap<>();

        for (NetworkDevice device : devices.values()) {
            if (device instanceof ServerComputer server) {
                for (FluidTank tank : server.getFluidTanks()) {
                    FluidStack fluidStack = tank.getFluid();
                    if (!fluidStack.isEmpty()) {
                        fluids.merge(fluidStack.getFluid(), fluidStack.getAmount(), Integer::sum);
                    }
                }
            }
        }

        return fluids;
    }

    public int getTotalFluidCapacity() {
        int total = 0;
        for (NetworkDevice device : devices.values()) {
            if (device instanceof NetworkComputer computer) {
                total += computer.getFluidStorageCapacity();
            }
        }
        return total;
    }

    public int getUsedFluidCapacity() {
        int used = 0;
        for (NetworkDevice device : devices.values()) {
            if (device instanceof NetworkComputer computer) {
                used += computer.getUsedFluidCapacity();
            }
        }
        return used;
    }

    public <T extends NetworkDevice> List<T> getDevicesByType(Class<T> deviceClass) {
        List<T> result = new ArrayList<>();
        for (NetworkDevice device : devices.values()) {
            if (deviceClass.isInstance(device)) {
                result.add(deviceClass.cast(device));
            }
        }
        return result;
    }

    public List<NetworkDevice> getDevicesByFunction(Predicate<NetworkDevice> predicate) {
        return devices.values().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public List<NetworkComputer> getComputers() {
        List<NetworkComputer> computers = new ArrayList<>();
        for (NetworkDevice device : devices.values()) {
            if (device instanceof NetworkComputer computer) {
                computers.add(computer);
            }
        }
        return computers;
    }

    // Getters
    public Map<UUID, NetworkDevice> getDevices() {
        return Collections.unmodifiableMap(devices);
    }

    public UUID getId() { return id; }
    public boolean isValid() { return isValid; }
    public NetworkBuffer getBuffer() { return buffer; }

    public Optional<NetworkComputer> getMainframe() {
        if (mainframeId == null) return Optional.empty();
        NetworkDevice device = devices.get(mainframeId);
        return device instanceof NetworkComputer ?
                Optional.of((NetworkComputer) device) : Optional.empty();
    }
}
