package fr.iglee42.notenoughchest.custompack.generation;

import fr.iglee42.notenoughchest.NotEnoughChest;
import fr.iglee42.notenoughchest.custompack.PathConstant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static fr.iglee42.notenoughchest.NotEnoughChest.MODID;

public class LangsGenerator {

    private static Map<String,String> langs = new HashMap<>();
    public static void generate() {

        NotEnoughChest.WOOD_TYPES.forEach(wt->{
            if (!wt.getNamespace().equals("minecraft")) {
                String t = wt.getPath();
                langs.put("block."+MODID+"."+t+"_chest",getUpperName(t+"_chest","_"));
            }
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
                    NotEnoughChest.LOGGER.error("An error was detected when langs generating",e);
                }
            });
            writer.write("}");
            writer.close();
        } catch (Exception exception){
            NotEnoughChest.LOGGER.error("An error was detected when langs generating",exception);
        }
    }

    private static String[] split(String base,String separator){
        String[] st = base.split(separator);
        String[] finale = new String[st.length];
        int i = 0;
        for (String s : st){
            String fi = s;
            if (s.endsWith(separator))fi = s.substring(s.length()-1);
            finale[i] = fi;
            i += 1;
        }
        return finale;
    }

    private static String getUpperName(String name,String wordSeparator) {
        String[] nm = split(name,wordSeparator);
        StringBuilder end = new StringBuilder();
        int i = 0;
        for (String n : nm) {
            i += 1;
            char fc = n.charAt(0);
            String fcs = String.valueOf(fc);
            String fs = fcs.toUpperCase() + n.substring(1);
            end.append(fs).append(i == nm.length ? "" : " ");
        }

        return end.toString();
    }
}
