package github.forilusa.gtlendless.item.custom;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import github.forilusa.gtlendless.GTLendless;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import github.forilusa.gtlendless.config.ScannerConfig;

// 多方块结构扫描器
public class MultiblockScannerItem extends BaseAnimatedItem {

    private static final String TAG_POS1 = "Pos1";
    private static final String TAG_POS2 = "Pos2";
    private static final String TAG_STRUCT_NAME = "StructName";

    private static final char[] CHAR_SET = {
            'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
            'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
            '0','1','2','3','4','5','6','7','8','9','@','#','$','%','&','*','+','-','=','_','~','!','?','<','>','|',
            '^', '`', '[', ']', '{', '}', '♂', ';', ':', '♀', '"', ',', '.', '/', '(', ')',
            '±', '×', '÷', '≠', '≤', '≥', '∞', '∝', '∫', '∬', '∭', '∮', '∇', '∆', '∏', '∑', '√',
            '⊃', '⊆', '⊇',
            '←', '→', '↑', '↓', '↔', '↕', '↖', '↗', '↘', '↙', '⇐', '⇒', '⇑', '⇓', '⇔', '⇕',
            '↦', '↩', '↪',
            'γ', 'θ', 'ξ', 'π', 'φ', 'ψ', 'ω', 'Γ', 'Θ', 'Ξ', 'Φ', 'Ψ',
            'δ', 'Δ', 'λ', 'Λ', 'μ',
            'ё', 'ж', 'з', 'й', 'п', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я',
            'Ё', 'Ж', 'З', 'Й', 'П', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я',
            'ц', 'ч', 'ђ', 'љ', 'њ', 'Ц', 'Ч', 'Ђ',
            'л', 'Л', 'м', 'М',
            '©', '®', '™', '§', '¶', '†', '‡', '•', '‣', '⁃', '⁎', '⁕', '∼', '≈', '≡', '∽', '∝', '∟', '∠', '∣',
            '€', '¥', '£',
            '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖', '拾',
            '一', '二', '三', '四', '五', '六', '七', '八', '九', '十',
            '∅', '∉', '∈', '∋', '∌', '∐', '∓', '∔', '∀', '∃', '∄', '∛', '∜', '∝', '∟', '∠', '∣', '∤', '∦', '∧', '∨', '⊂',
            '⊄',
            '☀', '☁', '☂', '☃', '☄', '★', '☆', '☇', '☈', '☉', '☊', '☋', '☌', '☍', '☎', '☏', '☐', '☑', '☒', '☕',
            '☖', '☗', '☘', '☙', '☚', '☛', '☜', '☝', '☞', '☟', '☠', '☡', '☢', '☣', '☤', '☥', '☦', '☧', '☨', '☩',
            '☪', '☫'
    };

