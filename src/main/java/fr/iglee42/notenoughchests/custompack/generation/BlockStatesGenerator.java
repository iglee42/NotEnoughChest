package fr.iglee42.notenoughchests.custompack.generation;

import fr.iglee42.notenoughchests.NotEnoughChests;
import fr.iglee42.notenoughchests.custompack.PathConstant;
import fr.iglee42.notenoughchests.utils.ModAbbreviation;

import java.io.File;
import java.io.FileWriter;

import static fr.iglee42.notenoughchests.NotEnoughChests.MODID;

public class BlockStatesGenerator {
    public static void generate() {
        NotEnoughChests.WOOD_TYPES.forEach(wt->{
            String t = wt.getPath();
            blockState(ModAbbreviation.getModAbbrevation(wt.getNamespace()) + t+"_chest");
            blockState(ModAbbreviation.getModAbbrevation(wt.getNamespace()) + t+"_trapped_chest");
        });
    }
    private static void blockState(String name){
        try {
            FileWriter writer = new FileWriter(new File(PathConstant.BLOCK_STATES_PATH.toFile(), name+".json"));
            writer.write("{\n" +
                    "  \"variants\": {\n" +
                    "    \"\": {\n" +
                    "      \"model\": \""+MODID+":block/"+name+"\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}");
            writer.close();
        } catch (Exception exception){
            NotEnoughChests.LOGGER.error("An error was detected when blockstates generating",exception);
        }
    }
}
