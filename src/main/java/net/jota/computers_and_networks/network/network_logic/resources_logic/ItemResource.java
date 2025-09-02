package net.jota.computers_and_networks.network.network_logic.resources_logic;

import net.jota.computers_and_networks.network.enums.ResourceType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class ItemResource extends NetworkResource{
    private final ItemStack itemStack;

    public ItemResource(ItemStack itemStack, UUID source, UUID destination) {
        super(ResourceType.ITEM, itemStack.getCount(), source, destination);
        this.itemStack = itemStack.copy();
    }

    @Override
    public ItemStack getResource() {
        return itemStack.copy();
    }

    @Override
    public NetworkResource copyWithAmount(int newAmount) {
        ItemStack newStack = itemStack.copy();
        newStack.setCount(newAmount);
        return new ItemResource(newStack, source, destination);
    }

    public Item getItem() { return itemStack.getItem(); }
}
