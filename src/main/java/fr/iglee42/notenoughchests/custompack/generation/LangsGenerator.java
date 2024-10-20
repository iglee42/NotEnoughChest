package fr.iglee42.notenoughchests.custompack.generation;

import fr.iglee42.igleelib.api.utils.ModsUtils;
import fr.iglee42.notenoughchests.NotEnoughChests;
import fr.iglee42.notenoughchests.custompack.PathConstant;
import fr.iglee42.notenoughchests.utils.ModAbbreviation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static fr.iglee42.notenoughchests.NotEnoughChests.MODID;

public class LangsGenerator {

    private static Map<String,String> langs = new HashMap<>();
    public static void generate() {

        NotEnoughChests.WOOD_TYPES.forEach(wt->{
            String t = wt.getPath();
            langs.put("block."+MODID+"."+ ModAbbreviation.getModAbbrevation(wt.getNamespace()) + t+"_chest", ModsUtils.getUpperName(t+"_chest","_"));
            langs.put("block."+MODID+"."+ ModAbbreviation.getModAbbrevation(wt.getNamespace()) + t+"_trapped_chest", ModsUtils.getUpperName(t+"_trapped_chest","_"));
        });

        try {
            FileWriter writer = new FileWriter(new File(PathConstant.LANGS_PATH.toFile(), "en_us.json"));
            writer.write("{\n");
            AtomicInteger index = new AtomicInteger(-1);
            langs.forEach((key,translation) -> {
                try {
                    index.getAndIncrement();
                    writer.write("  \"" + key + "\": \"" + translation + "\"" + (index.get() != langs.size() - 1? ",":"") + "\n");

                } catch (IOException e) {
                    NotEnoughChests.LOGGER.error("An error was detected when langs generating",e);
                }
            });
            writer.write("}");
            writer.close();
        } catch (Exception exception){
            NotEnoughChests.LOGGER.error("An error was detected when langs generating",exception);
        }
    }

}
