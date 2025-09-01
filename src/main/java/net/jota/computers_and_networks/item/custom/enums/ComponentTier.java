package net.jota.computers_and_networks.item.custom.enums;

public enum ComponentTier {
    BASIC(1, "basic"),
    ADVANCED(2, "advanced"),
    ELITE(4, "elite"),
    ULTIMATE(8, "ultimate");

    private final int baseSpeed;
    private final String name;

    ComponentTier(int baseSpeed, String name) {
        this.baseSpeed = baseSpeed;
        this.name = name;
    }

    public int getBaseSpeed() { return baseSpeed; }
    public String getName() { return name; }
}