    public MultiblockScannerItem(Properties properties) {
        super(properties,
                github.forilusa.gtlendless.item.animation.AnimationConfig.MULTIBLOCK_SCANNER,
                false);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, net.minecraft.world.entity.Entity entity) {
        if (Screen.hasAltDown()) {
            if (player.level().isClientSide) {
                sendClearSelectionsPacket();
                return true;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
        if (Screen.hasAltDown()) {
            if (player.level().isClientSide) {
                sendClearSelectionsPacket();
                return true;
            }
            return true;
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public void sendClearSelectionsPacket() {
        if (Minecraft.getInstance().getConnection() != null && GTLendless.NETWORK_CHANNEL != null) {
            GTLendless.NETWORK_CHANNEL.sendToServer(
                    new github.forilusa.gtlendless.network.ClearScannerSelectionsPacket()
            );
        }
    }

    public void clearSelectionsFromPacket(ItemStack stack, ServerPlayer player) {
        clearAllSelections(stack, player);
    }

    private int countBlockTypes(Level level, BlockPos minPos, BlockPos maxPos) {
        Set<ResourceLocation> uniqueBlocks = new HashSet<>();

        ResourceLocation scannerControllerId = new ResourceLocation("gtlendless", "scanner_controller");

        for (int y = minPos.getY(); y <= maxPos.getY(); y++) {
            for (int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
                for (int x = minPos.getX(); x <= maxPos.getX(); x++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    Block block = state.getBlock();

                    if (block == net.minecraft.world.level.block.Blocks.AIR) {
                        continue;
                    }

                    ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
                    if (blockId == null) {
                        continue;
                    }

                    if (blockId.equals(scannerControllerId)) {
                        continue;
                    }

                    uniqueBlocks.add(blockId);
                }
            }
        }

        return uniqueBlocks.size();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (Screen.hasAltDown()) {
            if (level.isClientSide) {
                openConfigurationUI(stack);
            }
            return InteractionResultHolder.success(stack);
        }

        if (player.isCrouching()) {
            if (level.isClientSide) {
                return InteractionResultHolder.success(stack);
            }
            return generateStructureTxt(stack, (ServerPlayer) player, level);
        }

        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;

        var hitResult = player.pick(5.0, 0.0f, false);

        if (hitResult.getType() != net.minecraft.world.phys.HitResult.Type.BLOCK) {
            player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.look_at_block")
                    .withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        BlockPos pos = ((net.minecraft.world.phys.BlockHitResult) hitResult).getBlockPos();

        return markPosition(stack, serverPlayer, pos);
    }

    public InteractionResultHolder<ItemStack> clearAllSelections(ItemStack stack, ServerPlayer player) {
        CompoundTag tag = stack.getOrCreateTag();

        boolean hadPos1 = tag.contains(TAG_POS1);
        boolean hadPos2 = tag.contains(TAG_POS2);
        boolean hadStructName = tag.contains(TAG_STRUCT_NAME) && !tag.getString(TAG_STRUCT_NAME).isEmpty();

        if (hadPos1 || hadPos2 || hadStructName) {
            tag.remove(TAG_POS1);
            tag.remove(TAG_POS2);
            tag.remove(TAG_STRUCT_NAME);

            player.displayClientMessage(
                    Component.translatable("message.gtlendless.multiblock_scanner.cleared_selection")
                            .withStyle(ChatFormatting.GREEN),
                    true
            );
        } else {
            player.displayClientMessage(
                    Component.translatable("message.gtlendless.multiblock_scanner.nothing_to_clear")
                            .withStyle(ChatFormatting.GRAY),
                    true
            );
        }

        return InteractionResultHolder.success(stack);
    }

    private void openConfigurationUI(ItemStack stack) {
        Minecraft.getInstance().setScreen(new ScannerConfigScreen(stack));
    }

    private InteractionResultHolder<ItemStack> markPosition(ItemStack stack, ServerPlayer player, BlockPos pos) {
        CompoundTag tag = stack.getOrCreateTag();

        if (!tag.contains(TAG_POS1)) {
            savePosToNBT(tag, TAG_POS1, pos);
            player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.marked_first",
                    formatPos(pos)), true);
        } else if (!tag.contains(TAG_POS2)) {
            savePosToNBT(tag, TAG_POS2, pos);
            player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.marked_second",
                    formatPos(pos)), true);

            player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.both_marked"), true);
        } else {
            savePosToNBT(tag, TAG_POS1, pos);
            tag.remove(TAG_POS2);
            player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.reset_first",
                    formatPos(pos)), true);
        }

