package fr.iglee42.notenoughchests.custompack;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class PathConstant {

    public static Path ROOT_PATH;
    public static Path ASSETS_PATH;
    public static Path DATAS_PATH;

    public static Path LANGS_PATH;
    public static Path RECIPES_PATH;

    public static Path BLOCK_STATES_PATH;
    public static Path MODELS_PATH;
    public static Path ITEM_MODELS_PATH;
    public static Path BLOCK_MODELS_PATH;

    public static Path MC_DATA_PATH;
    public static Path MC_TAGS_PATH;
    public static Path MC_BLOCK_TAGS_PATH;
    public static Path MC_ITEM_TAGS_PATH;
    public static Path MC_MINEABLE_TAGS_PATH;

    public static Path FORGE_DATA_PATH;
    public static Path FORGE_TAGS_PATH;
    public static Path FORGE_BLOCK_TAGS_PATH;
    public static Path FORGE_ITEM_TAGS_PATH;


    public static void init() {
        try {
            deleteDirectory(FMLPaths.CONFIGDIR.get().resolve("nec/pack"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ROOT_PATH = FMLPaths.CONFIGDIR.get().resolve("nec/pack");
        ROOT_PATH.toFile().mkdirs();
        ASSETS_PATH = ROOT_PATH.resolve("assets/nec");
        DATAS_PATH = ROOT_PATH.resolve("data/nec");

        BLOCK_STATES_PATH = ASSETS_PATH.resolve("blockstates");
        LANGS_PATH = ASSETS_PATH.resolve("lang");
        MODELS_PATH = ASSETS_PATH.resolve("models");

        RECIPES_PATH = DATAS_PATH.resolve("recipes");

        ITEM_MODELS_PATH = MODELS_PATH.resolve("item");
        BLOCK_MODELS_PATH = MODELS_PATH.resolve("block");

        MC_DATA_PATH = ROOT_PATH.resolve("data/minecraft");
        MC_TAGS_PATH = MC_DATA_PATH.resolve("tags");
        MC_BLOCK_TAGS_PATH = MC_TAGS_PATH.resolve("blocks");
        MC_ITEM_TAGS_PATH = MC_TAGS_PATH.resolve("items");
        MC_MINEABLE_TAGS_PATH = MC_BLOCK_TAGS_PATH.resolve("mineable");

        FORGE_DATA_PATH = ROOT_PATH.resolve("data/forge");
        FORGE_TAGS_PATH = FORGE_DATA_PATH.resolve("tags");
        FORGE_BLOCK_TAGS_PATH = FORGE_TAGS_PATH.resolve("blocks");
        FORGE_ITEM_TAGS_PATH = FORGE_TAGS_PATH.resolve("items");

        BLOCK_STATES_PATH.toFile().mkdirs();
        LANGS_PATH.toFile().mkdirs();
        ITEM_MODELS_PATH.toFile().mkdirs();
        BLOCK_MODELS_PATH.toFile().mkdirs();
        RECIPES_PATH.toFile().mkdirs();

        MC_TAGS_PATH.toFile().mkdirs();
        MC_BLOCK_TAGS_PATH.toFile().mkdirs();
        MC_ITEM_TAGS_PATH.toFile().mkdirs();
        MC_MINEABLE_TAGS_PATH.toFile().mkdirs();

        FORGE_TAGS_PATH.toFile().mkdirs();
        FORGE_BLOCK_TAGS_PATH.toFile().mkdirs();
        FORGE_ITEM_TAGS_PATH.toFile().mkdirs();
        FORGE_BLOCK_TAGS_PATH.resolve("chests").toFile().mkdirs();
        FORGE_ITEM_TAGS_PATH.resolve("chests").toFile().mkdirs();



        JsonObject emptyJson = new JsonObject();
        try {
            FileWriter writer = new FileWriter(new File(ASSETS_PATH.toFile(), "sounds.json"));
            writer.write(new Gson().toJson(emptyJson));
            writer.close();
        }catch (Exception ignored){}

    }

    public static void deleteDirectory(Path sourceDirectory) throws IOException {
        if (!sourceDirectory.toFile().exists()) return;

        for (String f : sourceDirectory.toFile().list()) {
            deleteDirectoryCompatibilityMode(new File(sourceDirectory.toFile(), f).toPath());
        }
        sourceDirectory.toFile().delete();
    }

    public static void deleteDirectoryCompatibilityMode(Path source) throws IOException {
        if (source.toFile().isDirectory()) {
            deleteDirectory(source);
        } else {
            source.toFile().delete();
        }
    }

}