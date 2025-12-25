package github.forilusa.gtlendless.item.custom;

import github.forilusa.gtlendless.GTLendless;
import github.forilusa.gtlendless.block.custom.ModBlocks;
import github.forilusa.gtlendless.item.animation.AnimationConfig;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GTLendless.MOD_ID);

    // 物品
    public static final RegistryObject<Item> FIRST_WATER_BUCKET = ITEMS.register("first_water_bucket",
            () -> new AnimatedWaterBucket(
                    new Item.Properties()
                            .stacksTo(1)
                            .fireResistant(),
                    AnimationConfig.WATER_BUCKET,
                    true
            ));

    public static final RegistryObject<Item> FINAL_PROTON = ITEMS.register("final_proton",
            () -> new AnimatedProtonItem(
                    new Item.Properties()
                            .fireResistant(),
                    AnimationConfig.PROTON,
                    false
            ));

    public static final RegistryObject<Item> GENESIS_FACTOR = ITEMS.register("genesis_factor",
            () -> new AnimatedGenesisFactorItem(
                    new Item.Properties()
                            .fireResistant(),
                    AnimationConfig.GENESIS,
                    false
            ));


    public static final RegistryObject<Item> UNIVERSAL_ENTITY_CONTROLLER = ITEMS.register("universal_entity_controller",
            () -> new UniversalEntityController(  // 使用现有的UniversalEntityController类
                    new Item.Properties()
                            .stacksTo(1)
                            .fireResistant()
                            .rarity(net.minecraft.world.item.Rarity.EPIC)
            ));

    // 工具
    // 多方块扫描器
    public static final RegistryObject<Item> MULTIBLOCK_SCANNER = ITEMS.register("multiblock_scanner",
            () -> new MultiblockScannerItem(
                    new Item.Properties()
                            .stacksTo(1)
            ));

    // 方块
    public static final RegistryObject<Item> FIRST_LOG = ITEMS.register("first_log",
            () -> new AnimatedLogBlockItem(
                    ModBlocks.FIRST_LOG.get(),
                    new Item.Properties(),
                    AnimationConfig.LOG,
                    true
            ));


    public static final RegistryObject<Item> PSEUDO_STEAM_FINAL_FURNACE = ITEMS.register("pseudo_steam_final_furnace",
            () -> new BlockItem(ModBlocks.PSEUDO_STEAM_FINAL_FURNACE.get(),
                    new Item.Properties()));

    public static void register(net.minecraftforge.eventbus.api.IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}