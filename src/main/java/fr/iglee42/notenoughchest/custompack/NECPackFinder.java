package fr.iglee42.notenoughchest.custompack;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

import java.nio.file.Path;
import java.util.function.Consumer;

public class NECPackFinder implements RepositorySource {


	private final PackType type;

	public NECPackFinder(PackType type) {

		this.type = type;
	}



	@Override
	public void loadPacks(Consumer<Pack> infoConsumer) {
		Path rootPath = PathConstant.ROOT_PATH;
		Pack pack = Pack.create("nec_"+type.getSuffix(),Component.literal("NEC_PACK"),true,
				(t)-> new InMemoryPack(rootPath),new Pack.Info(Component.literal("NEC_PACK_DESC"),12, FeatureFlagSet.of(FeatureFlags.VANILLA)),type.getVanillaType(), Pack.Position.TOP,true, PackSource.BUILT_IN);
		if (pack != null){
			infoConsumer.accept(pack);
		}
	}
}