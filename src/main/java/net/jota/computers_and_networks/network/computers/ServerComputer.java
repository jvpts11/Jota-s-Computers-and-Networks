package net.jota.computers_and_networks.network.computers;

import net.jota.computers_and_networks.block.custom.enums.ComputerType;
import net.jota.computers_and_networks.network.LogisticNetwork;
import net.jota.computers_and_networks.network.NetworkComputer;
import net.jota.computers_and_networks.network.network_logic.interfaces.IFluidHandlerDestination;
import net.jota.computers_and_networks.network.network_logic.interfaces.IFluidHandlerSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ServerComputer extends NetworkComputer implements IFluidHandlerSource, IFluidHandlerDestination {
    private final ItemStackHandler itemStorage;
    private final List<FluidTank> fluidTanks;

    public ServerComputer() {
        super(ComputerType.SERVER, "Server");
        this.itemStorage = new ItemStackHandler(getItemStorageCapacity());
        this.fluidTanks = new ArrayList<>();

        initializeFluidTanks();
    }

    public int getItemStorageCapacity() {
        return getStorage().getTotalItemCapacity();
    }

    @Override
    public boolean canJoinNetwork(LogisticNetwork network) {
        if (!network.isValid()) return false;

        return hasMinimumComponents();
    }

    private boolean hasMinimumComponents() {
        return !getComponents().getCpus().isEmpty() &&
                !getComponents().getRams().isEmpty() &&
                !getStorage().getStorageDevices().isEmpty() && // Verifica se tem dispositivos de storage
                getComponents().getMotherboard() != null;
    }

    @Override
    public void onNetworkJoin(LogisticNetwork network) {
        setNetworkId(network.getId());
        System.out.println("Server Connected to Network: " + network.getId());
    }

    @Override
    public void onNetworkLeave() {
        System.out.println("Server Disconnected from Network: " + getNetworkId());
        setNetworkId(null);
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return itemStorage.extractItem(slot, amount, simulate);
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return itemStorage.insertItem(slot, stack, simulate);
    }

    public ItemStack getStackInSlot(int slot) {
        return itemStorage.getStackInSlot(slot);
    }

    public int getSlots() {
        return itemStorage.getSlots();
    }

    public boolean hasItem(Item item) {
        for (int i = 0; i < itemStorage.getSlots(); i++) {
            if (itemStorage.getStackInSlot(i).getItem() == item) {
                return true;
            }
        }
        return false;
    }

    public int getItemCount(Item item) {
        int count = 0;
        for (int i = 0; i < itemStorage.getSlots(); i++) {
            ItemStack stack = itemStorage.getStackInSlot(i);
            if (stack.getItem() == item) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private void initializeFluidTanks() {
        fluidTanks.clear();
        getStorage().getStorageDevices().stream()
                .filter(device -> device.getType().toString().contains("FLUID"))
                .forEach(device -> {
                    fluidTanks.add(new FluidTank(device.getCapacity()));
                });
    }

    @Override
    public FluidStack insertFluid(FluidStack fluid, boolean simulate) {
        FluidStack remaining = fluid.copy();

        for (FluidTank tank : fluidTanks) {
            if (!tank.isEmpty() && tank.getFluid().getFluid() == fluid.getFluid()) {
                int filled = tank.fill(remaining, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
                if (filled > 0) {
                    remaining.setAmount(remaining.getAmount() - filled);
                    if (remaining.isEmpty()) return FluidStack.EMPTY;
                }
            }
        }

        for (FluidTank tank : fluidTanks) {
            if (tank.isEmpty()) {
                int filled = tank.fill(remaining, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
                if (filled > 0) {
                    remaining.setAmount(remaining.getAmount() - filled);
                    if (remaining.isEmpty()) return FluidStack.EMPTY;
                }
            }
        }

        return remaining.isEmpty() ? FluidStack.EMPTY : remaining;
    }

    @Override
    public boolean canAcceptFluid(Fluid fluid) {
        return fluidTanks.stream()
                .anyMatch(tank -> tank.isEmpty() || tank.getFluid().getFluid() == fluid);
    }

    @Override
    public int getFluidCapacity(Fluid fluid) {
        return fluidTanks.stream()
                .filter(tank -> tank.isEmpty() || tank.getFluid().getFluid() == fluid)
                .mapToInt(FluidTank::getCapacity)
                .sum();
    }

    @Override
    public FluidStack extractFluid(FluidStack fluid, int amount, boolean simulate) {
        int remainingToExtract = amount;
        FluidStack extracted = FluidStack.EMPTY;

        for (FluidTank tank : fluidTanks) {
            if (!tank.isEmpty() && tank.getFluid().getFluid() == fluid.getFluid()) {
                FluidStack drained = tank.drain(remainingToExtract, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
                if (!drained.isEmpty()) {
                    if (extracted.isEmpty()) {
                        extracted = drained;
                    } else {
                        extracted.setAmount(extracted.getAmount() + drained.getAmount());
                    }
                    remainingToExtract -= drained.getAmount();
                    if (remainingToExtract <= 0) break;
                }
            }
        }

        return extracted;
    }

    @Override
    public boolean canExtractFluid(Fluid fluid) {
        return fluidTanks.stream()
                .anyMatch(tank -> tank.getFluid().getFluid() == fluid);
    }

    @Override
    public int getFluidAmount(Fluid fluid) {
        return fluidTanks.stream()
                .filter(tank -> tank.getFluid().getFluid() == fluid)
                .mapToInt(FluidTank::getFluidAmount)
                .sum();
    }

    public int getTotalFluidAmount(Fluid fluid) {
        return fluidTanks.stream()
                .filter(tank -> !tank.isEmpty() && tank.getFluid().getFluid() == fluid)
                .mapToInt(FluidTank::getFluidAmount)
                .sum();
    }

    public int getAvailableFluidCapacity(Fluid fluid) {
        return fluidTanks.stream()
                .filter(tank -> tank.isEmpty() || tank.getFluid().getFluid() == fluid)
                .mapToInt(tank -> {
                    if (tank.isEmpty()) {
                        return tank.getCapacity();
                    } else {
                        return tank.getCapacity() - tank.getFluidAmount();
                    }
                })
                .sum();
    }

    public boolean hasFluid(Fluid fluid) {
        return fluidTanks.stream()
                .anyMatch(tank -> !tank.isEmpty() && tank.getFluid().getFluid() == fluid);
    }

    public List<Fluid> getStoredFluids() {
        return fluidTanks.stream()
                .filter(tank -> !tank.isEmpty())
                .map(tank -> tank.getFluid().getFluid())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<FluidTank> getFluidTanks() {
        return Collections.unmodifiableList(fluidTanks);
    }

    public FluidTank getFluidTank(int index) {
        if (index >= 0 && index < fluidTanks.size()) {
            return fluidTanks.get(index);
        }
        return null;
    }

    public void refreshStorage() {
        int newCapacity = getItemStorageCapacity();
        if (newCapacity != itemStorage.getSlots()) {
            ItemStackHandler newStorage = new ItemStackHandler(newCapacity);
            for (int i = 0; i < Math.min(itemStorage.getSlots(), newCapacity); i++) {
                newStorage.setStackInSlot(i, itemStorage.getStackInSlot(i));
            }
            // TODO: We cant directly substitute the item, i must find a solution later on
        }

        initializeFluidTanks();
    }

    private int getFluidCapacity() {
        return getStorage().getTotalFluidCapacity();
    }
}
