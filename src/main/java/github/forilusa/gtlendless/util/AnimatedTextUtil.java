package github.forilusa.gtlendless.util;

import github.forilusa.gtlendless.client.AnimatedTextManager;
import net.minecraft.network.chat.MutableComponent;

public class AnimatedTextUtil {


    public static MutableComponent createCyclingText(String text, int[] colorCycle, long cycleSpeed) {
        return AnimatedTextManager.createDynamicCyclingText(text, colorCycle, cycleSpeed);
    }


    public static MutableComponent createBlinkingTitle(String text) {
        return AnimatedTextManager.createBlinkingTitle(text);
    }


    public static MutableComponent createTranslatedBlinkingTitle(String translationKey, Object... args) {
        String translatedText = net.minecraft.network.chat.Component.translatable(translationKey, args).getString();
        return createBlinkingTitle(translatedText);
    }


    public static MutableComponent createTranslatedBlackGrayWave(String translationKey, Object... args) {
        return AnimatedTextManager.createDynamicTranslatedBlackGrayWave(translationKey);
    }


    public static MutableComponent createTranslatedRainbowWave(String translationKey, Object... args) {
        return AnimatedTextManager.createDynamicTranslatedRainbowWave(translationKey);
    }


    public static MutableComponent createTranslatedGoldenWave(String translationKey, Object... args) {
        return AnimatedTextManager.createDynamicTranslatedGoldenWave(translationKey);
    }


    public static MutableComponent createTranslatedDarkRedBloodWave(String translationKey, Object... args) {
        return AnimatedTextManager.createDynamicTranslatedDarkRedBloodWave(translationKey);
    }


    public static MutableComponent createTranslatedCyanGreenWave(String translationKey, Object... args) {
        return AnimatedTextManager.createDynamicTranslatedCyanGreenWave(translationKey);
    }


    public static MutableComponent createRainbowWave(String text) {
        return AnimatedTextManager.createRainbowWave(text);
    }

    public static MutableComponent createBlackGrayWave(String text) {
        return AnimatedTextManager.createBlackGrayWave(text);
    }

    public static MutableComponent createGoldenWave(String text) {
        return AnimatedTextManager.createGoldenWave(text);
    }

    public static MutableComponent createDarkRedBloodWave(String text) {
        return AnimatedTextManager.createDarkRedBloodWave(text);
    }

    public static MutableComponent createCyanGreenWave(String text) {
        return AnimatedTextManager.createCyanGreenWave(text);
    }
}