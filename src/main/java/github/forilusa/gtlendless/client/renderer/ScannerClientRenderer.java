package github.forilusa.gtlendless.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import github.forilusa.gtlendless.config.ScannerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

//  扫描器渲染功能
@Mod.EventBusSubscriber(modid = "gtlendless", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
@OnlyIn(Dist.CLIENT)
public class ScannerClientRenderer {

    private static final String TAG_POS1 = "Pos1";
    private static final String TAG_POS2 = "Pos2";
    private static final float FRAME_OFFSET = 0.001f;
    private static final float SINGLE_BLOCK_OFFSET = 0.0001f;

    private static final int[] RAINBOW_COLORS = {
            0xFF0000,
            0xFF7F00,
            0xFFFF00,
            0x00FF00,
            0x0000FF,
            0x4B0082,
            0x8B00FF
    };

    public enum ScannerError {
        NONE,
        NO_CONTROLLER,
        MULTIPLE_CONTROLLERS,
        TOO_MANY_BLOCK_TYPES
    }

    private static final int MAX_ALLOWED_BLOCK_TYPES = 298 - 3;

    public static class ScreenErrorDisplay {
        private static ScannerError currentError = ScannerError.NONE;
        private static String customMessage = "";

        public static void showError(ScannerError error, String message) {
            currentError = error;
            customMessage = message;
        }

        public static void clearError() {
            currentError = ScannerError.NONE;
            customMessage = "";
        }

        public static boolean isDisplaying() {
            return ScannerConfig.screenErrorDisplay && currentError != ScannerError.NONE;
        }

        public static ScannerError getCurrentError() {
            return currentError;
        }

        public static String getMessage() {
            return customMessage;
        }
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) {
            ScreenErrorDisplay.clearError();
            return;
        }
        if (mc.level == null) {
            ScreenErrorDisplay.clearError();
            return;
        }

        if (!ScannerConfig.renderMode) {
            ScreenErrorDisplay.clearError();
            return;
        }

        ItemStack scannerStack = findScannerStack(player);
        if (scannerStack == null) {
            ScreenErrorDisplay.clearError();
            return;
        }

        CompoundTag tag = scannerStack.getTag();
        if (tag == null) {
            ScreenErrorDisplay.clearError();
            return;
        }

        BlockPos pos1 = getPosFromNBT(tag, TAG_POS1);
        BlockPos pos2 = getPosFromNBT(tag, TAG_POS2);

        if (pos1 == null && pos2 == null) {
            ScreenErrorDisplay.clearError();
            return;
        }

        ScannerError error = ScannerError.NONE;
        if (pos1 != null && pos2 != null) {
            error = checkForErrors(mc.level, pos1, pos2);

            if (error != ScannerError.NONE) {
                String errorMsg = getErrorMessage(error);
                if (!ScreenErrorDisplay.isDisplaying() || ScreenErrorDisplay.getCurrentError() != error) {
                    ScreenErrorDisplay.showError(error, errorMsg);
                }
            } else {
                ScreenErrorDisplay.clearError();
            }
        } else {
            ScreenErrorDisplay.clearError();
        }

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        poseStack.pushPose();
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        try {
            if (pos1 != null) {
                renderSingleBlockOutline(poseStack, bufferSource, pos1, 0.0f, 0.4f, 1.0f, 0.8f);
            }
            if (pos2 != null) {
                renderSingleBlockOutline(poseStack, bufferSource, pos2, 1.0f, 0.65f, 0.0f, 0.8f);
            }
            if (pos1 != null && pos2 != null) {
                renderSelectionAreaWithError(poseStack, bufferSource, pos1, pos2, error);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bufferSource.endBatch();
            poseStack.popPose();
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (!ScannerConfig.screenErrorDisplay || !ScreenErrorDisplay.isDisplaying()) {
            return;
        }

        try {
            renderScreenErrorInGUI(Minecraft.getInstance(), event.getGuiGraphics(), event.getPartialTick());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void renderScreenErrorInGUI(Minecraft mc, GuiGraphics guiGraphics, float partialTick) {
        ScannerError error = ScreenErrorDisplay.getCurrentError();
        String errorMessage = ScreenErrorDisplay.getMessage();

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        float scale = 1.5f;
        Font font = mc.font;

        int textWidth = font.width(errorMessage);
        float scaledTextWidth = textWidth * scale;
        int textHeight = font.lineHeight;
        float scaledTextHeight = textHeight * scale;

        int x = screenWidth / 2 - (int) (scaledTextWidth / 2);
        int y = screenHeight / 2 - (int) (scaledTextHeight / 2);

        renderTextWithEffectInGUI(guiGraphics, font, errorMessage, x, y, scale, error);
    }

    private static void renderTextWithEffectInGUI(GuiGraphics guiGraphics, Font font, String text,
                                                  int x, int y, float scale, ScannerError error) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(x, y, 0);
        poseStack.scale(scale, scale, 1.0f);

        renderTextWithEffect(guiGraphics, font, text, scale, error);

        poseStack.popPose();
    }

    private static void renderTextWithEffect(GuiGraphics guiGraphics, Font font, String text,
                                             float scale, ScannerError error) {
        PoseStack poseStack = guiGraphics.pose();

        long time = System.currentTimeMillis();
        float pulseFactor = (float) ((Math.sin(time / 300.0) * 0.2) + 1.0);

        float brightnessPulse = (float) (1.0 + 0.3 * Math.sin(time / 200.0));

        if (pulseFactor > 1.1f) {
            int glowAlpha = (int) ((pulseFactor - 1.1f) * 3.33f * 100);
            int glowColor = (glowAlpha << 24) | 0xFFFFFF;

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    guiGraphics.drawString(font, text, dx, dy, glowColor, false);
                }
            }
        }

        int baseColor = getMainColorForError(error);

        int mainColor = applyBrightnessToColor(baseColor, brightnessPulse);
        guiGraphics.drawString(font, text, 0, 0, mainColor, false);

        if (pulseFactor > 1.15f) {
            int highlightAlpha = (int) ((pulseFactor - 1.15f) * 5 * 40);
            int highlightColor = (highlightAlpha << 24) | 0xFFFFFF;
            guiGraphics.drawString(font, text, 0, -1, highlightColor, false);
        }
    }

    private static int applyBrightnessToColor(int color, float brightness) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;

        r = (int) (r * brightness);
        g = (int) (g * brightness);
        b = (int) (b * brightness);

        r = Math.min(255, Math.max(0, r));
        g = Math.min(255, Math.max(0, g));
        b = Math.min(255, Math.max(0, b));

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int getMainColorForError(ScannerError error) {
        switch (error) {
            case NO_CONTROLLER:
                return 0xFFFF5555;
            case MULTIPLE_CONTROLLERS:
                return 0xFFFF55FF;
            case TOO_MANY_BLOCK_TYPES:
                return 0xFFFFFF55;
            default:
                return 0xFFFF5555;
        }
    }

    private static ScannerError checkForErrors(Level level, BlockPos pos1, BlockPos pos2) {
        int minX = Math.min(pos1.getX(), pos2.getX());
        int minY = Math.min(pos1.getY(), pos2.getY());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());

        Set<ResourceLocation> uniqueBlocks = new HashSet<>();
        ResourceLocation scannerControllerId = new ResourceLocation("gtlendless", "scanner_controller");
        int controllerCount = 0;

        for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int x = minX; x <= maxX; x++) {
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
                        controllerCount++;
                    } else {
                        uniqueBlocks.add(blockId);
                    }
                }
            }
        }

        if (controllerCount == 0) {
            return ScannerError.NO_CONTROLLER;
        }

        if (controllerCount > 1) {
            return ScannerError.MULTIPLE_CONTROLLERS;
        }

        if (uniqueBlocks.size() > MAX_ALLOWED_BLOCK_TYPES) {
            return ScannerError.TOO_MANY_BLOCK_TYPES;
        }

        return ScannerError.NONE;
    }

    private static void renderSelectionAreaWithError(PoseStack poseStack,
                                                     MultiBufferSource.BufferSource bufferSource,
                                                     BlockPos pos1, BlockPos pos2, ScannerError error) {

        int minX = Math.min(pos1.getX(), pos2.getX());
        int minY = Math.min(pos1.getY(), pos2.getY());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());

        int sizeX = maxX - minX + 1;
        int sizeY = maxY - minY + 1;
        int sizeZ = maxZ - minZ + 1;

        AABB box = new AABB(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1);

        float[] areaColor = getAreaColorForError(error);
        renderSolidBox(poseStack, bufferSource, box,
                areaColor[0], areaColor[1], areaColor[2], areaColor[3]);

        float[] borderColor = getBorderColorForError(error);
        renderCopy(poseStack, bufferSource, pos1, pos2,
                new Color(borderColor[0], borderColor[1], borderColor[2], borderColor[3]));

        renderErrorText(poseStack, bufferSource,
                minX, minY, minZ, maxX, maxY, maxZ, error);
    }

    private static float[] getAreaColorForError(ScannerError error) {
        switch (error) {
            case NONE:
                return new float[]{0.0f, 1.0f, 0.0f, 0.15f};
            case NO_CONTROLLER:
                return new float[]{1.0f, 0.0f, 0.0f, 0.15f};
            case MULTIPLE_CONTROLLERS:
                return new float[]{0.8f, 0.0f, 0.8f, 0.15f};
            case TOO_MANY_BLOCK_TYPES:
                return new float[]{1.0f, 1.0f, 0.0f, 0.15f};
            default:
                return new float[]{1.0f, 1.0f, 0.0f, 0.15f};
        }
    }

    private static float[] getBorderColorForError(ScannerError error) {
        switch (error) {
            case NONE:
                return new float[]{0.0f, 1.0f, 0.0f, 0.8f};
            case NO_CONTROLLER:
                return new float[]{1.0f, 0.0f, 0.0f, 0.8f};
            case MULTIPLE_CONTROLLERS:
                return new float[]{0.8f, 0.0f, 0.8f, 0.8f};
            case TOO_MANY_BLOCK_TYPES:
                return new float[]{1.0f, 1.0f, 0.0f, 0.8f};
            default:
                return new float[]{1.0f, 1.0f, 0.0f, 0.8f};
        }
    }

    private static void renderErrorText(PoseStack poseStack,
                                        MultiBufferSource.BufferSource bufferSource,
                                        int minX, int minY, int minZ, int maxX, int maxY, int maxZ,
                                        ScannerError error) {

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        double centerX = (minX + maxX + 1) / 2.0;
        double centerY = (minY + maxY + 1) / 2.0;
        double centerZ = (minZ + maxZ + 1) / 2.0;

        long time = System.currentTimeMillis();

        String text = getErrorMessage(error);

        int sizeX = maxX - minX + 1;
        int sizeY = maxY - minY + 1;
        int sizeZ = maxZ - minZ + 1;
        int maxSize = Math.max(sizeX, Math.max(sizeY, sizeZ));
        int volume = sizeX * sizeY * sizeZ;

        float baseScale;
        if (volume == 1) {
            baseScale = 0.005f;
        } else {
            float scaleFactor = Math.min((float)Math.sqrt(maxSize), 5.477f);
            baseScale = 0.02f * scaleFactor;
            if (volume <= 8) {
                baseScale *= 0.7f;
            }
            baseScale = Math.max(0.008f, Math.min(baseScale, 0.1f));
        }

        if (error == ScannerError.NONE) {
            renderRainbowText(poseStack, bufferSource, font,
                    centerX, centerY, centerZ, text, baseScale, time);
        } else {
            renderPulsingErrorText(poseStack, bufferSource, font,
                    centerX, centerY, centerZ, text, baseScale, time, error);
        }
    }

    private static void renderRainbowText(PoseStack poseStack,
                                          MultiBufferSource.BufferSource bufferSource,
                                          Font font, double centerX, double centerY, double centerZ,
                                          String text, float baseScale, long time) {

        poseStack.pushPose();
        poseStack.translate(centerX, centerY, centerZ);
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(-baseScale, -baseScale, baseScale);

        float textWidth = font.width(text);
        float charOffset = -textWidth / 2.0f;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String charStr = String.valueOf(c);

            int colorIndex = (int)((time / 200 + i) % RAINBOW_COLORS.length);
            int color = RAINBOW_COLORS[colorIndex];

            float charWidth = font.width(charStr);
            font.drawInBatch(charStr,
                    charOffset, 0, color, false,
                    poseStack.last().pose(), bufferSource, Font.DisplayMode.SEE_THROUGH,
                    0, 0xF000F0);
            charOffset += charWidth;
        }

        poseStack.popPose();
    }

    private static void renderPulsingErrorText(PoseStack poseStack,
                                               MultiBufferSource.BufferSource bufferSource,
                                               Font font, double centerX, double centerY, double centerZ,
                                               String text, float baseScale, long time, ScannerError error) {

        float pulseFactor = (float) ((Math.sin(time / 300.0) * 0.2) + 1.0);
        float currentScale = baseScale * pulseFactor;

        poseStack.pushPose();
        poseStack.translate(centerX, centerY, centerZ);
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(-currentScale, -currentScale, currentScale);

        float charOffset = -font.width(text) / 2.0f;

        int numGhosts = 4;
        float ghostSpacing = 0.002f * currentScale;

        for (int ghost = numGhosts; ghost > 0; ghost--) {
            float ghostAlpha = 0.2f * (ghost / (float)numGhosts);
            float ghostOffset = ghost * ghostSpacing;

            PoseStack ghostPose = new PoseStack();
            ghostPose.last().pose().mul(poseStack.last().pose());
            ghostPose.last().normal().mul(poseStack.last().normal());
            ghostPose.translate(ghostOffset, ghostOffset, ghostOffset);

            float ghostCharOffset = charOffset;
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                String charStr = String.valueOf(c);

                int color = getErrorColorWithLoop(error, i, text.length(), ghostAlpha, time);

                float charWidth = font.width(charStr);
                font.drawInBatch(charStr,
                        ghostCharOffset, 0, color, false,
                        ghostPose.last().pose(), bufferSource, Font.DisplayMode.SEE_THROUGH,
                        0, 0xF000F0);
                ghostCharOffset += charWidth;
            }
        }

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String charStr = String.valueOf(c);

            int color = getErrorColorWithLoop(error, i, text.length(), 1.0f, time);

            float charWidth = font.width(charStr);
            font.drawInBatch(charStr,
                    charOffset, 0, color, false,
                    poseStack.last().pose(), bufferSource, Font.DisplayMode.SEE_THROUGH,
                    0, 0xF000F0);
            charOffset += charWidth;
        }

        poseStack.popPose();
    }

    private static int getErrorColorWithLoop(ScannerError error, int position, int total, float alpha, long time) {
        double timeFactor = (time / 200.0) % 1.0;
        double positionFactor = position / (double)total * 0.3;
        double loopFactor = (timeFactor + positionFactor) % 1.0;

        int r, g, b;

        switch (error) {
            case NO_CONTROLLER:
                r = (int) (0x80 + (0xFF - 0x80) * loopFactor);
                g = 0x00;
                b = 0x00;
                break;

            case MULTIPLE_CONTROLLERS:
                r = (int) (0x40 + (0x80 - 0x40) * loopFactor);
                g = 0x00;
                b = (int) (0x40 + (0x80 - 0x40) * loopFactor);
                break;

            case TOO_MANY_BLOCK_TYPES:
                r = (int) (0x80 + (0xFF - 0x80) * loopFactor);
                g = (int) (0x80 + (0xFF - 0x80) * loopFactor);
                b = 0x00;
                break;

            default:
                r = 0xFF;
                g = 0xFF;
                b = 0xFF;
                break;
        }

        r = Math.min(255, Math.max(0, r));
        g = Math.min(255, Math.max(0, g));
        b = Math.min(255, Math.max(0, b));

        int a = (int)(alpha * 255);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static String getErrorMessage(ScannerError error) {
        switch (error) {
            case NONE:
                return "Gtl-Endless";
            case NO_CONTROLLER:
                return net.minecraft.client.resources.language.I18n.get("gtlendless.scanner_render.error.no_controller");
            case MULTIPLE_CONTROLLERS:
                return net.minecraft.client.resources.language.I18n.get("gtlendless.scanner_render.error.multiple_controllers");
            case TOO_MANY_BLOCK_TYPES:
                return net.minecraft.client.resources.language.I18n.get("gtlendless.scanner_render.error.too_many_block_types");
            default:
                return net.minecraft.client.resources.language.I18n.get("gtlendless.scanner_render.error.unknown");
        }
    }

    private static void renderSingleBlockOutline(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource,
                                                 BlockPos pos, float r, float g, float b, float a) {
        AABB box = new AABB(
                pos.getX() - SINGLE_BLOCK_OFFSET,
                pos.getY() - SINGLE_BLOCK_OFFSET,
                pos.getZ() - SINGLE_BLOCK_OFFSET,
                pos.getX() + 1 + SINGLE_BLOCK_OFFSET,
                pos.getY() + 1 + SINGLE_BLOCK_OFFSET,
                pos.getZ() + 1 + SINGLE_BLOCK_OFFSET
        );
        VertexConsumer builder = bufferSource.getBuffer(RenderType.lines());
        poseStack.pushPose();
        var matrix4f = poseStack.last().pose();
        var normal = poseStack.last().normal();
        float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float y2 = (float) box.maxY;
        float z2 = (float) box.maxZ;
        builder.vertex(matrix4f, x1, y1, z1).color(r, g, b, a).normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, x2, y1, z1).color(r, g, b, a).normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, x2, y1, z1).color(r, g, b, a).normal(normal, 0.0F, 0.0F, 1.0F).endVertex();
        builder.vertex(matrix4f, x2, y1, z2).color(r, g, b, a).normal(normal, 0.0F, 0.0F, 1.0F).endVertex();
        builder.vertex(matrix4f, x2, y1, z2).color(r, g, b, a).normal(normal, -1.0F, 0.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, x1, y1, z2).color(r, g, b, a).normal(normal, -1.0F, 0.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, x1, y1, z2).color(r, g, b, a).normal(normal, 0.0F, 0.0F, -1.0F).endVertex();
        builder.vertex(matrix4f, x1, y1, z1).color(r, g, b, a).normal(normal, 0.0F, 0.0F, -1.0F).endVertex();
        builder.vertex(matrix4f, x1, y2, z1).color(r, g, b, a).normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, x2, y2, z1).color(r, g, b, a).normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, x2, y2, z1).color(r, g, b, a).normal(normal, 0.0F, 0.0F, 1.0F).endVertex();
        builder.vertex(matrix4f, x2, y2, z2).color(r, g, b, a).normal(normal, 0.0F, 0.0F, 1.0F).endVertex();
        builder.vertex(matrix4f, x2, y2, z2).color(r, g, b, a).normal(normal, -1.0F, 0.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, x1, y2, z2).color(r, g, b, a).normal(normal, -1.0F, 0.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, x1, y2, z2).color(r, g, b, a).normal(normal, 0.0F, 0.0F, -1.0F).endVertex();
        builder.vertex(matrix4f, x1, y2, z1).color(r, g, b, a).normal(normal, 0.0F, 0.0F, -1.0F).endVertex();
        builder.vertex(matrix4f, x1, y1, z1).color(r, g, b, a).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, x1, y2, z1).color(r, g, b, a).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, x2, y1, z1).color(r, g, b, a).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, x2, y2, z1).color(r, g, b, a).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, x2, y1, z2).color(r, g, b, a).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, x2, y2, z2).color(r, g, b, a).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, x1, y1, z2).color(r, g, b, a).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, x1, y2, z2).color(r, g, b, a).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
        poseStack.popPose();
    }

    private static void renderSolidBox(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource,
                                       AABB box, float r, float g, float b, float a) {
        VertexConsumer builder = bufferSource.getBuffer(RenderType.lines());
        var matrix4f = poseStack.last().pose();
        var normal = poseStack.last().normal();
        float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float y2 = (float) box.maxY;
        float z2 = (float) box.maxZ;
        builder.vertex(matrix4f, x1, y1, z1).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x2, y1, z1).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x2, y1, z1).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x2, y1, z2).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x2, y1, z2).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x1, y1, z2).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x1, y1, z2).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x1, y1, z1).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x1, y2, z1).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x2, y2, z1).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x2, y2, z1).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x2, y2, z2).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x2, y2, z2).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x1, y2, z2).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x1, y2, z2).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x1, y2, z1).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x1, y1, z1).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x1, y2, z1).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x2, y1, z1).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x2, y2, z1).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x2, y1, z2).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x2, y2, z2).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x1, y1, z2).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix4f, x1, y2, z2).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
    }

    private static void renderCopy(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource,
                                   BlockPos startPos, BlockPos endPos, Color color) {
        if (startPos.equals(BlockPos.ZERO) || endPos.equals(BlockPos.ZERO)) return;
        int x = Math.min(startPos.getX(), endPos.getX());
        int y = Math.min(startPos.getY(), endPos.getY());
        int z = Math.min(startPos.getZ(), endPos.getZ());
        int dx = (startPos.getX() > endPos.getX()) ? startPos.getX() + 1 : endPos.getX() + 1;
        int dy = (startPos.getY() > endPos.getY()) ? startPos.getY() + 1 : endPos.getY() + 1;
        int dz = (startPos.getZ() > endPos.getZ()) ? startPos.getZ() + 1 : endPos.getZ() + 1;
        VertexConsumer builder = bufferSource.getBuffer(RenderType.lines());
        poseStack.pushPose();
        var matrix4f = poseStack.last().pose();
        var pose = poseStack.last();
        float r = color.getRed() / 255.0f;
        float g = color.getGreen() / 255.0f;
        float b = color.getBlue() / 255.0f;
        float a = color.getAlpha() / 255.0f;
        builder.vertex(matrix4f, x, y, z).color(r, g, b, a).normal(pose.normal(), 1.0F, 0.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, dx, y, z).color(r, g, b, a).normal(pose.normal(), 1.0F, 0.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, x, y, z).color(r, g, b, a).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, x, dy, z).color(r, g, b, a).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, x, y, z).color(r, g, b, a).normal(pose.normal(), 0.0F, 0.0F, 1.0F).endVertex();
        builder.vertex(matrix4f, x, y, dz).color(r, g, b, a).normal(pose.normal(), 0.0F, 0.0F, 1.0F).endVertex();
        builder.vertex(matrix4f, dx, y, z).color(r, g, b, a).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, dx, dy, z).color(r, g, b, a).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, dx, dy, z).color(r, g, b, a).normal(pose.normal(), -1.0F, 0.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, x, dy, z).color(r, g, b, a).normal(pose.normal(), -1.0F, 0.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, x, dy, z).color(r, g, b, a).normal(pose.normal(), 0.0F, 0.0F, 1.0F).endVertex();
        builder.vertex(matrix4f, x, dy, dz).color(r, g, b, a).normal(pose.normal(), 0.0F, 0.0F, 1.0F).endVertex();
        builder.vertex(matrix4f, x, dy, dz).color(r, g, b, a).normal(pose.normal(), 0.0F, -1.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, x, y, dz).color(r, g, b, a).normal(pose.normal(), 0.0F, -1.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, x, y, dz).color(r, g, b, a).normal(pose.normal(), 1.0F, 0.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, dx, y, dz).color(r, g, b, a).normal(pose.normal(), 1.0F, 0.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, dx, y, dz).color(r, g, b, a).normal(pose.normal(), 0.0F, 0.0F, -1.0F).endVertex();
        builder.vertex(matrix4f, dx, y, z).color(r, g, b, a).normal(pose.normal(), 0.0F, 0.0F, -1.0F).endVertex();
        builder.vertex(matrix4f, x, dy, dz).color(r, g, b, a).normal(pose.normal(), 1.0F, 0.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, dx, dy, dz).color(r, g, b, a).normal(pose.normal(), 1.0F, 0.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, dx, y, dz).color(r, g, b, a).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, dx, dy, dz).color(r, g, b, a).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
        builder.vertex(matrix4f, dx, dy, z).color(r, g, b, a).normal(pose.normal(), 0.0F, 0.0F, 1.0F).endVertex();
        builder.vertex(matrix4f, dx, dy, dz).color(r, g, b, a).normal(pose.normal(), 0.0F, 0.0F, 1.0F).endVertex();
        poseStack.popPose();
    }

    private static ItemStack findScannerStack(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        ResourceLocation mainHandId = ForgeRegistries.ITEMS.getKey(mainHand.getItem());
        ResourceLocation offHandId = ForgeRegistries.ITEMS.getKey(offHand.getItem());
        if (mainHandId != null && mainHandId.toString().equals("gtlendless:multiblock_scanner")) {
            return mainHand;
        }
        if (offHandId != null && offHandId.toString().equals("gtlendless:multiblock_scanner")) {
            return offHand;
        }
        String mainHandDesc = mainHand.getItem().getDescriptionId();
        String offHandDesc = offHand.getItem().getDescriptionId();
        if (mainHandDesc.contains("multiblock_scanner")) {
            return mainHand;
        }
        if (offHandDesc.contains("multiblock_scanner")) {
            return offHand;
        }
        return null;
    }

    private static BlockPos getPosFromNBT(CompoundTag tag, String key) {
        if (!tag.contains(key)) {
            return null;
        }
        int[] posArray = tag.getIntArray(key);
        if (posArray.length != 3) {
            return null;
        }
        return new BlockPos(posArray[0], posArray[1], posArray[2]);
    }
}