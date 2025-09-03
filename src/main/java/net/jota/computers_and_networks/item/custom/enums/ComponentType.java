package net.jota.computers_and_networks.item.custom.enums;

public enum ComponentType {
    CPU("cpu"),
    GPU("gpu"),
    RAM("ram"),
    MOTHERBOARD("motherboard");

    private final String name;

    ComponentType(String name) {
        this.name = name;
    }

    public String getName() { return name; }
}
