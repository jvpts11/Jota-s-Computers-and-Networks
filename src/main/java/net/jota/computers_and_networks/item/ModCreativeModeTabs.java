package net.jota.computers_and_networks.item;

import net.jota.computers_and_networks.Computers_and_Networks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Computers_and_Networks.MOD_ID);

    public static final Supplier<CreativeModeTab> COMPUTERS_AND_NETWORKS_COMPONENTS_TAB = CREATIVE_MODE_TAB.register(
            "computers_and_networks_components_tab",
                () -> CreativeModeTab
                        .builder()
                        .icon(() -> new ItemStack(ModItems.CPU.get()))
                        .title(Component.translatable("creativetab.computers_and_networks.components"))
                        .displayItems((itemDisplayParameters, output) -> {
                            output.accept(ModItems.CPU);
                            output.accept(ModItems.MEMORY);
                            output.accept(ModItems.HARD_DRIVE);
                            output.accept(ModItems.MOTHERBOARD);
                        })
                        .build()
    );

    public static final Supplier<CreativeModeTab> COMPUTERS_AND_NETWORKS_DEVICES_TAB = CREATIVE_MODE_TAB.register(
            "computers_and_networks_devices_tab",
            () -> CreativeModeTab
                    .builder()
                    .icon(() -> new ItemStack(ModItems.CPU.get()))
                    .title(Component.translatable("creativetab.computers_and_networks.devices"))
                    .displayItems((itemDisplayParameters, output) -> {

                    })
                    .build()
    );

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
