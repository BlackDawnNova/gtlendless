package github.forilusa.gtlendless.event;

import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import github.forilusa.gtlendless.config.GTLendlessConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;


// 机器破坏
@Mod.EventBusSubscriber(modid = "gtlendless", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockBreakEventHandler {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Level level = (Level) event.getLevel();
        Player player = event.getPlayer();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();

        if (level.isClientSide || player.isCreative()) {
            return;
        }

        MachineDefinition machineDefinition = getGTLendlessMachineDefinition(state);
        if (machineDefinition != null) {
            handleGTLendlessMachine(event, level, player, pos, machineDefinition);
            return;
        }

        handleDirectInventoryTool(event, level, player, pos, state);
    }

    private static void handleGTLendlessMachine(BlockEvent.BreakEvent event, Level level, Player player, BlockPos pos, MachineDefinition machineDefinition) {
        ResourceLocation machineId = machineDefinition.getId();

        if (hasPseudoItem(machineId)) {
            handleMachineWithPseudoItem(event, level, player, pos, machineId);
        } else {
            handleNormalMachine(event, level, player, pos, machineDefinition);
        }
    }

    private static boolean hasPseudoItem(ResourceLocation machineId) {
        String pseudoPath = "pseudo_" + machineId.getPath();
        ResourceLocation pseudoItemId = new ResourceLocation(machineId.getNamespace(), pseudoPath);
        var pseudoItem = ForgeRegistries.ITEMS.getValue(pseudoItemId);

        return pseudoItem != null && pseudoItem != Items.AIR;
    }

    private static void handleMachineWithPseudoItem(BlockEvent.BreakEvent event, Level level, Player player, BlockPos pos, ResourceLocation machineId) {
        event.setCanceled(true);

        ItemStack pseudoStack = getPseudoItemForMachine(machineId);
        if (!pseudoStack.isEmpty()) {
            if (shouldUseDirectInventory(player)) {
                boolean added = addToPlayerInventory(player, pseudoStack);
                if (!added) {
                    Block.popResource(level, pos, pseudoStack);
                }
            } else {
                Block.popResource(level, pos, pseudoStack);
            }

            level.removeBlock(pos, false);
        } else {
            event.setCanceled(false);
        }
    }

    private static void handleNormalMachine(BlockEvent.BreakEvent event, Level level, Player player, BlockPos pos, MachineDefinition machineDefinition) {
        if (shouldUseDirectInventory(player)) {
            event.setCanceled(true);

            BlockState state = machineDefinition.getBlock().defaultBlockState();
            handleDirectInventoryDrop(level, player, pos, state, player.getMainHandItem());
            level.removeBlock(pos, false);
        } else {
            event.setCanceled(false);
        }
    }

    private static ItemStack getPseudoItemForMachine(ResourceLocation machineId) {
        String pseudoPath = "pseudo_" + machineId.getPath();
        ResourceLocation pseudoItemId = new ResourceLocation(machineId.getNamespace(), pseudoPath);

        var pseudoItem = ForgeRegistries.ITEMS.getValue(pseudoItemId);

        if (pseudoItem != null && pseudoItem != Items.AIR) {
            return new ItemStack(pseudoItem);
        }

        return ItemStack.EMPTY;
    }

    private static boolean shouldUseDirectInventory(Player player) {
        if (!GTLendlessConfig.INSTANCE.blockSettings.enableDirectInventory ||
                !GTLendlessConfig.INSTANCE.toolListSettings.enableToolList) {
            return false;
        }

        return isDirectInventoryTool(player.getMainHandItem());
    }

    private static void handleDirectInventoryTool(BlockEvent.BreakEvent event, Level level, Player player, BlockPos pos, BlockState state) {
        if (!GTLendlessConfig.INSTANCE.blockSettings.enableDirectInventory ||
                !GTLendlessConfig.INSTANCE.toolListSettings.enableToolList) {
            return;
        }

        ItemStack tool = player.getMainHandItem();
        if (!isDirectInventoryTool(tool)) {
            return;
        }

        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (blockId == null || !shouldApplyDirectInventory(blockId, state)) {
            return;
        }

        event.setCanceled(true);
        handleDirectInventoryDrop(level, player, pos, state, tool);
        level.removeBlock(pos, false);
    }

    // 辅助工作

    public static MachineDefinition getGTLendlessMachineDefinition(BlockState state) {
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (blockId == null || !blockId.getNamespace().equals("gtlendless")) {
            return null;
        }

        for (MachineDefinition definition : GTRegistries.MACHINES) {
            if (definition.getBlock() == state.getBlock()) {
                return definition;
            }
        }

        return null;
    }

    private static boolean shouldApplyDirectInventory(ResourceLocation blockId, BlockState state) {
        String blockKey = blockId.toString();

        if (GTLendlessConfig.INSTANCE.blockSettings.enableForAllBlocks) {
            return !isBlockInBlacklist(state, blockKey);
        } else {
            return blockId.getNamespace().equals("gtlendless") && !isBlockInBlacklist(state, blockKey);
        }
    }

    private static boolean isBlockInBlacklist(BlockState state, String blockKey) {
        List<String> blacklist = GTLendlessConfig.INSTANCE.getBlacklist();

        for (String entry : blacklist) {
            if (entry.startsWith("#")) {
                if (isBlockInTag(state, entry.substring(1))) {
                    return true;
                }
            } else {
                if (entry.equals(blockKey)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isBlockInTag(BlockState state, String tagName) {
        try {
            ResourceLocation tagId = ResourceLocation.tryParse(tagName);
            if (tagId == null) {
                return false;
            }
            TagKey<Block> tagKey = TagKey.create(Registries.BLOCK, tagId);
            return state.is(tagKey);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isDirectInventoryTool(ItemStack tool) {
        if (tool.isEmpty()) return false;
        ResourceLocation toolId = ForgeRegistries.ITEMS.getKey(tool.getItem());
        if (toolId == null) return false;

        List<String> toolList = GTLendlessConfig.INSTANCE.getDirectInventoryTools();

        String toolIdStr = toolId.toString();
        if (toolList.contains(toolIdStr)) {
            return true;
        }

        for (String toolEntry : toolList) {
            if (toolEntry.startsWith("#")) {
                String tagName = toolEntry.substring(1);
                if (isItemInTag(tool, tagName)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isItemInTag(ItemStack tool, String tagName) {
        try {
            ResourceLocation tagId = ResourceLocation.tryParse(tagName);
            if (tagId == null) {
                return false;
            }
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagId);
            return tool.is(tagKey);
        } catch (Exception e) {
            return false;
        }
    }

    private static void handleDirectInventoryDrop(Level level, Player player, BlockPos pos, BlockState state, ItemStack tool) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        LootParams.Builder lootParamsBuilder = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, tool)
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withParameter(LootContextParams.BLOCK_STATE, state);

        List<ItemStack> drops = state.getDrops(lootParamsBuilder);

        for (ItemStack stack : drops) {
            if (!stack.isEmpty()) {
                boolean addedToInventory = addToPlayerInventory(player, stack);
                if (!addedToInventory) {
                    Block.popResource(level, pos, stack);
                }
            }
        }

        level.levelEvent(2001, pos, Block.getId(state));
    }

    private static boolean addToPlayerInventory(Player player, ItemStack stack) {
        if (GTLendlessConfig.INSTANCE.blockSettings.checkInventorySpace) {
            if (player.getInventory().getFreeSlot() == -1) {
                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                    ItemStack existing = player.getInventory().getItem(i);
                    if (existing.isEmpty()) continue;

                    if (ItemStack.isSameItemSameTags(existing, stack) && existing.getCount() < existing.getMaxStackSize()) {
                        int space = existing.getMaxStackSize() - existing.getCount();
                        int toAdd = Math.min(space, stack.getCount());
                        existing.grow(toAdd);
                        stack.shrink(toAdd);

                        if (stack.isEmpty()) {
                            return true;
                        }
                    }
                }

                if (player.getInventory().getFreeSlot() == -1) {
                    return false;
                }
            }
        }

        return player.getInventory().add(stack);
    }
}