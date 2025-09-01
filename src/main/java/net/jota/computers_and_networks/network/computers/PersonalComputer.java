package net.jota.computers_and_networks.network.computers;

import net.jota.computers_and_networks.block.custom.enums.ComputerType;
import net.jota.computers_and_networks.network.LogisticNetwork;
import net.jota.computers_and_networks.network.NetworkComputer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class PersonalComputer extends NetworkComputer {
    private final ItemStackHandler localStorage;

    public PersonalComputer() {
        super(ComputerType.PERSONAL_COMPUTER, "Personal Computer");
        this.localStorage = new ItemStackHandler(27);
    }

    @Override
    public boolean canJoinNetwork(LogisticNetwork network) {
        if (!network.isValid()) return false;

        return hasMinimumComponents();
    }

    private boolean hasMinimumComponents() {
        return !getComponents().getCpus().isEmpty() &&
                !getComponents().getRams().isEmpty() &&
                getComponents().getMotherboard() != null;
    }

    @Override
    public void onNetworkJoin(LogisticNetwork network) {
        this.networkId = network.getId();
        System.out.println("PC connected to Network: " + network.getId());
    }

    @Override
    public void onNetworkLeave() {
        System.out.println("PC disconnected from Network: " + networkId);
        this.networkId = null;
    }

    public ItemStack getLocalStack(int slot) {
        return localStorage.getStackInSlot(slot);
    }

    public void setLocalStack(int slot, ItemStack stack) {
        localStorage.setStackInSlot(slot, stack);
    }

    public ItemStack insertLocalItem(int slot, ItemStack stack, boolean simulate) {
        return localStorage.insertItem(slot, stack, simulate);
    }

    public ItemStack extractLocalItem(int slot, int amount, boolean simulate) {
        return localStorage.extractItem(slot, amount, simulate);
    }
}
