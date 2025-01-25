package fr.iglee42.notenoughchests;

import fr.iglee42.notenoughchests.chest.CustomChestRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import static fr.iglee42.notenoughchests.NotEnoughChests.MODID;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class NECClient {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void doClientStuff(FMLClientSetupEvent event) {
        BlockEntityRenderers.register(NotEnoughChests.CHEST.get(), CustomChestRenderer::new);
        BlockEntityRenderers.register(NotEnoughChests.TRAPPED_CHEST.get(), CustomChestRenderer::new);
    }
}