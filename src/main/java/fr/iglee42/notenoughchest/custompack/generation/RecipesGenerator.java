package fr.iglee42.notenoughchest.custompack.generation;

import fr.iglee42.notenoughchest.NotEnoughChest;
import fr.iglee42.notenoughchest.custompack.PathConstant;

import java.io.File;
import java.io.FileWriter;

public class RecipesGenerator {
    public static void generate() {
        NotEnoughChest.WOOD_TYPES.stream().forEach(rs->{
            if (!rs.getNamespace().equals("minecraft")){
                chest(rs.getNamespace(),rs.getPath());
            }
        });
    }

    private static void chest(String modid,String name){
        try {
            FileWriter writer = new FileWriter(new File(PathConstant.RECIPES_PATH.toFile(), name+"_chest.json"));
            writer.write("{\n" +
                    "  \"type\": \"minecraft:crafting_shaped\",\n" +
                    "  \"pattern\": [\n" +
                    "    \"###\",\n" +
                    "    \"# #\",\n" +
                    "    \"###\"\n" +
                    "  ],\n" +
                    "  \"key\": {\n" +
                    "    \"#\": {\n" +
                    "      \"item\": \""+modid+":"+name+"_planks\"\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"result\": {\n" +
                    "    \"item\": \"nec:"+name+"_chest\"\n" +
                    "  }\n" +
                    "}");
            writer.close();
        } catch (Exception exception){
            NotEnoughChest.LOGGER.error("An error was detected when recipes generating",exception);
        }
    }
}
