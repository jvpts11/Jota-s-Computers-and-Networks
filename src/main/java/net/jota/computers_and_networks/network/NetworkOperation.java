package net.jota.computers_and_networks.network;

import net.jota.computers_and_networks.network.enums.OperationStatus;
import net.jota.computers_and_networks.network.enums.OperationType;
import net.jota.computers_and_networks.network.enums.ResourceType;
import net.jota.computers_and_networks.network.network_logic.resources_logic.FluidResource;
import net.jota.computers_and_networks.network.network_logic.resources_logic.ItemResource;
import net.jota.computers_and_networks.network.network_logic.resources_logic.NetworkResource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.UUID;

public class NetworkOperation {
    private final UUID id;
    private final OperationType type;
    private final UUID source;
    private final UUID destination;
    private final UUID sender;
    private final NetworkResource resource;
    private OperationStatus status;
    private String failureReason;

    public NetworkOperation(OperationType type, UUID source, UUID destination,
                            UUID sender, NetworkResource resource) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.source = source;
        this.destination = destination;
        this.sender = sender;
        this.resource = resource;
        this.status = OperationStatus.PENDING;
        this.failureReason = null;
    }

    public static NetworkOperation createItemTransfer(ItemStack item, UUID source,
                                                      UUID destination, UUID sender) {
        return new NetworkOperation(OperationType.ITEM_TRANSFER, source, destination,
                sender, new ItemResource(item, source, destination));
    }

    public static NetworkOperation createFluidTransfer(FluidStack fluid, UUID source,
                                                       UUID destination, UUID sender) {
        return new NetworkOperation(OperationType.FLUID_TRANSFER, source, destination,
                sender, new FluidResource(fluid, source, destination));
    }

    public void setFailureReason(String reason) {
        this.failureReason = reason;
    }

    // Getters
    public ResourceType getResourceType() { return resource.getType(); }
    public ItemStack getItem() {
        return resource.getType() == ResourceType.ITEM ?
                ((ItemResource) resource).getResource() : ItemStack.EMPTY;
    }
    public FluidStack getFluid() {
        return resource.getType() == ResourceType.FLUID ?
                ((FluidResource) resource).getResource() : FluidStack.EMPTY;
    }

    public String getFailureReason() {return failureReason;}
    public UUID getId() { return id; }
    public OperationType getType() { return type; }
    public UUID getSource() { return source; }
    public UUID getDestination() { return destination; }
    public UUID getSender() { return sender; }
    public NetworkResource getResource(){return resource;};
    public OperationStatus getStatus() { return status; }
    public void setStatus(OperationStatus status) { this.status = status; }
}
