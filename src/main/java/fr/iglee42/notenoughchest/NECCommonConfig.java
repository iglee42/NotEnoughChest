package fr.iglee42.notenoughchest;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

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
