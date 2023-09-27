package fr.iglee42.notenoughchest.custompack.generation;

public record TextureKey(String key,String object) {

    public String toJson(){
        return "        \""+key+"\": \""+object+"\"";
    }
}
