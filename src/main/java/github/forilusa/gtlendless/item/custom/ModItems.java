package github.forilusa.gtlendless.item.custom;

import github.forilusa.gtlendless.GTLendless;
import github.forilusa.gtlendless.block.custom.ModBlocks;
import github.forilusa.gtlendless.registration.GTLEndlessRegistrate;
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
                    github.forilusa.gtlendless.item.animation.AnimationConfig.WATER_BUCKET,
                    true
            ));

    public static final RegistryObject<Item> FINAL_PROTON = ITEMS.register("final_proton",
            () -> new AnimatedProtonItem(
                    new Item.Properties()
                            .fireResistant(),
                    github.forilusa.gtlendless.item.animation.AnimationConfig.PROTON,
                    false
            ));

    public static final RegistryObject<Item> GENESIS_FACTOR = ITEMS.register("genesis_factor",
            () -> new AnimatedGenesisFactorItem(
                    new Item.Properties()
                            .fireResistant(),
                    github.forilusa.gtlendless.item.animation.AnimationConfig.GENESIS,
                    false
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
                    github.forilusa.gtlendless.item.animation.AnimationConfig.LOG,
                    true
            ));


    // 机器
    // 终焉蒸汽熔炉(伪)
    public static final RegistryObject<Item> PSEUDO_STEAM_FINAL_FURNACE = ITEMS.register("pseudo_steam_final_furnace",
            () -> new BlockItem(ModBlocks.PSEUDO_STEAM_FINAL_FURNACE.get(),
                    new Item.Properties()));

    public static void register(net.minecraftforge.eventbus.api.IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}