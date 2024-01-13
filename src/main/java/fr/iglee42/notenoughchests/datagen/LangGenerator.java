package fr.iglee42.notenoughchests.datagen;

import fr.iglee42.notenoughchests.NotEnoughChests;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;

public class LangGenerator extends LanguageProvider {

    public LangGenerator(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        for (String type : NotEnoughChests.PLANK_TYPES) {
            RegistryObject<Block> block = NotEnoughChests.BLOCKS.getEntries().stream().filter(i->i.getId().getPath().equals(type.toLowerCase() + "_chest")).findFirst().orElse(null);
            if (block == null) continue;
            char firstChar = type.charAt(0);
            add(block.get(),firstChar + type.toLowerCase().substring(1) + " Chest");
        }
    }
}
