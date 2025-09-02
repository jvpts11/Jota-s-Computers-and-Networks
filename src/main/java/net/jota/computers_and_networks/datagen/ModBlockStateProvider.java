package net.jota.computers_and_networks.datagen;

import net.jota.computers_and_networks.Computers_and_Networks;
import net.jota.computers_and_networks.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Computers_and_Networks.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        registerMainframe();
    }

    private void registerMainframe(){
        ModelFile mainframeModel = models().cube(ModBlocks.MAINFRAME_BLOCK.getId().getPath(),
                modLoc("block/mainframe_block/mainframe_bottom"),
                modLoc("block/mainframe_block/mainframe_top"),
                modLoc("block/mainframe_block/mainframe_front"),
                modLoc("block/mainframe_block/mainframe_back"),
                modLoc("block/mainframe_block/mainframe_side"),
                modLoc("block/mainframe_block/mainframe_side")
        ).texture("particle", modLoc("block/mainframe_block/mainframe_side"));

        simpleBlockWithItem(ModBlocks.MAINFRAME_BLOCK.get(), mainframeModel);
    }
}
