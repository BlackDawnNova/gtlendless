package github.forilusa.gtlendless.block.custom;

import github.forilusa.gtlendless.GTLendless;
import github.forilusa.gtlendless.block.PseudoMachineBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, GTLendless.MOD_ID);

    public static final RegistryObject<Block> FIRST_LOG = BLOCKS.register("first_log",
            BlockFactory::createLogBlock);

    public static final RegistryObject<Block> PSEUDO_STEAM_FINAL_FURNACE = BLOCKS.register("pseudo_steam_final_furnace",
            PseudoMachineBlock::new);

    // 多方块结构扫描器主控制器
    public static final RegistryObject<Block> MULTIBLOCK_SCANNER_CONTROLLER = BLOCKS.register(
            "multiblock_scanner_controller",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.METAL)
                    .lightLevel(state -> 1)
                    .pushReaction(PushReaction.BLOCK)
            )
    );

    public static void register(net.minecraftforge.eventbus.api.IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}