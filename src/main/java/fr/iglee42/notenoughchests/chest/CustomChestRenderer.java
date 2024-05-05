package fr.iglee42.notenoughchests.chest;

import fr.iglee42.notenoughchests.NotEnoughChests;
import fr.iglee42.notenoughchests.utils.ModAbbreviation;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static fr.iglee42.notenoughchests.NotEnoughChests.MODID;
import static fr.iglee42.notenoughchests.NotEnoughChests.WOOD_TYPES;

@OnlyIn(Dist.CLIENT)
public class CustomChestRenderer extends ChestRenderer<CustomChestBlockEntity> {
    public static Map<ResourceLocation,Material> single;
    public static Map<ResourceLocation,Material> left;
    public static Map<ResourceLocation,Material> right;
    public static Map<ResourceLocation,Material> single_trapped;
    public static Map<ResourceLocation,Material> left_trapped;
    public static Map<ResourceLocation,Material> right_trapped;
    private static boolean christmas;

    static {
        single = new HashMap<>();
        left = new HashMap<>();
        right = new HashMap<>();
        single_trapped = new HashMap<>();
        left_trapped = new HashMap<>();
        right_trapped = new HashMap<>();
        for (int i = 0; i < NotEnoughChests.WOOD_TYPES.size(); i++) {
            ResourceLocation type = NotEnoughChests.WOOD_TYPES.get(i);
            single.put(type,getChestMaterial(ModAbbreviation.getChestTexture(type),"single"));
            left.put(type,getChestMaterial(ModAbbreviation.getChestTexture(type),"left"));
            right.put(type,getChestMaterial(ModAbbreviation.getChestTexture(type), "right"));
            single_trapped.put(type,getTrappedMaterial(ModAbbreviation.getChestTexture(type),"single"));
            left_trapped.put(type,getTrappedMaterial(ModAbbreviation.getChestTexture(type),"left"));
            right_trapped.put(type,getTrappedMaterial(ModAbbreviation.getChestTexture(type), "right"));
        }
    }

    public CustomChestRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
            christmas = true;
        }
    }

    @Override
    protected Material getMaterial(CustomChestBlockEntity blockEntity, ChestType chestType) {
        return getChestMaterial(blockEntity, chestType);
    }

    public static Material chooseMaterial(ChestType type, Material left, Material right, Material single) {
        return switch (type) {
            case LEFT -> left;
            case RIGHT -> right;
            default -> single;
        };
    }

    private static Material getChestMaterial(String path,String type) {
        return new Material(Sheets.CHEST_SHEET, new ResourceLocation(MODID,"entity/chest/" + type + "/" + path));
    }
    private static Material getTrappedMaterial(String path,String type) {
        return new Material(Sheets.CHEST_SHEET, new ResourceLocation(MODID,"entity/chest/trapped_" + type + "/" + path));
    }

    private Material getChestMaterial(CustomChestBlockEntity tile, ChestType type) {
        if (christmas) {
            return Sheets.chooseMaterial(tile, type, true);
        } else if (tile instanceof CustomTrappedChestBlockEntity) {
            return chooseMaterial(type, left_trapped.get(WOOD_TYPES.get(tile.getChestTypeIndex())), right_trapped.get(WOOD_TYPES.get(tile.getChestTypeIndex())), single_trapped.get(WOOD_TYPES.get(tile.getChestTypeIndex())));
        } else {
            return chooseMaterial(type, left.get(WOOD_TYPES.get(tile.getChestTypeIndex())), right.get(WOOD_TYPES.get(tile.getChestTypeIndex())), single.get(WOOD_TYPES.get(tile.getChestTypeIndex())));
        }
    }
}