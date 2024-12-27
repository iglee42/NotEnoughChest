package fr.iglee42.notenoughchests.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fr.iglee42.notenoughchests.NotEnoughChests;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ModAbbreviation {

    private static final Map<String,String> abbreviations = new HashMap<>();

    public static boolean API_ONLINE = true;

    public static void init() throws IOException {
        URL url = new URL("https://iglee.fr:3000/modAbbreviations");
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
                } catch (Exception exception) {}
            }
            JsonObject obj = new Gson().fromJson(json.toString(), JsonObject.class);
            obj.entrySet().forEach(e->{
                abbreviations.put(e.getKey(),e.getValue().getAsString());
            });
            in.close();
        } else {
            API_ONLINE = false;
        }
        con.disconnect();
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
