package fr.iglee42.notenoughchests.datagen;

import fr.iglee42.notenoughchests.NotEnoughChests;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class BlockStatesGenerator extends BlockStateProvider {
    public BlockStatesGenerator(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
        super(output, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (String type : NotEnoughChests.PLANK_TYPES) {
            RegistryObject<Block> block = NotEnoughChests.BLOCKS.getEntries().stream().filter(i->i.getId().getPath().equals(type.toLowerCase() + "_chest")).findFirst().orElse(null);
            if (block == null) continue;
            models().withExistingParent(NotEnoughChests.MODID + ":"+type.toLowerCase()+"_chest","block/chest").texture("particle","#biomesoplenty:block/"+type.toLowerCase() + "_planks");
            getVariantBuilder(block.get()).forAllStates(bs->ConfiguredModel.builder().modelFile(new ModelFile.UncheckedModelFile(NotEnoughChests.MODID + ":block/"+type.toLowerCase()+"_chest")).build());
        }
    }
}
