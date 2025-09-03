package net.jota.computers_and_networks.network.computers;

import net.jota.computers_and_networks.item.custom.enums.ComponentTier;
import net.jota.computers_and_networks.network.enums.StorageType;

public class StorageDevice {
    private final StorageType type;
    private final ComponentTier tier;
    private final int capacity;

    public StorageDevice(StorageType type, ComponentTier tier) {
        this.type = type;
        this.tier = tier;
        this.capacity = calculateCapacity(type, tier);
    }

    private int calculateCapacity(StorageType type, ComponentTier tier) {
        int baseCapacity = switch (type) {
            case HDD_ITEM -> 64;
            case HDD_FLUID -> 64000;
            case SSD_ITEM -> 128;
            case SSD_FLUID -> 128000;
            default -> 0;
        };
        return baseCapacity * tier.getBaseSpeed();
    }

    // Getters
    public StorageType getType() { return type; }
    public ComponentTier getTier() { return tier; }
    public int getCapacity() { return capacity; }
}
