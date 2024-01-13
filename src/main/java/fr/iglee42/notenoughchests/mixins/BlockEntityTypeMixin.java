package fr.iglee42.notenoughchests.mixins;

import fr.iglee42.notenoughchests.NotEnoughChests;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockEntityType.class)
public class BlockEntityTypeMixin{

    @Inject(method = "isValid",at = @At("HEAD"), cancellable = true)
    private void inject(BlockState p_155263_, CallbackInfoReturnable<Boolean> cir){
        if (this.equals(NotEnoughChests.CHEST.get())) cir.setReturnValue(true);
    }

}
