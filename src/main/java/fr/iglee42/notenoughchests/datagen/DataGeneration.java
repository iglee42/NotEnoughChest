package fr.iglee42.notenoughchests.datagen;


import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import static fr.iglee42.notenoughchests.NotEnoughChests.MODID;

@Mod.EventBusSubscriber(modid = MODID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGeneration {

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent e){
        DataGenerator generator = e.getGenerator();
        ExistingFileHelper efh = e.getExistingFileHelper();

        generator.addProvider(new RecipeGenerator(generator));

        generator.addProvider(new ItemModelsGenerator(generator, MODID, efh));
        generator.addProvider(new BlockStatesGenerator(generator, MODID, efh));
        generator.addProvider( new LangGenerator(generator,MODID,"en_us"));
    }
}