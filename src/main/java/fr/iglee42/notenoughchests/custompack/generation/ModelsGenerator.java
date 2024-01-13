package fr.iglee42.notenoughchests.custompack.generation;

import fr.iglee42.notenoughchests.NotEnoughChests;
import fr.iglee42.notenoughchests.custompack.PathConstant;
import fr.iglee42.notenoughchests.utils.ModAbbreviation;

import java.io.File;
import java.io.FileWriter;

import static fr.iglee42.notenoughchests.NotEnoughChests.MODID;


public class ModelsGenerator {
    public static void generate() {
        NotEnoughChests.WOOD_TYPES.forEach(wt->{
            if (!wt.getNamespace().equals("minecraft")) {
                String t = wt.getPath();
                itemFromParent(t + "_chest", MODID + ":item/chest_base", new TextureKey("wood_type", MODID + ":entity/chest/" + ModAbbreviation.getChestTexture(wt)));
                blockFromParent(t + "_chest", "minecraft:block/chest", new TextureKey("particle", MODID + ":entity/chest/" + ModAbbreviation.getChestTexture(wt)));
            }
        });
    }

    private static void itemFromBlock(String name){
        itemFromParent(name,MODID + ":block/" + name);
    }
    private static void itemFromParent(String name, String parent, TextureKey... textureKeys){
        String jsonBase =   "{\n"+
                            "   \"parent\": \""+ parent +"\""+(textureKeys.length > 0 ? ",":"")+"\n";
        StringBuilder builder = new StringBuilder(jsonBase);
        if (textureKeys.length > 0){
            builder.append("   \"textures\": {\n");
            for (int i = 0; i < textureKeys.length; i++){
                builder.append(textureKeys[i].toJson());
                if (i != textureKeys.length - 1) builder.append(",");
                builder.append("\n");
            }
            builder.append("    }\n");
        }
        builder.append("}");
        generateItem(name,builder.toString());
    }
    private static void generateItem(String name, String fileText) {
        try {
            FileWriter writer = new FileWriter(new File(PathConstant.ITEM_MODELS_PATH.toFile(), name+".json"));
            writer.write(fileText);
            writer.close();
        } catch (Exception exception){
            NotEnoughChests.LOGGER.error("An error was detected when models generating",exception);
        }

    }
    private static void blockFromParent(String name,String parent,TextureKey... textureKeys){
        String jsonBase =   "{\n"+
                            "   \"parent\": \""+ parent +"\",\n";
        StringBuilder builder = new StringBuilder(jsonBase);
        if (textureKeys.length > 0){
            builder.append("   \"textures\": {\n");
            for (int i = 0; i < textureKeys.length; i++){
                builder.append(textureKeys[i].toJson());
                if (i != textureKeys.length - 1) builder.append(",");
                builder.append("\n");
            }
            builder.append("    }\n");
        }
        builder.append("}");
        generateBlock(name,builder.toString());
    }
    private static void generateBlock(String name, String fileText) {
        try {
            FileWriter writer = new FileWriter(new File(PathConstant.BLOCK_MODELS_PATH.toFile(), name+".json"));
            writer.write(fileText);
            writer.close();
        } catch (Exception exception){
            NotEnoughChests.LOGGER.error("An error was detected when models generating",exception);
        }

    }
}
