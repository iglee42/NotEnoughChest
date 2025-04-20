package fr.iglee42.notenoughchests.custompack.generation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import fr.iglee42.notenoughchests.NotEnoughChests;
import fr.iglee42.notenoughchests.custompack.PathConstant;
import fr.iglee42.notenoughchests.utils.ModAbbreviation;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;

public class RecipesGenerator {
    public static void generate() {
        NotEnoughChests.WOOD_TYPES.stream().forEach(rs->{
            if (!rs.equals(ResourceLocation.withDefaultNamespace("oak")))chest(rs.getNamespace(),rs.getPath());
            trappedChest(rs.getNamespace(),rs.getPath());
            logChest(rs.getNamespace(),rs.getPath());
        });
    }

    private static void trappedChest(String modid, String name) {
        String recipe;
        try {
            if (!PathConstant.TRAPPED_CHEST_RECIPE_PATH.toFile().exists()) {
                FileWriter writer = new FileWriter(PathConstant.TRAPPED_CHEST_RECIPE_PATH.toFile());
                recipe = """
                        {
                          "type": "minecraft:crafting_shapeless",
                          "category": "redstone",
                          "ingredients": [
                            {
                               "item": "nec:${type}_chest"
                            },
                            {
                              "item": "minecraft:tripwire_hook"
                            }
                          ],
                          "result": {
                            "id": "nec:${type}_trapped_chest"
                          }
                        }""";
                writer.write(recipe);
                writer.close();

            } else {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonObject object = gson.fromJson(new FileReader(PathConstant.TRAPPED_CHEST_RECIPE_PATH.toFile()), JsonObject.class);
                recipe = gson.toJson(object);
            }
            recipe(modid,name,recipe,"_trapped_chest.json");
        } catch (Exception exception) {
            NotEnoughChests.LOGGER.error("An error was detected when recipes generating", exception);
        }
    }


    private static void chest(String modid, String name) {
        String recipe;
        try {
            if (!PathConstant.CHEST_RECIPE_PATH.toFile().exists()) {
                FileWriter writer = new FileWriter(PathConstant.CHEST_RECIPE_PATH.toFile());
                recipe = """
                        {
                          "type": "minecraft:crafting_shaped",
                          "pattern": [
                            "###",
                            "# #",
                            "###"
                          ],
                          "key": {
                            "#": {
                              "item": "${planks}"
                            }
                          },
                          "result": {
                            "id": "nec:${type}_chest"
                          }
                        }""";
                writer.write(recipe);
                writer.close();

            } else {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonObject object = gson.fromJson(new FileReader(PathConstant.CHEST_RECIPE_PATH.toFile()), JsonObject.class);
                recipe = gson.toJson(object);
            }
            recipe(modid,name,recipe,"_chest.json");
        } catch (Exception exception){
            NotEnoughChests.LOGGER.error("An error was detected when recipes generating",exception);
        }
    }
    private static void logChest(String modid, String name) {
        String recipe;
        try {
            if (!PathConstant.LOG_CHEST_RECIPE_PATH.toFile().exists()) {
                FileWriter writer = new FileWriter(PathConstant.LOG_CHEST_RECIPE_PATH.toFile());
                recipe = """
                        {
                          "type": "minecraft:crafting_shaped",
                          "pattern": [
                            "###",
                            "# #",
                            "###"
                          ],
                          "key": {
                            "#": {
                              "item": "${modid}:${wood}_log"
                            }
                          },
                          "result": {
                            "id": "nec:${type}_chest",
                            "count": 4
                          }
                        }""";
                writer.write(recipe);
                writer.close();

            } else {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonObject object = gson.fromJson(new FileReader(PathConstant.LOG_CHEST_RECIPE_PATH.toFile()), JsonObject.class);
                recipe = gson.toJson(object);
            }
            recipe(modid,name,recipe,"_chest_from_log.json");
        } catch (Exception exception) {
            NotEnoughChests.LOGGER.error("An error was detected when recipes generating", exception);
        }
    }
    private static void recipe(String modid, String name, String recipe, String fileSuffix) throws Exception {
        recipe = replaceVariables(recipe,modid,name,ModAbbreviation.getModAbbreviation(modid));
        FileWriter writer = new FileWriter(new File(PathConstant.RECIPES_PATH.toFile(), ModAbbreviation.getModAbbreviation(modid) + name + fileSuffix));
        writer.write(recipe);
        writer.close();
    }

    private static String replaceVariables(String recipe,String modid,String wood, String abbreviation) {
        return recipe.replace("${type}",abbreviation + wood)
                .replace("${abbreviation}",abbreviation)
                .replace("${modid}",modid)
                .replace("${wood}",wood)
                .replace("${planks}", modid + ":" + getPrefix(ResourceLocation.fromNamespaceAndPath(modid, wood)) + wood + getSuffix(ResourceLocation.fromNamespaceAndPath(modid, wood)));
    }

    private static String getPrefix(ResourceLocation rs) {
        return NotEnoughChests.PLANK_NAME_FORMAT.get(rs).endsWith("_") ? NotEnoughChests.PLANK_NAME_FORMAT.get(rs) : "";
    }
    private static String getSuffix(ResourceLocation rs) {
        return NotEnoughChests.PLANK_NAME_FORMAT.get(rs).startsWith("_") ? NotEnoughChests.PLANK_NAME_FORMAT.get(rs) : "";
    }
}
