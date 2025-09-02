package net.jota.computers_and_networks.network.network_logic.resources_logic;

import net.jota.computers_and_networks.network.enums.ResourceType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.UUID;

public class FluidResource extends NetworkResource{
    private final FluidStack fluidStack;

    public FluidResource(FluidStack fluidStack, UUID source, UUID destination) {
        super(ResourceType.FLUID, fluidStack.getAmount(), source, destination);
        this.fluidStack = fluidStack.copy();
    }

    @Override
    public FluidStack getResource() {
        return fluidStack.copy();
    }

    @Override
    public NetworkResource copyWithAmount(int newAmount) {
        FluidStack newStack = new FluidStack(fluidStack.getFluid(), newAmount);
        return new FluidResource(newStack, source, destination);
    }

    public Fluid getFluid() { return fluidStack.getFluid(); }
    public CompoundTag getTag() { return (CompoundTag) fluidStack.getTags(); }
}
