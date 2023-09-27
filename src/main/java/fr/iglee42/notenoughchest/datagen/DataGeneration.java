package fr.iglee42.notenoughchest.datagen;


import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static fr.iglee42.notenoughchest.NotEnoughChest.MODID;

@Mod.EventBusSubscriber(modid = MODID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGeneration {

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent e){
        DataGenerator generator = e.getGenerator();
        ExistingFileHelper efh = e.getExistingFileHelper();

        generator.addProvider(e.includeServer(),new RecipeGenerator(generator.getPackOutput()));

        generator.addProvider(e.includeClient(),new ItemModelsGenerator(generator.getPackOutput(), MODID, efh));
        generator.addProvider(e.includeClient(), new BlockStatesGenerator(generator.getPackOutput(), MODID, efh));
        generator.addProvider(e.includeClient(), new LangGenerator(generator.getPackOutput(),MODID,"en_us"));
    }
}