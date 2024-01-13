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

import static fr.iglee42.notenoughchests.NotEnoughChests.MODID;

@OnlyIn(Dist.CLIENT)
public class CustomChestRenderer extends ChestRenderer<CustomChestBlockEntity> {
    public static Material[] single;
    public static Material[] left;
    public static Material[] right;
    private static boolean christmas;

    static {
        single = new Material[NotEnoughChests.PLANK_TYPES.size()];
        left = new Material[NotEnoughChests.PLANK_TYPES.size()];
        right = new Material[NotEnoughChests.PLANK_TYPES.size()];
        for (int i = 0; i < NotEnoughChests.PLANK_TYPES.size(); i++) {
            ResourceLocation type = NotEnoughChests.WOOD_TYPES.get(i);
            single[i] = getChestMaterial(ModAbbreviation.getChestTexture(type));
            left[i] = getChestMaterial(ModAbbreviation.getChestTexture(type) + "_left");
            right[i] = getChestMaterial(ModAbbreviation.getChestTexture(type) + "_right");
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

    private static Material getChestMaterial(String path) {
        return new Material(Sheets.CHEST_SHEET, new ResourceLocation(MODID,"entity/chest/" + path));
    }

    private Material getChestMaterial(CustomChestBlockEntity tile, ChestType type) {
        if (christmas) {
            return Sheets.chooseMaterial(tile, type, true);
        } else {
            return chooseMaterial(type, left[tile.getChestTypeIndex()], right[tile.getChestTypeIndex()], single[tile.getChestTypeIndex()]);
        }
    }
}