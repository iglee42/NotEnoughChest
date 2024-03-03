package fr.iglee42.notenoughchests.custompack.generation;

import fr.iglee42.notenoughchests.NotEnoughChests;
import fr.iglee42.notenoughchests.custompack.PathConstant;
import fr.iglee42.notenoughchests.utils.ModAbbreviation;

import java.io.File;
import java.io.FileWriter;

public class LootTablesGenerator {
    public static void generate() {
        NotEnoughChests.WOOD_TYPES.stream().forEach(rs->{
            if (!rs.getNamespace().equals("minecraft")){
                chest(rs.getNamespace(),rs.getPath());
            }
        });
    }

    private static void chest(String modid,String name){
        try {
            FileWriter writer = new FileWriter(new File(PathConstant.LOOT_TABLES_PATH.toFile(), ModAbbreviation.getModAbbrevation(modid) + name+"_chest.json"));
            writer.write("{\n" +
                    "  \"type\": \"minecraft:block\",\n" +
                    "  \"pools\": [\n" +
                    "    {\n" +
                    "      \"bonus_rolls\": 0.0,\n" +
                    "      \"conditions\": [\n" +
                    "        {\n" +
                    "          \"condition\": \"minecraft:survives_explosion\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"entries\": [\n" +
                    "        {\n" +
                    "          \"type\": \"minecraft:item\",\n" +
                    "          \"name\": \"nec:"+ModAbbreviation.getModAbbrevation(modid) +name+"_chest\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"rolls\": 1.0\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"random_sequence\": \"nec:"+ModAbbreviation.getModAbbrevation(modid) +name+"_chest\"\n" +
                    "}");
            writer.close();
        } catch (Exception exception){
            NotEnoughChests.LOGGER.error("An error was detected when recipes generating",exception);
        }
    }
}
