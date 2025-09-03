package net.jota.computers_and_networks.item.custom.components;

import net.jota.computers_and_networks.item.custom.enums.ComponentTier;
import net.jota.computers_and_networks.item.custom.enums.ComponentType;
import net.jota.computers_and_networks.item.custom.interfaces.IComputerComponent;
import net.jota.computers_and_networks.network.computers.StorageDevice;
import net.jota.computers_and_networks.network.enums.StorageType;

public class HDDItemComponent{
    private final ComponentTier tier;

    public HDDItemComponent(ComponentTier tier) {
        this.tier = tier;
    }

    public StorageDevice toStorageDevice() {
        return new StorageDevice(StorageType.HDD_ITEM, tier);
    }

    public ComponentTier getTier() { return tier; }

    public int getCapacity() {
        return toStorageDevice().getCapacity();
    }
}
