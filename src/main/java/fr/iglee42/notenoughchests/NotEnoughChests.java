package fr.iglee42.notenoughchests;

import com.mojang.logging.LogUtils;
import fr.iglee42.notenoughchests.chest.CustomChestBlock;
import fr.iglee42.notenoughchests.chest.CustomChestBlockEntity;
import fr.iglee42.notenoughchests.custompack.NECPackFinder;
import fr.iglee42.notenoughchests.custompack.PackType;
import fr.iglee42.notenoughchests.custompack.PathConstant;
import fr.iglee42.notenoughchests.custompack.generation.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mod(NotEnoughChests.MODID)
public class NotEnoughChests {

    public static final String MODID = "nec";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.nec"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(Items.CHEST::getDefaultInstance)
            .build());

    public static final RegistryObject<BlockEntityType<CustomChestBlockEntity>> CHEST = BLOCK_ENTITIES.register("chest", ()->BlockEntityType.Builder.of(CustomChestBlockEntity::new).build(null));


    public static List<ResourceLocation> WOOD_TYPES;
    public static List<String> PLANK_TYPES;


    private static boolean hasGenerated;

    public NotEnoughChests() {
        hasGenerated = false;
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        WOOD_TYPES = new ArrayList<>();
        PLANK_TYPES = new ArrayList<>();

        ForgeRegistries.BLOCKS.getKeys().stream().filter(rs->rs.getPath().endsWith("_planks")).forEach(rs->{
            String woodType = rs.getPath().replace("_planks","");
            PLANK_TYPES.add(woodType);
            WOOD_TYPES.add(new ResourceLocation(rs.getPath().replace("_planks","").toLowerCase()));
            RegistryObject<Block> chest = BLOCKS.register(woodType.toLowerCase() + "_chest", ()-> new CustomChestBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava(), CHEST::get,PLANK_TYPES.indexOf(woodType)));
            ITEMS.register(woodType.toLowerCase() +"_chest",()->new BlockItem(chest.get(),new Item.Properties()){
                @Override
                public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
                    return 300;
                }
            });
        });

        if (ModList.get().isLoaded("integrateddynamics")) {
            String woodType = "menril";
            PLANK_TYPES.add(woodType);
            WOOD_TYPES.add(new ResourceLocation("integrateddynamics",woodType));
            RegistryObject<Block> chest = BLOCKS.register(woodType.toLowerCase() + "_chest", ()-> new CustomChestBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava(), CHEST::get,PLANK_TYPES.indexOf(woodType)));
            ITEMS.register(woodType.toLowerCase() +"_chest",()->new BlockItem(chest.get(),new Item.Properties()){
                @Override
                public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
                    return 300;
                }
            });
        }

        try {
            NECCommonConfig.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        PathConstant.init();


        MinecraftForge.EVENT_BUS.addListener(this::onServerStart);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarted);

        modEventBus.addListener(this::addCreative);

        MinecraftForge.EVENT_BUS.register(this);


        try {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                Minecraft.getInstance().getResourcePackRepository().addPackFinder(new NECPackFinder(PackType.RESOURCE));
            }
        } catch (Exception ignored) {
        }

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    public static void onRegistryObjectCreated(ResourceLocation registryName,ResourceLocation id,RegisterEvent event){
        if (NECCommonConfig.modsBlacklist.contains(id.getNamespace())) return;
        if (registryName.getPath().equals("block")) {
            if (id.getPath().endsWith("_planks") || id.getPath().startsWith("plank_")) {
                String woodType = id.getPath().replace("_planks", "").replace("plank_","");
                WOOD_TYPES.add(new ResourceLocation(id.getNamespace(), woodType.toLowerCase()));
                PLANK_TYPES.add(woodType);
                event.register(ForgeRegistries.Keys.BLOCKS, new ResourceLocation(MODID, woodType + "_chest"), () -> new CustomChestBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava(), CHEST::get, PLANK_TYPES.indexOf(woodType)));
            }
        } else if (registryName.getPath().equals("item")) {
            if (id.getPath().endsWith("_planks")  || id.getPath().startsWith("plank_")) {
                String woodType = id.getPath().replace("_planks", "").replace("plank_","");
                event.register(ForgeRegistries.Keys.ITEMS, new ResourceLocation(MODID, woodType + "_chest"), () -> new BlockItem(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MODID,woodType + "_chest")),new Item.Properties()){
                    @Override
                    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
                        return 300;
                    }
                });
            }
        }

    }





    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == TAB.getKey()) {
            ForgeRegistries.ITEMS.getKeys().stream().filter(rs -> rs.getNamespace().equals(MODID)).forEach(rs ->
                    event.accept(ForgeRegistries.ITEMS.getValue(rs)));
        }
    }
    public void onServerStarted(final ServerStartedEvent event) {
        /*Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                PackRepository repo = event.getServer().getPackRepository();
                List<Pack> packs = Lists.newArrayList(repo.getSelectedPacks());
                event.getServer().reloadResources(packs.stream().map(Pack::getId).collect(Collectors.toList()));
                this.cancel();
            }
        }, 5000L);*/
    }

    public void onServerStart(final ServerAboutToStartEvent event) {
        event.getServer().getPackRepository().addPackFinder(new NECPackFinder(PackType.DATA));

    }


    public static void generateData() {
        if (!hasGenerated) {
            if (!ModLoader.isLoadingStateValid()) {
                return;
            }
            ModelsGenerator.generate();
            BlockStatesGenerator.generate();
            LangsGenerator.generate();
            RecipesGenerator.generate();
            TagsGenerator.generate();
            hasGenerated = true;
        }
    }

    public static void injectDatapackFinder(PackRepository resourcePacks) {
        if (DistExecutor.unsafeRunForDist(() -> () -> resourcePacks != Minecraft.getInstance().getResourcePackRepository(), () -> () -> true)) {
            resourcePacks.addPackFinder(new NECPackFinder(PackType.RESOURCE));
        }
    }

}
