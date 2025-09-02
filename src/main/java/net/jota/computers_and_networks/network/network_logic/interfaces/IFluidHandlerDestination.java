package net.jota.computers_and_networks.network.network_logic.interfaces;

import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public interface IFluidHandlerDestination {
    FluidStack insertFluid(FluidStack fluid, boolean simulate);
    boolean canAcceptFluid(Fluid fluid);
    int getFluidCapacity(Fluid fluid);
}
