package fr.iglee42.notenoughchest.custompack.generation;

import fr.iglee42.notenoughchest.NotEnoughChest;
import fr.iglee42.notenoughchest.custompack.PathConstant;

import java.io.File;
import java.io.FileWriter;

import static fr.iglee42.notenoughchest.NotEnoughChest.MODID;

public class BlockStatesGenerator {
    public static void generate() {
        NotEnoughChest.WOOD_TYPES.forEach(wt->{
            if (!wt.getNamespace().equals("minecraft")) {
                String t = wt.getPath();
                blockState(t+"_chest");
            }
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
            NotEnoughChest.LOGGER.error("An error was detected when blockstates generating",exception);
        }
    }
}
