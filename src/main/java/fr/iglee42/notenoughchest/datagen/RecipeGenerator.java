package fr.iglee42.notenoughchest.datagen;

import fr.iglee42.notenoughchest.NotEnoughChest;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;
import java.util.function.Consumer;

public class RecipeGenerator extends RecipeProvider {


    public RecipeGenerator(PackOutput p_248933_) {
        super(p_248933_);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        for (String type : NotEnoughChest.PLANK_TYPES) {
            RegistryObject<Item> item = NotEnoughChest.ITEMS.getEntries().stream().filter(i->i.getId().getPath().equals(type.toLowerCase() + "_chest")).findFirst().orElse(null);
            Map.Entry<ResourceKey<Item>,Item> planksItem = ForgeRegistries.ITEMS.getEntries().stream().filter(i->i.getKey().location().getPath().equals(type.toLowerCase() + "_planks")).findFirst().orElse(null);
            if (item == null) continue;
            if (planksItem == null) continue;
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,item.get())
                    .pattern("###")
                    .pattern("# #")
                    .pattern("###")
                    .define('#', planksItem.getValue())
                    .unlockedBy("unlock", InventoryChangeTrigger.TriggerInstance.hasItems(planksItem.getValue()))
                    .save(consumer);
        }
    }
}
