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
import fr.iglee42.notenoughchests.utils.ModAbbreviation;
import fr.iglee42.notenoughchests.utils.RequestsUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod(NotEnoughChests.MODID)
public class NotEnoughChests {

    public static final String MODID = "nec";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab,CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.nec"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(Items.CHEST::getDefaultInstance)
            .build());

    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CustomChestBlockEntity>> CHEST = BLOCK_ENTITIES.register("chest", ()->BlockEntityType.Builder.of(CustomChestBlockEntity::new).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CustomTrappedChestBlockEntity>> TRAPPED_CHEST = BLOCK_ENTITIES.register("trapped_chest", ()->BlockEntityType.Builder.of(CustomTrappedChestBlockEntity::new).build(null));


    public static List<ResourceLocation> WOOD_TYPES;
    public static List<String> PLANK_TYPES;
    public static Map<ResourceLocation,String> PLANK_NAME_FORMAT;
    public static Map<ResourceLocation,ResourceLocation> CHESTS_TO_WOOD;
    public static Map<ResourceLocation,ResourceLocation> TRAPPED_CHESTS_TO_WOOD;


    private static boolean hasGenerated;
    public static JsonObject chestTextureIds;

    public NotEnoughChests(IEventBus modEventBus) {
        hasGenerated = false;

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addPackFindEvent);
        modEventBus.addListener(this::addCreative);


        RequestsUtils.ping();

        ModAbbreviation.init();

        askChestIds();

        WOOD_TYPES = new ArrayList<>();
        PLANK_TYPES = new ArrayList<>();
        PLANK_NAME_FORMAT = new HashMap<>();
        CHESTS_TO_WOOD = new HashMap<>();
        TRAPPED_CHESTS_TO_WOOD = new HashMap<>();

        BuiltInRegistries.BLOCK.keySet().stream().filter(rs->rs.getPath().endsWith("_planks")).forEach(rs->{
            String woodType = rs.getPath().replace("_planks","");
            PLANK_TYPES.add(woodType);
            WOOD_TYPES.add(ResourceLocation.withDefaultNamespace(woodType));
            PLANK_NAME_FORMAT.put(ResourceLocation.withDefaultNamespace(woodType),"_planks");
            CHESTS_TO_WOOD.put(ResourceLocation.fromNamespaceAndPath(MODID,woodType.toLowerCase() + "_chest"),ResourceLocation.withDefaultNamespace(woodType));
            TRAPPED_CHESTS_TO_WOOD.put(ResourceLocation.fromNamespaceAndPath(MODID,woodType.toLowerCase() + "_trapped_chest"),ResourceLocation.withDefaultNamespace(woodType));
            int index = WOOD_TYPES.indexOf(ResourceLocation.withDefaultNamespace(rs.getPath().replace("_planks","").toLowerCase()));
            DeferredHolder<Block,CustomChestBlock> chest = BLOCKS.register(woodType.toLowerCase() + "_chest", ()-> new CustomChestBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava(), CHEST::get,index));
            DeferredHolder<Block,CustomTrappedChestBlock> trappedChest = BLOCKS.register(woodType.toLowerCase() + "_trapped_chest", ()-> new CustomTrappedChestBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava(),index));
            ITEMS.register(woodType.toLowerCase() +"_chest",()->new BlockItem(chest.get(),new Item.Properties()){
                @Override
                public int getBurnTime(@NotNull ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
                    return 300;
                }
            });
            ITEMS.register(woodType.toLowerCase() +"_trapped_chest",()->new BlockItem(trappedChest.get(),new Item.Properties()){
                @Override
                public int getBurnTime(@NotNull ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
                    return 300;
                }
            });
        });

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


        try {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                Minecraft.getInstance().getResourcePackRepository().addPackFinder(new NECPackFinder(PackType.RESOURCE));
            }
        } catch (Exception ignored) {
        }

    }

    private void askChestIds() {
        try {
            if (FMLEnvironment.dist == Dist.CLIENT && RequestsUtils.API_ONLINE) {
                HttpURLConnection con = RequestsUtils.sendRequest("chests","GET",0);
                int status = con.getResponseCode();
                if (status == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder json = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        try {
                            json.append(inputLine);
                        } catch (Exception ignored) {}
                    }
                    chestTextureIds = new Gson().fromJson(json.toString(),JsonObject.class);
                    in.close();
                } else {
                    LOGGER.error("Error while asking chests ids, server sent code: {}", status);
                }
                con.disconnect();
            }
        } catch (Exception ignored) {
            LOGGER.error("Error while asking chests ids");
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    public static void onRegister(Registry<?> registry, ResourceLocation id){
        if (NECCommonConfig.modsBlacklist.contains(id.getNamespace())) return;
        if (id.getNamespace().equals("ad_astra")) return;
        if (registry.key().location().getPath().equals("block")) {
            generateChest(id);
        }

    }

    public static void generateChest(ResourceLocation id){
        if (id.getPath().endsWith("_planks") || id.getPath().startsWith("plank_")) {
            String woodType = id.getPath().replace("_planks", "").replace("plank_","");
            if (WOOD_TYPES.contains(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), woodType.toLowerCase())) ) {
                if (BuiltInRegistries.BLOCK.containsKey(ResourceLocation.fromNamespaceAndPath(MODID, ModAbbreviation.getModAbbreviation(id.getNamespace()) + woodType + "_chest"))) return;
                WOOD_TYPES.remove(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), woodType.toLowerCase()));
                PLANK_TYPES.remove(woodType);
                PLANK_NAME_FORMAT.remove(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), woodType.toLowerCase()));
                CHESTS_TO_WOOD.remove(ResourceLocation.fromNamespaceAndPath(MODID, ModAbbreviation.getModAbbreviation(id.getNamespace()) + woodType + "_chest"));
                TRAPPED_CHESTS_TO_WOOD.remove(ResourceLocation.fromNamespaceAndPath(MODID, ModAbbreviation.getModAbbreviation(id.getNamespace()) + woodType + "_trapped_chest"));
            }
            WOOD_TYPES.add(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), woodType.toLowerCase()));
            PLANK_TYPES.add(woodType);
            PLANK_NAME_FORMAT.put(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), woodType.toLowerCase()),id.getPath().endsWith("_planks")?"_planks":(id.getPath().startsWith("plank_")?"plank_":""));
            CHESTS_TO_WOOD.put(ResourceLocation.fromNamespaceAndPath(MODID, ModAbbreviation.getModAbbreviation(id.getNamespace()) + woodType + "_chest"),ResourceLocation.fromNamespaceAndPath(id.getNamespace(), woodType.toLowerCase()));
            TRAPPED_CHESTS_TO_WOOD.put(ResourceLocation.fromNamespaceAndPath(MODID, ModAbbreviation.getModAbbreviation(id.getNamespace()) + woodType + "_trapped_chest"),ResourceLocation.fromNamespaceAndPath(id.getNamespace(), woodType.toLowerCase()));
            Registry.register(BuiltInRegistries.BLOCK,ResourceLocation.fromNamespaceAndPath(MODID, ModAbbreviation.getModAbbreviation(id.getNamespace()) + woodType + "_chest"),  new CustomChestBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava(), CHEST::get, WOOD_TYPES.indexOf(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), woodType.toLowerCase()))));
            Registry.register(BuiltInRegistries.BLOCK,ResourceLocation.fromNamespaceAndPath(MODID, ModAbbreviation.getModAbbreviation(id.getNamespace()) + woodType + "_trapped_chest"), new CustomTrappedChestBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava(), WOOD_TYPES.indexOf(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), woodType.toLowerCase()))));
            Registry.register(BuiltInRegistries.ITEM,ResourceLocation.fromNamespaceAndPath(MODID, ModAbbreviation.getModAbbreviation(id.getNamespace()) + woodType + "_chest"), new BlockItem(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(MODID,ModAbbreviation.getModAbbreviation(id.getNamespace())+woodType + "_chest")),new Item.Properties()){
                @Override
                public int getBurnTime(@NotNull ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
                    return 300;
                }
            });
            Registry.register(BuiltInRegistries.ITEM,ResourceLocation.fromNamespaceAndPath(MODID, ModAbbreviation.getModAbbreviation(id.getNamespace()) + woodType + "_trapped_chest"), new BlockItem(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(MODID,ModAbbreviation.getModAbbreviation(id.getNamespace())+woodType + "_trapped_chest")),new Item.Properties()){
                @Override
                public int getBurnTime(@NotNull ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
                    return 300;
                }
            });
        }
    }


    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == TAB.getKey()) {
            BuiltInRegistries.ITEM.keySet().stream().filter(rs -> rs.getNamespace().equals(MODID)).forEach(rs ->
                    event.accept(BuiltInRegistries.ITEM.get(rs)));
        }
    }


    public static void generateData() {
        if (!hasGenerated) {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                RequestsUtils.downloadTextures();
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


    public void addPackFindEvent(AddPackFindersEvent event){
        if (event.getPackType() == net.minecraft.server.packs.PackType.CLIENT_RESOURCES)event.addRepositorySource(new NECPackFinder(PackType.RESOURCE));
        else event.addRepositorySource(new NECPackFinder(PackType.DATA));
    }


}
