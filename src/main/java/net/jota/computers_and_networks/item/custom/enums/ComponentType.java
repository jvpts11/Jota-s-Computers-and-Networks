package net.jota.computers_and_networks.item.custom.enums;

public enum ComponentType {
    CPU("cpu"),
    GPU("gpu"),
    RAM("ram"),
    HDD_ITEM("hdd_item"),
    HDD_FLUID("hdd_fluid"),
    SSD_ITEM("ssd_item"),
    SSD_FLUID("ssd_fluid"),
    MOTHERBOARD("motherboard");

    private final String name;

    ComponentType(String name) {
        this.name = name;
    }

    public String getName() { return name; }
}
