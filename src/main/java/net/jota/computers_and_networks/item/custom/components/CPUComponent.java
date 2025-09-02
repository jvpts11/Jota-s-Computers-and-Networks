package net.jota.computers_and_networks.item.custom.components;

import net.jota.computers_and_networks.item.custom.enums.ComponentTier;
import net.jota.computers_and_networks.item.custom.interfaces.IComputerComponent;

public class CPUComponent implements IComputerComponent {
    private final ComponentTier tier;
    private final int baseUploadSpeed;
    private final int baseDownloadSpeed;

    public CPUComponent(ComponentTier tier) {
        this.tier = tier;
        this.baseUploadSpeed = 4 * tier.getBaseSpeed(); // 4, 8, 16, 32 items/tick
        this.baseDownloadSpeed = 4 * tier.getBaseSpeed(); // 4, 8, 16, 32 items/tick
    }

    public int getUploadSpeed() { return baseUploadSpeed; }
    public int getDownloadSpeed() { return baseDownloadSpeed; }

    @Override
    public ComponentTier getTier() { return tier; }
    @Override
    public String getType() { return "cpu"; }
}
