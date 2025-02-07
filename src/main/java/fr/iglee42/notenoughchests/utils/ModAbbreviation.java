package fr.iglee42.notenoughchests.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import fr.iglee42.notenoughchests.NotEnoughChests;
import fr.iglee42.notenoughchests.custompack.PathConstant;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ModAbbreviation {

    private static final Map<String,String> abbreviations = new HashMap<>();

    public static final File BACKUP_FILE = new File(FMLPaths.CONFIGDIR.get().resolve("nec").toFile(), "abbreviations_backup.json");

    public static boolean API_ONLINE = true;

    public static void init(){
        try {
            URL url = URI.create("https://iglee.fr:3000/modAbbreviations").toURL();
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(1000);
            con.setInstanceFollowRedirects(false);
            int status = con.getResponseCode();
            if (status == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder json = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    try {
                        json.append(inputLine);
                    } catch (Exception ignored) {
                    }
                }
                JsonObject obj = new Gson().fromJson(json.toString(), JsonObject.class);
                obj.entrySet().forEach(e -> {
                    abbreviations.put(e.getKey(), e.getValue().getAsString());
                });

                new File(BACKUP_FILE.getParent()).mkdirs();
                if (BACKUP_FILE.exists()) BACKUP_FILE.delete();
                try (FileWriter fw = new FileWriter(BACKUP_FILE)) {
                    fw.write(new GsonBuilder().setPrettyPrinting().create().toJson(obj));
                } catch (IOException ignored) {
                }

                in.close();
                con.disconnect();
            }
        } catch (Exception ignored) {
            API_ONLINE = false;
            NotEnoughChests.LOGGER.error("API isn't online, some chests may not work correctly !");
            loadBackup();
        }
    }

    public static void loadBackup(){
        abbreviations.clear();
        if (BACKUP_FILE.exists()){
            try (BufferedReader reader = new BufferedReader(new FileReader(BACKUP_FILE))){
                StringBuilder json = new StringBuilder();
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    try {
                        json.append(inputLine);
                    } catch (Exception ignored) {}
                }
                JsonObject obj = new Gson().fromJson(json.toString(), JsonObject.class);
                obj.entrySet().forEach(e->{
                    abbreviations.put(e.getKey(),e.getValue().getAsString());
                });
                NotEnoughChests.LOGGER.info("Abbreviations loaded with backup file ! ");
                return;
            } catch (Exception ignored){}
        }

        abbreviations.putAll(Map.of(
                "biomesoplenty", "bop",
                "biomeyoullgo", "byg",
                "biomeswevegone", "bwg",
                "regions_unexplored", "rgun",
                "caveopolis", "caveop",
                "pokecube", "pokecube",
                "pokecube_legends", "pokecube",
                "productivetrees", "pt"
        ));
        NotEnoughChests.LOGGER.info("Abbreviation's Backup File not found loaded hardcoded abbreviations ! ");
    }



    public static String getChestTexture(ResourceLocation plankType){
        String abbrevModid = abbreviations.getOrDefault(plankType.getNamespace(), "");
        if (abbrevModid.isEmpty()){
            return plankType.getPath();
        } else {
            return abbrevModid+"/"+plankType.getPath();
        }
    }

    public static String getModAbbreviation(String modid){
        return abbreviations.containsKey(modid) ? abbreviations.get(modid)+"_":"";
    }


}
