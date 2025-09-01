package net.jota.computers_and_networks.item.custom.components;

import net.jota.computers_and_networks.item.custom.enums.ComponentTier;
import net.jota.computers_and_networks.item.custom.enums.ComponentType;
import net.jota.computers_and_networks.item.custom.interfaces.ComputerComponent;

public class GPUComponent implements ComputerComponent {
    private final ComponentTier tier;
    private final float performanceMultiplier;

    public GPUComponent(ComponentTier tier) {
        this.tier = tier;
        this.performanceMultiplier = 1.0f + (tier.getBaseSpeed() * 0.5f); // 1.5x, 2.0x, 3.0x, 5.0x
    }

    public float getPerformanceMultiplier() { return performanceMultiplier; }

    @Override
    public ComponentTier getTier() { return tier; }
    @Override
    public String getType() { return ComponentType.GPU.getName(); }
}
