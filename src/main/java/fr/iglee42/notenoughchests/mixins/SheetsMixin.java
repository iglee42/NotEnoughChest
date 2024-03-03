package fr.iglee42.notenoughchests.mixins;

import fr.iglee42.notenoughchests.chest.CustomChestRenderer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(Sheets.class)
public class SheetsMixin {

    @Inject(method = "getAllMaterials",at = @At("RETURN"))
    private static void inject(Consumer<Material> p_110781_, CallbackInfo ci){


        /*for (Material single : CustomChestRenderer.single) {
            p_110781_.accept(single);
        }
        for (Material left : CustomChestRenderer.left) {
            p_110781_.accept(left);
        }
        for (Material right : CustomChestRenderer.right) {
            p_110781_.accept(right);
        }*/
    }
}
