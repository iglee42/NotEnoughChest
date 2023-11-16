package fr.iglee42.notenoughchest;

import fr.iglee42.notenoughchest.chest.CustomChestRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static fr.iglee42.notenoughchest.NotEnoughChest.MODID;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NECClient {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void doClientStuff(FMLClientSetupEvent event) {
        BlockEntityRenderers.register(NotEnoughChest.CHEST.get(), CustomChestRenderer::new);
    }
}