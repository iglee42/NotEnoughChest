package fr.iglee42.notenoughchests.datagen;

import fr.iglee42.notenoughchests.NotEnoughChests;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ItemModelsGenerator extends ItemModelProvider {
    public ItemModelsGenerator(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (String type : NotEnoughChests.PLANK_TYPES) {
            RegistryObject<Item> item = NotEnoughChests.ITEMS.getEntries().stream().filter(i->i.getId().getPath().equals(type.toLowerCase() + "_chest")).findFirst().orElse(null);
            if (item == null) continue;
            getBuilder(NotEnoughChests.MODID + ":"+type.toLowerCase()+"_chest").parent(new ModelFile.UncheckedModelFile(new ResourceLocation(NotEnoughChests.MODID , "item/chest_base"))).texture("wood_type","#"+ NotEnoughChests.MODID + ":entity/chest/"+type.toLowerCase());
        }
    }
}
