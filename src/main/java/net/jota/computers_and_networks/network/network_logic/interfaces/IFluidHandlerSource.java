package net.jota.computers_and_networks.network.network_logic.interfaces;

import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public interface IFluidHandlerSource {
    FluidStack extractFluid(FluidStack fluid, int amount, boolean simulate);
    boolean canExtractFluid(Fluid fluid);
    int getFluidAmount(Fluid fluid);
}
