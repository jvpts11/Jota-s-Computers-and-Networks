package net.jota.computers_and_networks.network.enums;

public enum OperationStatus {
    PENDING("Pending"),
    PROCESSING("Processing"),
    COMPLETED("Completed"),
    FAILED("Failed");

    private final String displayName;

    OperationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED;
    }
}
