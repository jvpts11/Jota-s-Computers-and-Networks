package net.jota.computers_and_networks.item.custom.interfaces;

import net.jota.computers_and_networks.item.custom.enums.ComponentTier;

public interface IComputerComponent {
    ComponentTier getTier();
    String getType();
}
