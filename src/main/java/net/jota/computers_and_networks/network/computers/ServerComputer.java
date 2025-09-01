package net.jota.computers_and_networks.network.computers;

import net.jota.computers_and_networks.block.custom.enums.ComputerType;
import net.jota.computers_and_networks.network.LogisticNetwork;
import net.jota.computers_and_networks.network.NetworkComputer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ServerComputer extends NetworkComputer {
    private final ItemStackHandler storage;

    public ServerComputer() {
        super(ComputerType.SERVER, "Server");
        this.storage = new ItemStackHandler(getStorageCapacity());
    }

    @Override
    public boolean canJoinNetwork(LogisticNetwork network) {
        if (!network.isValid()) return false;

        return hasMinimumComponents();
    }

    private boolean hasMinimumComponents() {
        return !getComponents().getCpus().isEmpty() &&
                !getComponents().getRams().isEmpty() &&
                !getComponents().getHdds().isEmpty() &&
                getComponents().getMotherboard() != null;
    }

    @Override
    public void onNetworkJoin(LogisticNetwork network) {
        this.networkId = network.getId();
        System.out.println("Server Connected to Network: " + network.getId());
    }

    @Override
    public void onNetworkLeave() {
        System.out.println("Server Disconnected from Network: " + networkId);
        this.networkId = null;
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return storage.extractItem(slot, amount, simulate);
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return storage.insertItem(slot, stack, simulate);
    }

    public ItemStack getStackInSlot(int slot) {
        return storage.getStackInSlot(slot);
    }

    public int getSlots() {
        return storage.getSlots();
    }

    public boolean hasItem(Item item) {
        for (int i = 0; i < storage.getSlots(); i++) {
            if (storage.getStackInSlot(i).getItem() == item) {
                return true;
            }
        }
        return false;
    }

    public int getItemCount(Item item) {
        int count = 0;
        for (int i = 0; i < storage.getSlots(); i++) {
            ItemStack stack = storage.getStackInSlot(i);
            if (stack.getItem() == item) {
                count += stack.getCount();
            }
        }
        return count;
    }
}
