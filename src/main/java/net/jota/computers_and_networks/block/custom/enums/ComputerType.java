package net.jota.computers_and_networks.block.custom.enums;

public enum ComputerType {
    MAINFRAME("mainframe"),
    SERVER("server"),
    PERSONAL_COMPUTER("personal_computer");

    private final String name;

    ComputerType(String name) {
        this.name = name;
    }

    public String getName() { return name; }
}

