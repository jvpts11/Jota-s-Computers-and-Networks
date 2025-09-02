package net.jota.computers_and_networks.network.enums;

public enum ResourceType {
    ITEM("item"),
    FLUID("fluid");

    private final String name;

    ResourceType(String name) {
        this.name = name;
    }

    public String getName() { return name; }
}
