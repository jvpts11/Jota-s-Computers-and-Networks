package net.jota.computers_and_networks.network;

import net.jota.computers_and_networks.network.enums.OperationStatus;
import net.jota.computers_and_networks.network.enums.OperationType;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class NetworkOperation {
    private final UUID id;
    private final OperationType type;
    private final UUID source;
    private final UUID destination;
    private final UUID sender;
    private final ItemStack item;
    private final int amount;
    private OperationStatus status;

    public NetworkOperation(OperationType type, UUID source, UUID destination,
                            UUID sender, ItemStack item, int amount) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.source = source;
        this.destination = destination;
        this.sender = sender;
        this.item = item;
        this.amount = amount;
        this.status = OperationStatus.PENDING;
    }
    // Getters
    public UUID getId() { return id; }
    public OperationType getType() { return type; }
    public UUID getSource() { return source; }
    public UUID getDestination() { return destination; }
    public UUID getSender() { return sender; }
    public ItemStack getItem() { return item; }
    public int getAmount() { return amount; }
    public OperationStatus getStatus() { return status; }
    public void setStatus(OperationStatus status) { this.status = status; }
}
