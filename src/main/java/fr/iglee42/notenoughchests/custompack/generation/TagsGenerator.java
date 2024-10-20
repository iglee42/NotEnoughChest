package fr.iglee42.notenoughchests.custompack.generation;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.iglee42.notenoughchests.NotEnoughChests;
import fr.iglee42.notenoughchests.custompack.PathConstant;
import fr.iglee42.notenoughchests.utils.ModAbbreviation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static fr.iglee42.notenoughchests.NotEnoughChests.MODID;

public class TagsGenerator {

    private static List<String> chests = new ArrayList<>();
    private static List<String> onlyChests = new ArrayList<>();
    private static List<String> onlyTrapped = new ArrayList<>();
    public static void generate() {

        NotEnoughChests.WOOD_TYPES.forEach(wt->{
            String t = wt.getPath();
            chests.add(MODID+":"+ModAbbreviation.getModAbbrevation(wt.getNamespace()) + t+"_chest");
            onlyChests.add(MODID+":"+ModAbbreviation.getModAbbrevation(wt.getNamespace()) + t+"_chest");
            chests.add(MODID+":"+ModAbbreviation.getModAbbrevation(wt.getNamespace()) + t+"_trapped_chest");
            onlyTrapped.add(MODID+":"+ModAbbreviation.getModAbbrevation(wt.getNamespace()) + t+"_trapped_chest");
        });

        try {
            JsonObject tag = new JsonObject();
            tag.addProperty("replace",false);
            JsonArray chest = new JsonArray();
            chests.forEach(chest::add);
            tag.add("values",chest);

            JsonObject onlyC = new JsonObject();
            onlyC.addProperty("replace",false);
            JsonArray oc = new JsonArray();
            onlyChests.forEach(oc::add);
            onlyC.add("values",oc);

            JsonObject onlyT = new JsonObject();
            onlyT.addProperty("replace",false);
            JsonArray ot = new JsonArray();
            onlyTrapped.forEach(ot::add);
            onlyT.add("values",ot);


            writeChestTag(tag,new File(PathConstant.FORGE_BLOCK_TAGS_PATH.toFile(), "chests.json"));
            writeChestTag(tag,new File(PathConstant.BLOCK_TAGS_PATH.toFile(), "chests.json"));
            writeChestTag(onlyC,new File(PathConstant.BLOCK_TAGS_PATH.toFile(), "normal_chests.json"));
            writeChestTag(onlyT,new File(PathConstant.BLOCK_TAGS_PATH.toFile(), "trapped_chests.json"));            writeChestTag(tag,new File(PathConstant.FORGE_BLOCK_TAGS_PATH.toFile()+"/chests", "wooden.json"));
            writeChestTag(onlyT,new File(PathConstant.FORGE_BLOCK_TAGS_PATH.toFile()+"/chests", "trapped.json"));
            writeChestTag(tag,new File(PathConstant.FORGE_ITEM_TAGS_PATH.toFile(), "chests.json"));
            writeChestTag(tag,new File(PathConstant.ITEM_TAGS_PATH.toFile(), "chests.json"));
            writeChestTag(onlyC,new File(PathConstant.ITEM_TAGS_PATH.toFile(), "normal_chests.json"));
            writeChestTag(onlyT,new File(PathConstant.ITEM_TAGS_PATH.toFile(), "trapped_chests.json"));
            writeChestTag(tag,new File(PathConstant.FORGE_ITEM_TAGS_PATH.toFile()+"/chests", "wooden.json"));
            writeChestTag(onlyT,new File(PathConstant.FORGE_ITEM_TAGS_PATH.toFile()+"/chests", "trapped.json"));
            writeChestTag(tag,new File(PathConstant.MC_MINEABLE_TAGS_PATH.toFile(), "axe.json"));
            writeChestTag(onlyC,new File(PathConstant.MC_BLOCK_TAGS_PATH.toFile(), "features_cannot_replace.json"));
            writeChestTag(tag,new File(PathConstant.MC_BLOCK_TAGS_PATH.toFile(), "guarded_by_piglins.json"));

        } catch (Exception exception){
            NotEnoughChests.LOGGER.error("An error was detected when tags generating",exception);
        }
    }

    private static void writeChestTag(JsonObject tag, File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(tag));
        writer.close();
    }

}
