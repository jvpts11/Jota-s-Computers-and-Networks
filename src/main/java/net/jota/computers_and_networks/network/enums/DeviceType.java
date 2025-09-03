package net.jota.computers_and_networks.network.enums;

public enum DeviceType {
    COMPUTER("computer"),
    IMPORTER("importer"),
    EXPORTER("exporter"),
    INTERFACE("interface"),
    MONITOR("monitor"),
    TERMINAL("terminal"),
    PROCESSOR("processor"),
    STORAGE_UNIT("storage_unit");

    private final String name;

    DeviceType(String name) {
        this.name = name;
    }

    public String getName() {return name;}
}