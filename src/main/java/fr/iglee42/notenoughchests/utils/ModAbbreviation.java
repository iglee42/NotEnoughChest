package fr.iglee42.notenoughchests.utils;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ModAbbreviation {

    private static Map<String,String> abbrevations = new HashMap<>();

    static {
        abbrevations.put("biomesoplenty","bop");
        abbrevations.put("biomeyoullgo","byg");
        abbrevations.put("regions_unexplored","rgun");
        abbrevations.put("caveopolis","caveop");
        abbrevations.put("pokecube","pokecube");
        abbrevations.put("pokecube_legends","pokecube");
    }

    public static String getChestTexture(ResourceLocation plankType){
        String abbrevModid = abbrevations.getOrDefault(plankType.getNamespace(), "");
        if (abbrevModid.isEmpty()){
            return plankType.getPath();
        } else {
            return abbrevModid+"/"+plankType.getPath();
        }
    }

    public static String getModAbbrevation(String modid){
        return abbrevations.containsKey(modid) ? abbrevations.get(modid)+"_":"";
    }


}
