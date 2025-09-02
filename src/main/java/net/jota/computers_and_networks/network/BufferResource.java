package net.jota.computers_and_networks.network;

import net.jota.computers_and_networks.network.enums.ResourceType;
import net.jota.computers_and_networks.network.network_logic.resources_logic.FluidResource;
import net.jota.computers_and_networks.network.network_logic.resources_logic.ItemResource;
import net.jota.computers_and_networks.network.network_logic.resources_logic.NetworkResource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.UUID;

public class BufferResource {
    private final UUID operationId;
    private final NetworkResource resource;
    private int transferredAmount;
    private boolean transferComplete;

    public BufferResource(UUID operationId, NetworkResource resource) {
        this.operationId = operationId;
        this.resource = resource;
        this.transferredAmount = 0;
        this.transferComplete = false;
    }

    public void transfer(int amount) {
        transferredAmount += amount;
        if (transferredAmount >= resource.getAmount()) {
            transferComplete = true;
            transferredAmount = resource.getAmount();
        }
    }

    public boolean isTransferComplete() { return transferComplete; }
    public int getRemainingAmount() { return resource.getAmount() - transferredAmount; }
    public NetworkResource getResource() { return resource; }

    public ItemStack getItemStack() {
        if (resource.getType() == ResourceType.ITEM) {
            ItemResource itemResource = (ItemResource) resource;
            ItemStack stack = itemResource.getResource();
            stack.setCount(Math.min(stack.getCount(), getRemainingAmount()));
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public FluidStack getFluidStack() {
        if (resource.getType() == ResourceType.FLUID) {
            FluidResource fluidResource = (FluidResource) resource;
            FluidStack stack = fluidResource.getResource();
            stack.setAmount(Math.min(stack.getAmount(), getRemainingAmount()));
            return stack;
        }
        return FluidStack.EMPTY;
    }
}
