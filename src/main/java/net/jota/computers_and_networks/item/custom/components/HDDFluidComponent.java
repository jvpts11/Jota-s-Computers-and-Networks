package net.jota.computers_and_networks.item.custom.components;

import net.jota.computers_and_networks.item.custom.enums.ComponentTier;
import net.jota.computers_and_networks.item.custom.enums.ComponentType;
import net.jota.computers_and_networks.item.custom.interfaces.IComputerComponent;

public class HDDFluidComponent implements IComputerComponent {
    private final ComponentTier tier;
    private final int capacity; // MB(Milibuckets)

    public HDDFluidComponent(ComponentTier tier) {
        this.tier = tier;
        this.capacity = tier.getBaseSpeed() * 64000; // 64,000MB, 128,000MB, etc
    }

    public int getCapacity() { return capacity; }

    @Override
    public ComponentTier getTier() { return tier; }
    @Override
    public String getType() { return ComponentType.HDD_FLUID.getName(); }
}
