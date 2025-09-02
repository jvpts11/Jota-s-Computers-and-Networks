package net.jota.computers_and_networks.datagen;

import net.jota.computers_and_networks.Computers_and_Networks;
import net.jota.computers_and_networks.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {


    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Computers_and_Networks.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.CPU.get());
        basicItem(ModItems.MEMORY.get());
        basicItem(ModItems.HARD_DRIVE.get());
        basicItem(ModItems.GPU.get());

        ModelFile motherboardModel = withExistingParent("motherboard_template", "item/generated")
                .texture("layer0", modLoc("item/motherboard"));

        getBuilder(ModItems.MAINFRAME_MOTHERBOARD.getId().getPath())
                .parent(motherboardModel);

        getBuilder(ModItems.SERVER_MOTHERBOARD.getId().getPath())
                .parent(motherboardModel);

        getBuilder(ModItems.PERSONAL_COMPUTER_MOTHERBOARD.getId().getPath())
                .parent(motherboardModel);
    }
}
