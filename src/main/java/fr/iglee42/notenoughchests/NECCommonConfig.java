package fr.iglee42.notenoughchests;

import com.google.gson.*;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NECCommonConfig {

    private static File configFile;

    public static List<String> modsBlacklist = Arrays.asList("chipped","ars_nouveau");




    public static void load() throws IOException {
        configFile = new File(FMLPaths.CONFIGDIR.get().toFile(),"notenoughchest-config.json");
        if (configFile.exists()){
            JsonObject config = new Gson().fromJson(new FileReader(configFile),JsonObject.class);
            modsBlacklist = config.getAsJsonArray("blacklistedMods").asList().stream().map(JsonElement::getAsString).collect(Collectors.toList());
        } else {
            JsonObject config = new JsonObject();
            JsonArray blacklist = new JsonArray();
            modsBlacklist.forEach(blacklist::add);
            config.add("blacklistedMods",blacklist);
            FileWriter writer = new FileWriter(configFile);
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(config));
            writer.close();
        }
    }
}
