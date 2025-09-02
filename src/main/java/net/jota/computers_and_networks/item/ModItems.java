package net.jota.computers_and_networks.item;

import net.jota.computers_and_networks.Computers_and_Networks;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Computers_and_Networks.MOD_ID);

    public static final DeferredItem<Item> CPU = ITEMS.register("cpu",
            ()->new Item(new Item.Properties()));

    public static final DeferredItem<Item> MEMORY = ITEMS.register("memory",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> HARD_DRIVE = ITEMS.register("hard_drive",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> MAINFRAME_MOTHERBOARD = ITEMS.register("mainframe_motherboard",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SERVER_MOTHERBOARD = ITEMS.register("server_motherboard",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> PERSONAL_COMPUTER_MOTHERBOARD = ITEMS.register("personal_computer_motherboard",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
