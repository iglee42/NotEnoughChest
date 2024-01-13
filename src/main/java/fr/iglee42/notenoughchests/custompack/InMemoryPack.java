package fr.iglee42.notenoughchests.custompack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import fr.iglee42.notenoughchests.NotEnoughChests;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryPack implements PackResources {
    private final Path path;

    public InMemoryPack(Path path) {
        //TechResourcesGenerator.transferTextures();
        this.path = path;
        NotEnoughChests.generateData();
    }


    private static String getFullPath(PackType type, ResourceLocation location) {
        return String.format("%s/%s/%s", type.getDirectory(), location.getNamespace(), location.getPath());
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... p_252049_) {
        Path resolved = path.resolve(p_252049_[0]);
        return IoSupplier.create(resolved);
    }

    @Override
    public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
        Path resolved = path.resolve(getFullPath(type, location));
        if (!Files.exists(resolved)) return null;
        return IoSupplier.create(resolved);
    }


    @Override
    public void listResources(PackType p_10289_, String p_251379_, String p_251932_, ResourceOutput p_249347_) {
        var result = new ArrayList<Pair<ResourceLocation, String>>();
        getChildResourceLocations(result, 100, x -> true, path.resolve(p_10289_.getDirectory()).resolve(p_251379_).resolve(p_251932_), p_251379_, p_251932_);
        for (Pair<ResourceLocation, String> row : result) {
            p_249347_.accept(row.getFirst(), IoSupplier.create(Path.of(row.getSecond())));
        }
    }

    private void getChildResourceLocations(List<Pair<ResourceLocation, String>> result, int depth, Predicate<ResourceLocation> filter, Path current, String currentRLNS, String currentRLPath) {
        try {
            if (!Files.exists(current) || !Files.isDirectory(current)){
                return;
            }
            Stream<Path> list = Files.list(current);
            for (Path child : list.toList()) {
                if (!Files.isDirectory(child)) {
                    result.add(new Pair<>(new ResourceLocation(currentRLNS, currentRLPath + "/" + child.getFileName()), child.toString()));
                    continue;
                }
                getChildResourceLocations(result, depth + 1, filter, child, currentRLNS,  currentRLPath + "/" + child.getFileName());
            }
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
    }



    @Override
    public @NotNull Set<String> getNamespaces(PackType type) {
        Set<String> result = new HashSet<>();
        try {
            Stream<Path> list = Files.list(path.resolve(type.getDirectory()));
            for (Path resultingPath : list.collect(Collectors.toList())) {
                result.add(resultingPath.getFileName().toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException {
        JsonObject jsonobject = new JsonObject();
        JsonObject packObject = new JsonObject();
        packObject.addProperty("pack_format", 16);
        packObject.addProperty("description", "nec");
        jsonobject.add("pack", packObject);
        if (!jsonobject.has(deserializer.getMetadataSectionName())) {
            return null;
        } else {
            try {
                return deserializer.fromJson(jsonobject.get(deserializer.getMetadataSectionName()).getAsJsonObject());
            } catch (JsonParseException jsonparseexception) {
                return null;
            }
        }
    }

    @Override
    public String packId() {
        return "NEC InCode Pack";
    }


    @Override
    public void close() {

    }
}