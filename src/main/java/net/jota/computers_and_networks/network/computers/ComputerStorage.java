package net.jota.computers_and_networks.network.computers;

import net.jota.computers_and_networks.block.custom.enums.ComputerType;
import net.jota.computers_and_networks.network.enums.StorageType;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComputerStorage {
    private final ComputerType computerType;
    private final List<StorageDevice> storageDevices;
    private final ItemStackHandler itemStorage;
    private final List<FluidTank> fluidTanks;

    public ComputerStorage(ComputerType computerType) {
        this.computerType = computerType;
        this.storageDevices = new ArrayList<>();
        this.itemStorage = new ItemStackHandler(0);
        this.fluidTanks = new ArrayList<>();
    }

    public boolean addStorageDevice(StorageDevice device) {
        if (canAddDevice(device)) {
            storageDevices.add(device);
            updateStorageCapacity();
            return true;
        }
        return false;
    }

    private boolean canAddDevice(StorageDevice device) {
        int currentCount = storageDevices.stream()
                .filter(d -> d.getType() == device.getType())
                .mapToInt(d -> 1)
                .sum();

        int maxDevices = getMaxDevices(device.getType());
        return currentCount < maxDevices;
    }

    private int getMaxDevices(StorageType type) {
        switch (computerType) {
            case MAINFRAME:
                return type == StorageType.HDD_ITEM || type == StorageType.HDD_FLUID ? 8 : 4;
            case SERVER:
                return type == StorageType.HDD_ITEM || type == StorageType.HDD_FLUID ? 12 : 6;
            case PERSONAL_COMPUTER:
                return type == StorageType.HDD_ITEM || type == StorageType.HDD_FLUID ? 2 : 1;
            default:
                return 0;
        }
    }

    private void updateStorageCapacity() {
        int itemSlots = storageDevices.stream()
                .filter(device -> device.getType() == StorageType.HDD_ITEM || device.getType() == StorageType.SSD_ITEM)
                .mapToInt(StorageDevice::getCapacity)
                .sum();
        itemStorage.setSize(itemSlots);

        fluidTanks.clear();
        storageDevices.stream()
                .filter(device -> device.getType() == StorageType.HDD_FLUID || device.getType() == StorageType.SSD_FLUID)
                .forEach(device -> fluidTanks.add(new FluidTank(device.getCapacity())));
    }

    // Getters
    public List<StorageDevice> getStorageDevices() { return Collections.unmodifiableList(storageDevices); }
    public ItemStackHandler getItemStorage() { return itemStorage; }
    public List<FluidTank> getFluidTanks() { return Collections.unmodifiableList(fluidTanks); }
    public int getTotalItemCapacity() { return itemStorage.getSlots(); }
    public int getTotalFluidCapacity() {
        return fluidTanks.stream().mapToInt(FluidTank::getCapacity).sum();
    }
}
