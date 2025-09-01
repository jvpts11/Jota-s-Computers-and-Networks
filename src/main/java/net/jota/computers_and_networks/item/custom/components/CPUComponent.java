package net.jota.computers_and_networks.item.custom.components;

import net.jota.computers_and_networks.item.custom.enums.ComponentTier;
import net.jota.computers_and_networks.item.custom.interfaces.ComputerComponent;

public class CPUComponent implements ComputerComponent {
    private final ComponentTier tier;
    private final int baseUploadSpeed;
    private final int baseDownloadSpeed;

    public CPUComponent(ComponentTier tier) {
        this.tier = tier;
        // Velocidade base por tick (pode ser ajustada conforme balanceamento)
        this.baseUploadSpeed = 4 * tier.getBaseSpeed(); // 4, 8, 16, 32 itens/tick
        this.baseDownloadSpeed = 4 * tier.getBaseSpeed(); // 4, 8, 16, 32 itens/tick
    }

    public int getUploadSpeed() { return baseUploadSpeed; }
    public int getDownloadSpeed() { return baseDownloadSpeed; }

    @Override
    public ComponentTier getTier() { return tier; }
    @Override
    public String getType() { return "cpu"; }
}
