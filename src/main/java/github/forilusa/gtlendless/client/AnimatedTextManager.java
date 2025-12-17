package github.forilusa.gtlendless.client;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public class AnimatedTextManager {
    private static long clientTick = 0;

    // 颜色循环数组定义
    public static final int[] RAINBOW_CYCLE = {
            0xFF0000, // 红
            0xFF7F00, // 橙
            0xFFFF00, // 黄
            0x00FF00, // 绿
            0x0000FF, // 蓝
            0x4B0082, // 靛
            0x8B00FF  // 紫
    };

    public static final int[] BLINK_CYCLE = {
            0xFF0000, // 红
            0xFFFFFF, // 白
            0x00FF00, // 绿
            0x0000FF  // 蓝
    };

    public static final int[] BLACK_GRAY_CYCLE = {
            0x000000, // 纯黑
            0x222222, // 深灰
            0x444444, // 暗灰
            0x666666, // 中灰
            0x888888, // 浅灰
            0xAAAAAA, // 亮灰
            0xCCCCCC, // 更亮灰
            0xEEEEEE, // 几乎白色
            0xFFFFFF, // 纯白
            0xEEEEEE, // 几乎白色
            0xCCCCCC, // 更亮灰
            0xAAAAAA, // 亮灰
            0x888888, // 浅灰
            0x666666, // 中灰
            0x444444, // 暗灰
            0x222222  // 深灰
    };

    public static final int[] GOLDEN_CYCLE = {
            0xFFD700, // 金色
            0xFFEC8B, // 浅金色
            0xFFFF00, // 黄色
            0xFFF68F, // 浅黄色
            0xFFD700, // 金色
            0xDAA520, // 金菊黄
            0xB8860B, // 暗金色
            0xDAA520  // 金菊黄
    };

    public static final int[] DARK_RED_BLOOD_CYCLE = {
            0x8B0000, // 暗红色
            0xA52A2A, // 棕色红
            0xB22222, // 火砖红
            0xDC143C, // 深红
            0xFF0000, // 纯红
            0xFF4500, // 橙红
            0xFF0000, // 纯红
            0xDC143C, // 深红
            0xB22222, // 火砖红
            0xA52A2A  // 棕色红
    };

    public static final int[] CYAN_GREEN_CYCLE = {
            0x00FFFF, // 青色
            0x00CCCC, // 深青色
            0x009999, // 暗青色
            0x00FF99, // 青绿色
            0x00FF66, // 亮青绿
            0x00FF00, // 纯绿
            0x00FF66, // 亮青绿
            0x00FF99, // 青绿色
            0x009999, // 暗青色
            0x00CCCC  // 深青色
    };

    /**
     * 在客户端每tick调用，更新动画计数器
     */
    public static void onClientTick() {
        clientTick++;
    }

    /**
     * 创建动态颜色循环文本
     */
    public static MutableComponent createDynamicCyclingText(String text, int[] colorCycle, long cycleSpeed) {
        MutableComponent component = Component.literal("");
        int cycleLength = colorCycle.length;

        for (int i = 0; i < text.length(); i++) {
            long charTime = clientTick + (i * 2);
            int colorIndex = (int) ((charTime / cycleSpeed) % cycleLength);
            int color = colorCycle[colorIndex];

            Style style = Style.EMPTY
                    .withColor(TextColor.fromRgb(color))
                    .withItalic(false);

            component.append(Component.literal(String.valueOf(text.charAt(i))).withStyle(style));
        }

        return component;
    }

    /**
     * 创建闪烁标题文本
     */
    public static MutableComponent createBlinkingTitle(String text) {
        return createDynamicCyclingText(text, BLINK_CYCLE, 5L);
    }

    /**
     * 创建基于翻译键的闪烁标题文本
     */
    public static MutableComponent createDynamicTranslatedBlinkingTitle(String translationKey) {
        String translatedText = Component.translatable(translationKey).getString();
        return createBlinkingTitle(translatedText);
    }

    /**
     * 创建基于翻译键的动态灰白渐变动画文本
     */
    public static MutableComponent createDynamicTranslatedBlackGrayWave(String translationKey) {
        String translatedText = Component.translatable(translationKey).getString();
        return createDynamicCyclingText(translatedText, BLACK_GRAY_CYCLE, 6L);
    }

    /**
     * 创建基于翻译键的动态彩虹动画文本
     */
    public static MutableComponent createDynamicTranslatedRainbowWave(String translationKey) {
        String translatedText = Component.translatable(translationKey).getString();
        return createDynamicCyclingText(translatedText, RAINBOW_CYCLE, 5L);
    }

    /**
     * 创建基于翻译键的动态金色动画文本
     */
    public static MutableComponent createDynamicTranslatedGoldenWave(String translationKey) {
        String translatedText = Component.translatable(translationKey).getString();
        return createDynamicCyclingText(translatedText, GOLDEN_CYCLE, 6L);
    }

    /**
     * 创建基于翻译键的动态深红血液动画文本
     */
    public static MutableComponent createDynamicTranslatedDarkRedBloodWave(String translationKey) {
        String translatedText = Component.translatable(translationKey).getString();
        return createDynamicCyclingText(translatedText, DARK_RED_BLOOD_CYCLE, 6L);
    }

    /**
     * 创建基于翻译键的动态青绿动画文本
     */
    public static MutableComponent createDynamicTranslatedCyanGreenWave(String translationKey) {
        String translatedText = Component.translatable(translationKey).getString();
        return createDynamicCyclingText(translatedText, CYAN_GREEN_CYCLE, 5L);
    }

    /**
     * 直接创建黑灰波动文本（不通过翻译键）
     */
    public static MutableComponent createBlackGrayWave(String text) {
        return createDynamicCyclingText(text, BLACK_GRAY_CYCLE, 5L);
    }

    /**
     * 直接创建彩虹波动文本（不通过翻译键）
     */
    public static MutableComponent createRainbowWave(String text) {
        return createDynamicCyclingText(text, RAINBOW_CYCLE, 5L);
    }

    /**
     * 直接创建金色波动文本（不通过翻译键）
     */
    public static MutableComponent createGoldenWave(String text) {
        return createDynamicCyclingText(text, GOLDEN_CYCLE, 5L);
    }

    /**
     * 直接创建深红血液波动文本（不通过翻译键）
     */
    public static MutableComponent createDarkRedBloodWave(String text) {
        return createDynamicCyclingText(text, DARK_RED_BLOOD_CYCLE, 5L);
    }

    /**
     * 直接创建青绿波动文本（不通过翻译键）
     */
    public static MutableComponent createCyanGreenWave(String text) {
        return createDynamicCyclingText(text, CYAN_GREEN_CYCLE, 5L);
    }
}