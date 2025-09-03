package net.jota.computers_and_networks.network;

import net.jota.computers_and_networks.block.custom.enums.ComputerType;
import net.jota.computers_and_networks.item.custom.ComputerComponents;
import net.jota.computers_and_networks.network.computers.ComputerStorage;
import net.jota.computers_and_networks.network.computers.ServerComputer;
import net.jota.computers_and_networks.network.enums.DeviceType;
import net.jota.computers_and_networks.network.interfaces.NetworkDevice;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.UUID;

public abstract class NetworkComputer implements NetworkDevice {
    protected final UUID id;
    protected String displayName;
    protected final ComputerType type;
    protected final ComputerComponents components;
    protected final ComputerStorage storage;
    protected UUID networkId;

    public NetworkComputer(ComputerType type, String defaultName) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.displayName = defaultName;
        this.components = new ComputerComponents();
        this.storage = new ComputerStorage(type);
    }

    public abstract boolean canJoinNetwork(LogisticNetwork network);
    public abstract void onNetworkJoin(LogisticNetwork network);
    public abstract void onNetworkLeave();

    public int getUsedItemSlots() {
        if (this instanceof ServerComputer server) {
            int used = 0;
            for (int i = 0; i < server.getSlots(); i++) {
                if (!server.getStackInSlot(i).isEmpty()) {
                    used++;
                }
            }
            return used;
        }
        return 0;
    }

    public int getUsedFluidCapacity() {
        if (this instanceof ServerComputer server) {
            return server.getFluidTanks().stream()
                    .mapToInt(FluidTank::getFluidAmount)
                    .sum();
        }
        return 0;
    }



    // Getters e Setters
    public UUID getId() { return id; }
    public UUID getNetworkId() { return networkId; }
    public void setNetworkId(UUID networkId) { this.networkId = networkId; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String name) { this.displayName = name; }

    public DeviceType getDeviceType() { return DeviceType.COMPUTER; }
    public ComputerType getType() { return type; }
    public ComputerComponents getComponents() { return components; }
    public ComputerStorage getStorage() { return storage; }

    public int getDownloadSpeed() {
        return components.getTotalDownloadSpeed();
    }
    public int getUploadSpeed() {
        return components.getTotalUploadSpeed();
    }
    public float getGPUMultiplier() {
        return components.getTotalGPUMultiplier();
    }
    public int getRAMCapacity() {
        return components.getTotalRAMCapacity();
    }

    public int getItemStorageCapacity() {
        return components.getTotalItemCapacity();
    }

    public int getFluidStorageCapacity() {
        return components.getTotalFluidCapacity();
    }
}
