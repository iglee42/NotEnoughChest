package fr.iglee42.notenoughchests;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import fr.iglee42.notenoughchests.chest.CustomChestBlock;
import fr.iglee42.notenoughchests.chest.CustomChestBlockEntity;
import fr.iglee42.notenoughchests.chest.CustomTrappedChestBlock;
import fr.iglee42.notenoughchests.chest.CustomTrappedChestBlockEntity;
import fr.iglee42.notenoughchests.custompack.NECPackFinder;
import fr.iglee42.notenoughchests.custompack.PackType;
import fr.iglee42.notenoughchests.custompack.PathConstant;
import fr.iglee42.notenoughchests.custompack.generation.*;
import fr.iglee42.notenoughchests.utils.DownloadAndZipUtils;
import fr.iglee42.notenoughchests.utils.ModAbbreviation;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrappedChestBlock;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final RegistryObject<BlockEntityType<CustomTrappedChestBlockEntity>> TRAPPED_CHEST = BLOCK_ENTITIES.register("trapped_chest", ()->BlockEntityType.Builder.of(CustomTrappedChestBlockEntity::new).build(null));


    public static List<ResourceLocation> WOOD_TYPES;
    public static List<String> PLANK_TYPES;
    public static Map<ResourceLocation,String> PLANK_NAME_FORMAT;
    public static Map<ResourceLocation,ResourceLocation> CHESTS_TO_WOOD;
    public static Map<ResourceLocation,ResourceLocation> TRAPPED_CHESTS_TO_WOOD;


    private static boolean hasGenerated;
    public static boolean textureServerOnline = true;
    private static JsonObject chestTextureIds;

    public NotEnoughChests() {
        hasGenerated = false;
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        try {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                URL url = new URL("https://iglee.fr:3000/chests");
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(5000);
                con.setReadTimeout(1000);
                con.setInstanceFollowRedirects(false);
                int status = con.getResponseCode();
                if (status == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder json = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        try {
                            json.append(inputLine);
                        } catch (Exception exception) {}
                    }
                    chestTextureIds = new Gson().fromJson(json.toString(),JsonObject.class);
                    in.close();
                } else {
                    textureServerOnline = false;
                }
                con.disconnect();
            }
        } catch (Exception ignored) {
            textureServerOnline = false;
        }

        WOOD_TYPES = new ArrayList<>();
        PLANK_TYPES = new ArrayList<>();
        PLANK_NAME_FORMAT = new HashMap<>();
        CHESTS_TO_WOOD = new HashMap<>();
        TRAPPED_CHESTS_TO_WOOD = new HashMap<>();

        ForgeRegistries.BLOCKS.getKeys().stream().filter(rs->rs.getPath().endsWith("_planks")).forEach(rs->{
            String woodType = rs.getPath().replace("_planks","");
            PLANK_TYPES.add(woodType);
            WOOD_TYPES.add(new ResourceLocation(woodType));
            PLANK_NAME_FORMAT.put(new ResourceLocation(woodType),"_planks");
            CHESTS_TO_WOOD.put(new ResourceLocation(MODID,woodType.toLowerCase() + "_chest"),new ResourceLocation(woodType));
            TRAPPED_CHESTS_TO_WOOD.put(new ResourceLocation(MODID,woodType.toLowerCase() + "_trapped_chest"),new ResourceLocation(woodType));
            int index = WOOD_TYPES.indexOf(new ResourceLocation(rs.getPath().replace("_planks","").toLowerCase()));
            RegistryObject<Block> chest = BLOCKS.register(woodType.toLowerCase() + "_chest", ()-> new CustomChestBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava(), CHEST::get,index));
            RegistryObject<Block> trappedChest = BLOCKS.register(woodType.toLowerCase() + "_trapped_chest", ()-> new CustomTrappedChestBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava(),index));
            ITEMS.register(woodType.toLowerCase() +"_chest",()->new BlockItem(chest.get(),new Item.Properties()){
                @Override
                public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
                    return 300;
                }
            });
            ITEMS.register(woodType.toLowerCase() +"_trapped_chest",()->new BlockItem(trappedChest.get(),new Item.Properties()){
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
            PLANK_NAME_FORMAT.put(new ResourceLocation("integrateddynamics",woodType),"_planks");
            CHESTS_TO_WOOD.put(new ResourceLocation(MODID,woodType.toLowerCase() + "_chest"),new ResourceLocation("integrateddynamics",woodType));
            TRAPPED_CHESTS_TO_WOOD.put(new ResourceLocation(MODID,woodType.toLowerCase() + "_trapped_chest"),new ResourceLocation("integrateddynamics",woodType));
            RegistryObject<Block> chest = BLOCKS.register(woodType.toLowerCase() + "_chest", ()-> new CustomChestBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava(), CHEST::get,PLANK_TYPES.indexOf(woodType)));
            RegistryObject<Block> trappedChest = BLOCKS.register(woodType.toLowerCase() + "_trapped_chest", ()-> new CustomTrappedChestBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava(), PLANK_TYPES.indexOf(woodType)));
            ITEMS.register(woodType.toLowerCase() +"_chest",()->new BlockItem(chest.get(),new Item.Properties()){
                @Override
                public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
                    return 300;
                }
            });ITEMS.register(woodType.toLowerCase() +"_trapped_chest",()->new BlockItem(trappedChest.get(),new Item.Properties()){
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
        if (id.getNamespace().equals("ad_astra")) return;
        if (registryName.getPath().equals("block")) {
            if (id.getPath().endsWith("_planks") || id.getPath().startsWith("plank_")) {
                String woodType = id.getPath().replace("_planks", "").replace("plank_","");
                if (WOOD_TYPES.contains(new ResourceLocation(id.getNamespace(), woodType.toLowerCase())) ) {
                    if (ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(MODID, ModAbbreviation.getModAbbrevation(id.getNamespace()) + woodType + "_chest"))) return;
                    WOOD_TYPES.remove(new ResourceLocation(id.getNamespace(), woodType.toLowerCase()));
                    PLANK_TYPES.remove(woodType);
                    PLANK_NAME_FORMAT.remove(new ResourceLocation(id.getNamespace(), woodType.toLowerCase()));
                    CHESTS_TO_WOOD.remove(new ResourceLocation(MODID, ModAbbreviation.getModAbbrevation(id.getNamespace()) + woodType + "_chest"));
                    TRAPPED_CHESTS_TO_WOOD.remove(new ResourceLocation(MODID, ModAbbreviation.getModAbbrevation(id.getNamespace()) + woodType + "_trapped_chest"));
                }
                WOOD_TYPES.add(new ResourceLocation(id.getNamespace(), woodType.toLowerCase()));
                PLANK_TYPES.add(woodType);
                PLANK_NAME_FORMAT.put(new ResourceLocation(id.getNamespace(), woodType.toLowerCase()),id.getPath().endsWith("_planks")?"_planks":(id.getPath().startsWith("plank_")?"plank_":""));
                CHESTS_TO_WOOD.put(new ResourceLocation(MODID, ModAbbreviation.getModAbbrevation(id.getNamespace()) + woodType + "_chest"),new ResourceLocation(id.getNamespace(), woodType.toLowerCase()));
                TRAPPED_CHESTS_TO_WOOD.put(new ResourceLocation(MODID, ModAbbreviation.getModAbbrevation(id.getNamespace()) + woodType + "_trapped_chest"),new ResourceLocation(id.getNamespace(), woodType.toLowerCase()));

                event.register(ForgeRegistries.Keys.BLOCKS, new ResourceLocation(MODID, ModAbbreviation.getModAbbrevation(id.getNamespace()) + woodType + "_chest"), () -> new CustomChestBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava(), CHEST::get, WOOD_TYPES.indexOf(new ResourceLocation(id.getNamespace(), woodType.toLowerCase()))));
                event.register(ForgeRegistries.Keys.BLOCKS, new ResourceLocation(MODID, ModAbbreviation.getModAbbrevation(id.getNamespace()) + woodType + "_trapped_chest"), () -> new CustomTrappedChestBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava(), WOOD_TYPES.indexOf(new ResourceLocation(id.getNamespace(), woodType.toLowerCase()))));
            }
        } else if (registryName.getPath().equals("item")) {
            if (id.getPath().endsWith("_planks")  || id.getPath().startsWith("plank_")) {
                String woodType = id.getPath().replace("_planks", "").replace("plank_","");
                event.register(ForgeRegistries.Keys.ITEMS, new ResourceLocation(MODID, ModAbbreviation.getModAbbrevation(id.getNamespace()) + woodType + "_chest"), () -> new BlockItem(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MODID,ModAbbreviation.getModAbbrevation(id.getNamespace())+woodType + "_chest")),new Item.Properties()){
                    @Override
                    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
                        return 300;
                    }
                });
                event.register(ForgeRegistries.Keys.ITEMS, new ResourceLocation(MODID, ModAbbreviation.getModAbbrevation(id.getNamespace()) + woodType + "_trapped_chest"), () -> new BlockItem(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MODID,ModAbbreviation.getModAbbrevation(id.getNamespace())+woodType + "_trapped_chest")),new Item.Properties()){
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
        /*AtomicInteger current = new AtomicInteger();
        List<ResourceLocation> used = new ArrayList<>();
        WOOD_TYPES.stream().filter(wt->!used.contains(wt)).forEach(wt->{
            System.out.println("\""+ModAbbreviation.getModAbbrevation(wt.getNamespace())+wt.getPath()+"\": " + current);
            current.getAndIncrement();
            used.add(wt);
        });*/
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
            if (FMLEnvironment.dist == Dist.CLIENT) {
                if (textureServerOnline) {
                    List<Integer> idsToDownload = new ArrayList<>();
                    WOOD_TYPES.forEach(wt -> {
                        String abrev = ModAbbreviation.getModAbbrevation(wt.getNamespace());
                        Map<String, Integer> ids = new HashMap<>();
                        if (chestTextureIds != null)chestTextureIds.asMap().forEach((id, el) -> ids.put(id, el.getAsInt()));
                        int id;
                        if (abrev.isEmpty()) {
                            id = ids.getOrDefault(wt.getPath(), -1);
                        } else {
                            id = ids.getOrDefault(abrev.replace("_", "") + "/" + wt.getPath(), -1);
                        }
                        idsToDownload.add(id);
                    });
                    StringBuilder array = new StringBuilder("[");
                    for (int i = 0; i < idsToDownload.size(); i++) {
                        array.append(idsToDownload.get(i));
                        if (i < idsToDownload.size() - 1) array.append(",");
                    }
                    array.append("]");
                    try {
                        URL url = new URL("https://iglee.fr:3000/chestTextures?chests=" + array);
                        File zipFile = new File(PathConstant.ROOT_PATH.toString(), "chests.zip");
                        DownloadAndZipUtils.downloadUsingStream(url, zipFile);
                        DownloadAndZipUtils.unzip(zipFile, PathConstant.CHEST_TEXTURES_PATH.toFile());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                ModelsGenerator.generate();
                BlockStatesGenerator.generate();
                LangsGenerator.generate();
            }
            RecipesGenerator.generate();
            TagsGenerator.generate();
            LootTablesGenerator.generate();

            hasGenerated = true;
        }
    }

    public static void injectDatapackFinder(PackRepository resourcePacks) {
        if (DistExecutor.unsafeRunForDist(() -> () -> resourcePacks != Minecraft.getInstance().getResourcePackRepository(), () -> () -> true)) {
            resourcePacks.addPackFinder(new NECPackFinder(PackType.DATA));
        }
    }


}
