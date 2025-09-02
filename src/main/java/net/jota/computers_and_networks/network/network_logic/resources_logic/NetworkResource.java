package net.jota.computers_and_networks.network.network_logic.resources_logic;

import net.jota.computers_and_networks.network.enums.ResourceType;

import java.util.UUID;

public abstract class NetworkResource {
    protected final UUID id;
    protected final ResourceType type;
    protected final int amount;
    protected final UUID source;
    protected final UUID destination;

    public NetworkResource(ResourceType type, int amount, UUID source, UUID destination) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.amount = amount;
        this.source = source;
        this.destination = destination;
    }

    public abstract Object getResource();
    public abstract NetworkResource copyWithAmount(int newAmount);

    // Getters
    public UUID getId() { return id; }
    public ResourceType getType() { return type; }
    public int getAmount() { return amount; }
    public UUID getSource() { return source; }
    public UUID getDestination() { return destination; }
}
