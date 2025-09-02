package net.jota.computers_and_networks.network;

import net.jota.computers_and_networks.block.custom.enums.ComputerType;
import net.jota.computers_and_networks.item.custom.ComputerComponents;
import net.jota.computers_and_networks.network.computers.ServerComputer;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.UUID;

public abstract class NetworkComputer {
    protected final UUID id;
    protected String displayName;
    protected final ComputerType type;
    protected final ComputerComponents components;
    protected UUID networkId;

    public NetworkComputer(ComputerType type, String defaultName) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.displayName = defaultName;
        this.components = new ComputerComponents();
    }

    public abstract boolean canJoinNetwork(LogisticNetwork network);
    public abstract void onNetworkJoin(LogisticNetwork network);
    public abstract void onNetworkLeave();

    public int getDownloadSpeed() {
        return components.getTotalDownloadSpeed();
    }

    public int getUploadSpeed() {
        return components.getTotalUploadSpeed();
    }

    public float getGPUMultiplier() {
        return components.getTotalGPUMultiplier();
    }

    public int getItemStorageCapacity() {
        return components.getTotalItemCapacity();
    }

    public int getFluidStorageCapacity() {
        return components.getTotalFluidCapacity();
    }

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

    public int getRAMCapacity() {
        return components.getTotalRAMCapacity();
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String name) { this.displayName = name; }
    public ComputerType getType() { return type; }
    public UUID getNetworkId() { return networkId; }
    public ComputerComponents getComponents() { return components; }
}
