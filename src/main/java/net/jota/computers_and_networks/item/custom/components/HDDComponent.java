package net.jota.computers_and_networks.item.custom.components;

import net.jota.computers_and_networks.item.custom.enums.ComponentTier;
import net.jota.computers_and_networks.item.custom.enums.ComponentType;
import net.jota.computers_and_networks.item.custom.interfaces.ComputerComponent;

public class HDDComponent implements ComputerComponent {
    private final ComponentTier tier;
    private final int capacityGB;

    public HDDComponent(ComponentTier tier) {
        this.tier = tier;
        this.capacityGB = tier.getBaseSpeed() * 64; // 64GB, 128GB, 256GB, 512GB
    }

    public int getCapacity() { return capacityGB * 1024 * 1024; } // Retorna capacidade em KB

    @Override
    public ComponentTier getTier() { return tier; }
    @Override
    public String getType() { return ComponentType.HDD.getName(); }
}
