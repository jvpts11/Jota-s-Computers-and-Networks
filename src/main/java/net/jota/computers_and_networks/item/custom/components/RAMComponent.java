package net.jota.computers_and_networks.item.custom.components;

import net.jota.computers_and_networks.item.custom.enums.ComponentTier;
import net.jota.computers_and_networks.item.custom.enums.ComponentType;
import net.jota.computers_and_networks.item.custom.interfaces.IComputerComponent;

public class RAMComponent implements IComputerComponent {
    private final ComponentTier tier;
    private final int capacityMB;

    public RAMComponent(ComponentTier tier) {
        this.tier = tier;
        this.capacityMB = tier.getBaseSpeed() * 1024; // 1GB, 2GB, 4GB, 8GB
    }

    public int getCapacityMB() { return capacityMB; }

    @Override
    public ComponentTier getTier() { return tier; }
    @Override
    public String getType() { return ComponentType.RAM.getName(); }
}
