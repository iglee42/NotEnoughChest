package fr.iglee42.notenoughchests.mixins;

import fr.iglee42.notenoughchests.NotEnoughChests;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Supplier;

@Pseudo
@Mixin(value = RegisterEvent.class,remap = false)
public abstract class RegisterEventMixin<T> {

    @Shadow @Final private @NotNull ResourceKey<? extends Registry<?>> registryKey;

    @Shadow @Final private @Nullable Registry<?> vanillaRegistry;

    @Shadow @Final
    @Nullable ForgeRegistry<?> forgeRegistry;

    @Inject(method = "register(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/resources/ResourceLocation;Ljava/util/function/Supplier;)V",at = @At("HEAD"),locals = LocalCapture.CAPTURE_FAILSOFT)
    private void inject(ResourceKey<? extends Registry<T>> registryKey, ResourceLocation name, Supplier<T> valueSupplier, CallbackInfo ci){

        if (this.registryKey.equals(registryKey) && !name.getNamespace().equals(NotEnoughChests.MODID)){
            if (forgeRegistry != null)NotEnoughChests.onRegister(forgeRegistry,name);

        }
    }


}
