package fr.iglee42.notenoughchests.mixins;

import fr.iglee42.notenoughchests.NotEnoughChests;
import net.minecraftforge.registries.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Supplier;

@Pseudo
@Mixin(value = DeferredRegister.class,remap = false)
public abstract class DeferredRegisterMixin<T> {

    @Shadow @Final private Map<RegistryObject<T>, Supplier<? extends T>> entries;

    @Inject(method = "addEntries",at = @At("HEAD"))
    private void inject(RegisterEvent event, CallbackInfo ci){
        for (Map.Entry<RegistryObject<T>, Supplier<? extends T>> e : entries.entrySet()){
            NotEnoughChests.onRegistryObjectCreated(e.getKey().getKey().registry(), e.getKey().getId(),event);
        }
    }


}