        return InteractionResultHolder.success(stack);
    }

    private InteractionResultHolder<ItemStack> generateStructureTxt(ItemStack stack, ServerPlayer player, Level level) {
        CompoundTag tag = stack.getOrCreateTag();

        if (!tag.contains(TAG_POS1) || !tag.contains(TAG_POS2)) {
            player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.mark_two_points")
                    .withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        BlockPos pos1 = getPosFromNBT(tag, TAG_POS1);
        BlockPos pos2 = getPosFromNBT(tag, TAG_POS2);

        if (pos1 == null || pos2 == null) {
            player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.position_error")
                    .withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        int minX = Math.min(pos1.getX(), pos2.getX());
        int minY = Math.min(pos1.getY(), pos2.getY());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());

        pos1 = new BlockPos(minX, minY, minZ);
        pos2 = new BlockPos(maxX, maxY, maxZ);

        int maxAllowedBlockTypes = CHAR_SET.length - 3;
        int blockTypeCount = countBlockTypes(level, pos1, pos2);

        if (blockTypeCount > maxAllowedBlockTypes) {
            player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.too_many_block_types")
                    .append(Component.literal(" (" + blockTypeCount + "/" + maxAllowedBlockTypes + ")"))
                    .withStyle(ChatFormatting.RED), true);
            player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.simplify_structure")
                    .withStyle(ChatFormatting.YELLOW), true);
            return InteractionResultHolder.fail(stack);
        }

        player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.block_type_count")
                .append(Component.literal(": " + blockTypeCount + "/" + maxAllowedBlockTypes))
                .withStyle(ChatFormatting.GREEN), true);

        boolean hasScannerController = false;
        BlockPos scannerControllerPos = null;
        Direction scannerControllerFacing = null;
        Direction recordedPlayerFacing = null;

        for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int x = minX; x <= maxX; x++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    Block block = state.getBlock();
                    ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);

                    if (blockId != null && blockId.getNamespace().equals("gtlendless") &&
                            blockId.getPath().equals("scanner_controller")) {
                        if (hasScannerController) {
                            player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.too_many_controllers")
                                    .withStyle(ChatFormatting.RED), true);
                            return InteractionResultHolder.fail(stack);
                        }
                        hasScannerController = true;
                        scannerControllerPos = pos;

                        if (level.getBlockEntity(pos) instanceof IMachineBlockEntity machineBlockEntity) {
                            MetaMachine machine = machineBlockEntity.getMetaMachine();
                            if (machine instanceof github.forilusa.gtlendless.machine.CustomPartAbility.IScannerController scannerController) {
                                if (!scannerController.hasFacingRecorded()) {
                                    player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.no_player_facing")
                                            .withStyle(ChatFormatting.RED), true);
                                    player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.replace_controller")
                                            .withStyle(ChatFormatting.YELLOW), true);
                                    return InteractionResultHolder.fail(stack);
                                }

                                scannerControllerFacing = scannerController.getBlockFrontFacing();
                                recordedPlayerFacing = scannerController.getRecordedPlayerFacing();

                                player.displayClientMessage(
                                        Component.translatable("message.gtlendless.multiblock_scanner.using_recorded_facing")
                                                .append(Component.literal(": " + getDirectionName(recordedPlayerFacing)))
                                                .withStyle(ChatFormatting.GREEN),
                                        true);

                                if (scannerControllerFacing == Direction.UP || scannerControllerFacing == Direction.DOWN) {
                                    player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.controller_facing_up_down")
                                            .withStyle(ChatFormatting.RED), true);
                                    return InteractionResultHolder.fail(stack);
                                }

                            } else {
                                player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.no_player_facing")
                                        .withStyle(ChatFormatting.RED), true);
                                player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.replace_controller")
                                        .withStyle(ChatFormatting.YELLOW), true);
                                return InteractionResultHolder.fail(stack);
                            }
                        } else {
                            player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.no_player_facing")
                                    .withStyle(ChatFormatting.RED), true);
                            player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.replace_controller")
                                    .withStyle(ChatFormatting.YELLOW), true);
                            return InteractionResultHolder.fail(stack);
                        }
                    }
                }
            }
        }

        if (!hasScannerController) {
            player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.no_controller")
                    .withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        String structName = tag.getString(TAG_STRUCT_NAME);
        if (structName.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            structName = "Structure_" + sdf.format(new java.util.Date());
            tag.putString(TAG_STRUCT_NAME, structName);
        }

        try {
            String txtContent = scanStructureToTxt(stack, level, pos1, pos2, structName,
                    scannerControllerPos, scannerControllerFacing, recordedPlayerFacing);
            boolean success = saveToTxtFile(structName, txtContent);

            if (success) {
                player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.export_success")
                        .withStyle(ChatFormatting.GREEN), true);
                player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.save_path",
                        structName).withStyle(ChatFormatting.YELLOW), true);

                tag.remove(TAG_POS1);
                tag.remove(TAG_POS2);
                tag.remove(TAG_STRUCT_NAME);
            } else {
                player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.save_failed")
                        .withStyle(ChatFormatting.RED), true);
            }

        } catch (Exception e) {
            player.displayClientMessage(Component.translatable("message.gtlendless.multiblock_scanner.export_error",
                    e.getMessage()).withStyle(ChatFormatting.RED), true);
        }

        return InteractionResultHolder.success(stack);
    }

    private String scanStructureToTxt(ItemStack stack, Level level, BlockPos minPos, BlockPos maxPos, String structName,
                                      BlockPos controllerPos, Direction controllerFacing, Direction recordedPlayerFacing) {
        boolean globalMode = ScannerConfig.globalMode;
        boolean compactOutput = ScannerConfig.compactOutput;
        boolean renderMode = ScannerConfig.renderMode;

        StringBuilder txt = new StringBuilder();

        if (!compactOutput) {
            txt.append("=== 多方块结构扫描数据 ===\n");
            txt.append("结构名称: ").append(structName).append("\n");
            txt.append("扫描范围: ").append(minPos.toShortString()).append(" 到 ").append(maxPos.toShortString()).append("\n");
            txt.append("扫描器控制器位置: ").append(controllerPos.toShortString()).append("\n");
            txt.append("方块正面朝向: ").append(getDirectionName(controllerFacing)).append("\n");
            txt.append("记录玩家面向: ").append(getDirectionName(recordedPlayerFacing)).append("\n");
            txt.append("生成时间: ").append(new java.util.Date()).append("\n");
            txt.append("当前配置: ");
            txt.append("空气模式=").append(globalMode ? "全局" : "边缘");
            txt.append(", 输出模式=").append(compactOutput ? "精简" : "完整");
            txt.append(", 渲染模式=").append(renderMode ? "开启" : "关闭");
            txt.append("\n");
            txt.append("由 GTL-Endless 多方块扫描器生成\n");
            txt.append("===========================\n\n");
        }

        int sizeX = maxPos.getX() - minPos.getX() + 1;
        int sizeY = maxPos.getY() - minPos.getY() + 1;
        int sizeZ = maxPos.getZ() - minPos.getZ() + 1;

        Map<Block, Character> blockToChar = new HashMap<>();
        Map<Character, BlockInfo> charToBlockInfo = new HashMap<>();

        blockToChar.put(net.minecraft.world.level.block.Blocks.AIR, 'A');
        charToBlockInfo.put('A', new BlockInfo("空气", "minecraft:air"));

        Block scannerControllerBlock = ForgeRegistries.BLOCKS.getValue(
                new ResourceLocation("gtlendless", "scanner_controller")
        );
        if (scannerControllerBlock != null) {
            blockToChar.put(scannerControllerBlock, 'K');
            charToBlockInfo.put('K', new BlockInfo("扫描器控制器", "gtlendless:scanner_controller"));
        }

        charToBlockInfo.put('#', new BlockInfo("任意方块", "any"));

        int charIndex = 0;
        int maxUsableChars = CHAR_SET.length - 3;

        for (int y = minPos.getY(); y <= maxPos.getY(); y++) {
            for (int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
                for (int x = minPos.getX(); x <= maxPos.getX(); x++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    Block block = state.getBlock();

                    if (block == net.minecraft.world.level.block.Blocks.AIR || block == scannerControllerBlock) {
                        continue;
                    }

                    if (!blockToChar.containsKey(block)) {
                        if (charIndex >= maxUsableChars) {
                            blockToChar.put(block, 'X');
                            charToBlockInfo.put('X', new BlockInfo("超出字符限制的方块", "error:exceeded_char_limit"));
                            continue;
                        }

                        char c = CHAR_SET[charIndex];
                        while (charToBlockInfo.containsKey(c)) {
                            charIndex++;
                            if (charIndex >= CHAR_SET.length) {
                                charIndex = 0;
                            }
                            c = CHAR_SET[charIndex];
                        }

                        charIndex++;

                        blockToChar.put(block, c);

                        String blockName = block.getName().getString();
                        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
                        String blockIdStr = blockId != null ? blockId.toString() : "unknown:unknown";

                        charToBlockInfo.put(c, new BlockInfo(blockName, blockIdStr));
                    }
                }
            }
        }

        char[][][] blockChars = new char[sizeZ][sizeY][sizeX];

        for (int y = minPos.getY(); y <= maxPos.getY(); y++) {
            int yIndex = y - minPos.getY();
            for (int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
                int zIndex = z - minPos.getZ();
                for (int x = minPos.getX(); x <= maxPos.getX(); x++) {
                    int xIndex = x - minPos.getX();
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    Block block = state.getBlock();
                    char c = blockToChar.getOrDefault(block, '?');
                    blockChars[zIndex][yIndex][xIndex] = c;
                }
            }
        }

        int controllerX = controllerPos.getX() - minPos.getX();
        int controllerY = controllerPos.getY() - minPos.getY();
        int controllerZ = controllerPos.getZ() - minPos.getZ();

        if (!compactOutput) {
            txt.append("=== 方块映射表 ===\n");
            for (Map.Entry<Character, BlockInfo> entry : charToBlockInfo.entrySet()) {
                char c = entry.getKey();
                BlockInfo info = entry.getValue();
                txt.append(c).append(" = ").append(info.name).append(" (").append(info.id).append(")\n");
            }
            txt.append("=================\n\n");
        }

        if (!compactOutput) {
            txt.append("=== 结构层数据 ===\n");
            txt.append("总层数: ").append(sizeY).append("\n");
            txt.append("每层大小: ").append(sizeX).append("x").append(sizeZ).append("\n");
            txt.append("扫描器控制器位于第 ").append(controllerY + 1).append(" 层，位置: X=")
                    .append(controllerX).append(", Z=").append(controllerZ).append("\n");
            txt.append("方块正面朝向: ").append(getDirectionName(controllerFacing)).append("\n");
            txt.append("记录玩家面向: ").append(getDirectionName(recordedPlayerFacing)).append("\n\n");

            for (int offset = 0; offset < sizeY; offset++) {
                int yIndex;
                if (offset == 0) {
                    yIndex = controllerY;
                } else if (offset % 2 == 1) {
                    yIndex = controllerY + (offset + 1) / 2;
                    if (yIndex >= sizeY) continue;
                } else {
                    yIndex = controllerY - offset / 2;
                    if (yIndex < 0) continue;
                }

                int layerNumber = yIndex + 1;
                txt.append("--- 第 ").append(layerNumber).append(" 层 (Y=").append(minPos.getY() + yIndex);
                if (yIndex == controllerY) {
                    txt.append(", 控制器层");
                }
                txt.append(") ---\n");

                for (int zIndex = 0; zIndex < sizeZ; zIndex++) {
                    StringBuilder row = new StringBuilder();
                    for (int xIndex = 0; xIndex < sizeX; xIndex++) {
                        if (yIndex == controllerY && zIndex == controllerZ && xIndex == controllerX) {
                            row.append('K');
                        } else {
                            row.append(blockChars[zIndex][yIndex][xIndex]);
                        }
                    }
                    txt.append(row.toString());
                    if (zIndex < sizeZ - 1) {
                        txt.append("\n");
                    }
                }
                txt.append("\n\n");
            }
        }

        txt.append("=== GTCEu 格式建议 ===\n");
        txt.append("// 注意：此结构已根据记录玩家面向方向旋转到朝北\n");
        txt.append("// 原始玩家面向方向: ").append(getDirectionName(recordedPlayerFacing)).append("\n");
        txt.append("// 原始方块正面朝向: ").append(getDirectionName(controllerFacing)).append("\n");
        txt.append("// 空气替换模式: ").append(globalMode ? "全局模式" : "边缘模式").append("\n");
        txt.append("// 文件输出模式: ").append(compactOutput ? "精简输出" : "完整输出").append("\n\n");

        int rotateAngle = getRotationAngleForRecordedFacing(recordedPlayerFacing);
        txt.append("FactoryBlockPattern.start()\n");

        int rotatedSizeX = sizeX;
        int rotatedSizeZ = sizeZ;

        if (rotateAngle == 90 || rotateAngle == 270) {
            rotatedSizeX = sizeZ;
            rotatedSizeZ = sizeX;
        }

        char[][][] rotatedChars = new char[rotatedSizeZ][sizeY][rotatedSizeX];

        for (int z = 0; z < rotatedSizeZ; z++) {
            for (int y = 0; y < sizeY; y++) {
                for (int x = 0; x < rotatedSizeX; x++) {
                    rotatedChars[z][y][x] = 'A';
                }
            }
        }

        int rotatedControllerX = 0;
        int rotatedControllerZ = 0;

        double centerX = (sizeX - 1) / 2.0;
        double centerZ = (sizeZ - 1) / 2.0;
        double newCenterX = (rotatedSizeX - 1) / 2.0;
        double newCenterZ = (rotatedSizeZ - 1) / 2.0;

        for (int z = 0; z < sizeZ; z++) {
            for (int y = 0; y < sizeY; y++) {
                for (int x = 0; x < sizeX; x++) {
                    char c = blockChars[z][y][x];
                    if (c == 'A') continue;

                    double dx = x - centerX;
                    double dz = z - centerZ;

                    double newDx, newDz;

                    switch (rotateAngle) {
                        case 0:
                            newDx = dx;
                            newDz = dz;
                            break;
                        case 90:
                            newDx = -dz;
                            newDz = dx;
                            break;
                        case 180:
                            newDx = -dx;
                            newDz = -dz;
                            break;
                        case 270:
                            newDx = dz;
                            newDz = -dx;
                            break;
                        default:
                            newDx = dx;
                            newDz = dz;
                    }

                    int newX = (int) Math.round(newDx + newCenterX);
                    int newZ = (int) Math.round(newDz + newCenterZ);

                    if (newX >= 0 && newX < rotatedSizeX && newZ >= 0 && newZ < rotatedSizeZ) {
                        rotatedChars[newZ][y][newX] = c;

                        if (y == controllerY && z == controllerZ && x == controllerX) {
                            rotatedControllerX = newX;
                            rotatedControllerZ = newZ;
                        }
                    }
                }
            }
        }

        rotatedChars[rotatedControllerZ][controllerY][rotatedControllerX] = 'K';

        if (globalMode) {
            for (int z = 0; z < rotatedSizeZ; z++) {
                for (int y = 0; y < sizeY; y++) {
                    for (int x = 0; x < rotatedSizeX; x++) {
                        if (rotatedChars[z][y][x] == 'A') {
                            rotatedChars[z][y][x] = '#';
                        }
                    }
                }
            }
        } else {
            for (int z = 0; z < rotatedSizeZ; z++) {
                for (int y = 0; y < sizeY; y++) {
                    for (int x = 0; x < rotatedSizeX; x++) {
                        boolean isEdge = (x == 0 || x == rotatedSizeX - 1 ||
                                z == 0 || z == rotatedSizeZ - 1);

                        if (isEdge && rotatedChars[z][y][x] == 'A') {
                            rotatedChars[z][y][x] = '#';
                        }
                    }
                }
            }
        }

        for (int z = 0; z < rotatedSizeZ; z++) {
            List<String> layerStrings = new ArrayList<>();

            for (int y = 0; y < sizeY; y++) {
                StringBuilder layer = new StringBuilder();
                for (int x = 0; x < rotatedSizeX; x++) {
                    layer.append(rotatedChars[z][y][x]);
                }
                layerStrings.add(layer.toString());
            }

            txt.append("    .aisle(\"").append(String.join("\", \"", layerStrings)).append("\")\n");
        }

        txt.append("    .where('A', Predicates.air())\n");
        txt.append("    .where('K', Predicates.blocks(gtlendlessBlocks.ScannerController))\n");
        txt.append("    .where('#', Predicates.any())\n");

        for (Map.Entry<Character, BlockInfo> entry : charToBlockInfo.entrySet()) {
            char c = entry.getKey();
            if (c != 'A' && c != 'K' && c != '#') {
                String blockRef = formatBlockIdForGTCEu(entry.getValue().id);
                txt.append("    .where('").append(c).append("', Predicates.blocks(")
                        .append(blockRef).append("))\n");
            }
        }

        txt.append("    .build())\n");

        return txt.toString();
    }

    private int getRotationAngleForRecordedFacing(Direction recordedPlayerFacing) {
        switch (recordedPlayerFacing) {
            case NORTH: return 0;
            case EAST:  return 270;
            case SOUTH: return 180;
            case WEST:  return 90;
            default:    return 0;
        }
    }

    private String formatBlockIdForGTCEu(String blockIdStr) {
        if (blockIdStr.startsWith("minecraft:")) {
            String path = blockIdStr.substring(10);
            return "Blocks." + path.toUpperCase();
        } else if (blockIdStr.startsWith("gtceu:")) {
            String path = blockIdStr.substring(6);
            return "GTBlocks." + toCamelCase(path);
        } else if (blockIdStr.startsWith("gtlendless:")) {
            String path = blockIdStr.substring(11);
            return "gtlendlessBlocks." + toCamelCase(path);
        } else {
            String[] parts = blockIdStr.split(":");
            if (parts.length == 2) {
                return parts[0] + "Blocks." + toCamelCase(parts[1]);
            }
            return "Blocks.STONE";
        }
    }

    private String toCamelCase(String str) {
        String[] parts = str.split("_");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    private boolean saveToTxtFile(String fileName, String content) {
        try {
            Path dir = Paths.get("config/gtlendless/structures");
            Files.createDirectories(dir);

            Path file = dir.resolve(fileName + ".txt");
            Files.write(file, content.getBytes());

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void savePosToNBT(CompoundTag tag, String key, BlockPos pos) {
        tag.putIntArray(key, new int[]{pos.getX(), pos.getY(), pos.getZ()});
    }

    @Nullable
    private BlockPos getPosFromNBT(CompoundTag tag, String key) {
        if (!tag.contains(key)) {
            return null;
        }
        int[] posArray = tag.getIntArray(key);
        if (posArray.length != 3) {
            return null;
        }
        return new BlockPos(posArray[0], posArray[1], posArray[2]);
    }

    private String formatPos(BlockPos pos) {
        return String.format("(%d, %d, %d)", pos.getX(), pos.getY(), pos.getZ());
    }

    private String getDirectionName(Direction direction) {
        switch (direction) {
            case NORTH: return "北";
            case SOUTH: return "南";
            case EAST: return "东";
            case WEST: return "西";
            case UP: return "上";
            case DOWN: return "下";
            default: return direction.getName();
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(animationConfig.getTitleAnimation().apply(animationConfig.getTitleKey()));
        tooltip.add(Component.literal(""));

        tooltip.add(Component.translatable(animationConfig.getDescriptionKey()));
        tooltip.add(Component.literal(""));

        for (String lineKey : animationConfig.getAdditionalLines()) {
            tooltip.add(Component.translatable(lineKey));
        }

        if (animationConfig.getAdditionalLines().length > 0) {
            tooltip.add(Component.literal(""));
        }

        tooltip.add(Component.translatable("tooltip.gtlendless.multiblock_scanner.config_usage"));
        tooltip.add(Component.translatable("tooltip.gtlendless.multiblock_scanner.config1",
                ScannerConfig.globalMode ? "§a全局" : "§7边缘"));
        tooltip.add(Component.translatable("tooltip.gtlendless.multiblock_scanner.config2",
                ScannerConfig.compactOutput ? "§a精简" : "§7完整"));
        tooltip.add(Component.translatable("tooltip.gtlendless.multiblock_scanner.config3",
                ScannerConfig.renderMode ? "§a开启" : "§7关闭"));
        tooltip.add(Component.literal(""));

        CompoundTag tag = stack.getTag();
        if (tag != null) {
            BlockPos pos1 = getPosFromNBT(tag, TAG_POS1);
            BlockPos pos2 = getPosFromNBT(tag, TAG_POS2);
            String structName = tag.getString(TAG_STRUCT_NAME);

            if (pos1 != null) {
                tooltip.add(Component.translatable("tooltip.gtlendless.multiblock_scanner.position1",
                        formatPos(pos1)).withStyle(ChatFormatting.GREEN));
            }
            if (pos2 != null) {
                tooltip.add(Component.translatable("tooltip.gtlendless.multiblock_scanner.position2",
                        formatPos(pos2)).withStyle(ChatFormatting.GREEN));
            }
            if (!structName.isEmpty()) {
                tooltip.add(Component.translatable("tooltip.gtlendless.multiblock_scanner.struct_name",
                        structName).withStyle(ChatFormatting.YELLOW));
            }
        }

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("tooltip.gtlendless.multiblock_scanner.usage_title"));
            tooltip.add(Component.translatable("tooltip.gtlendless.multiblock_scanner.step1"));
            tooltip.add(Component.translatable("tooltip.gtlendless.multiblock_scanner.step2"));
            tooltip.add(Component.translatable("tooltip.gtlendless.multiblock_scanner.step3"));
            tooltip.add(Component.translatable("tooltip.gtlendless.multiblock_scanner.step4"));
            tooltip.add(Component.translatable("tooltip.gtlendless.multiblock_scanner.step5"));
        } else {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("tooltip.gtlendless.multiblock_scanner.hold_shift")
                    .withStyle(ChatFormatting.GRAY));
        }

        tooltip.add(Component.literal(""));
        tooltip.add(animationConfig.getAuthorAnimation().apply("gtlendless.by"));
    }

    private static class BlockInfo {
        final String name;
        final String id;

        BlockInfo(String name, String id) {
            this.name = name;
            this.id = id;
        }
    }
}