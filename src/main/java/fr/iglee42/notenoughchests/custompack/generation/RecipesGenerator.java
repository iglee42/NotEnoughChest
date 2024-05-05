package fr.iglee42.notenoughchests.custompack.generation;

import fr.iglee42.notenoughchests.NotEnoughChests;
import fr.iglee42.notenoughchests.custompack.PathConstant;
import fr.iglee42.notenoughchests.utils.ModAbbreviation;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.FileWriter;

public class RecipesGenerator {
    public static void generate() {
        NotEnoughChests.WOOD_TYPES.stream().forEach(rs->{
            if (!rs.equals(new ResourceLocation("oak")))chest(rs.getNamespace(),rs.getPath());
            trappedChest(rs.getNamespace(),rs.getPath());
        });
    }
    private static void trappedChest(String modid,String name){
        try {
            FileWriter writer = new FileWriter(new File(PathConstant.RECIPES_PATH.toFile(), ModAbbreviation.getModAbbrevation(modid)+name+"_trapped_chest.json"));
            writer.write("{\n" +
                    "  \"type\": \"minecraft:crafting_shapeless\",\n" +
                    "  \"category\": \"redstone\",\n" +
                    "  \"ingredients\": [\n" +
                    "    {\n" +
                    "       \"item\": \"nec:"+ModAbbreviation.getModAbbrevation(modid)+name+"_chest\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"item\": \"minecraft:tripwire_hook\"\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"result\": {\n" +
                    "    \"item\": \"nec:"+ModAbbreviation.getModAbbrevation(modid)+name+"_trapped_chest\"\n" +
                    "  }\n" +
                    "}");


                    writer.close();
        } catch (Exception exception){
            NotEnoughChests.LOGGER.error("An error was detected when recipes generating",exception);
        }
    }


    private static void chest(String modid,String name){
        try {
            FileWriter writer = new FileWriter(new File(PathConstant.RECIPES_PATH.toFile(), ModAbbreviation.getModAbbrevation(modid)+name+"_chest.json"));
            writer.write("{\n" +
                    "  \"type\": \"minecraft:crafting_shaped\",\n" +
                    "  \"pattern\": [\n" +
                    "    \"###\",\n" +
                    "    \"# #\",\n" +
                    "    \"###\"\n" +
                    "  ],\n" +
                    "  \"key\": {\n" +
                    "    \"#\": {\n" +
                    "      \"item\": \""+modid+":"+getPrefix(new ResourceLocation(modid,name))+name+getSuffix(new ResourceLocation(modid,name))+"\"\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"result\": {\n" +
                    "    \"item\": \"nec:"+ModAbbreviation.getModAbbrevation(modid)+name+"_chest\"\n" +
                    "  }\n" +
                    "}");
            writer.close();
        } catch (Exception exception){
            NotEnoughChests.LOGGER.error("An error was detected when recipes generating",exception);
        }
    }

    private static String getPrefix(ResourceLocation rs){
        return NotEnoughChests.PLANK_NAME_FORMAT.get(rs).endsWith("_") ? NotEnoughChests.PLANK_NAME_FORMAT.get(rs) : "";
    }
    private static String getSuffix(ResourceLocation rs){
        return NotEnoughChests.PLANK_NAME_FORMAT.get(rs).startsWith("_") ? NotEnoughChests.PLANK_NAME_FORMAT.get(rs) : "";
    }
}
