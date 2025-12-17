package github.forilusa.gtlendless.item.animation;

import github.forilusa.gtlendless.item.category.ItemCategory;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class AnimationConfig {
    private final Function<String, Component> titleAnimation;
    private final Function<String, Component> authorAnimation;
    private final String descriptionKey;
    private final String titleKey;
    private final String[] additionalLines;
    private final ItemCategory.CategoryType categoryType;

    public AnimationConfig(Function<String, Component> titleAnimation,
                           Function<String, Component> authorAnimation,
                           String titleKey,
                           String descriptionKey,
                           String[] additionalLines,
                           ItemCategory.CategoryType categoryType) {
        this.titleAnimation = titleAnimation;
        this.authorAnimation = authorAnimation;
        this.titleKey = titleKey;
        this.descriptionKey = descriptionKey;
        this.additionalLines = additionalLines;
        this.categoryType = categoryType;
    }

    // Getters
    public Function<String, Component> getTitleAnimation() { return titleAnimation; }
    public Function<String, Component> getAuthorAnimation() { return authorAnimation; }
    public String getDescriptionKey() { return descriptionKey; }
    public String getTitleKey() { return titleKey; }
    public String[] getAdditionalLines() { return additionalLines; }
    public ItemCategory.CategoryType getCategoryType() { return categoryType; }

    // 预设动画
    public static final AnimationConfig WATER_BUCKET = new AnimationConfig(
            titleKey -> github.forilusa.gtlendless.util.AnimatedTextUtil.createTranslatedBlinkingTitle(titleKey),
            author -> github.forilusa.gtlendless.client.AnimatedTextManager.createDynamicTranslatedRainbowWave(author),
            "tooltip.gtlendless.first_water_bucket.title",
            "tooltip.gtlendless.first_water_bucket.description",
            new String[]{
                    "tooltip.gtlendless.first_water_bucket.chemical",
                    "tooltip.gtlendless.first_water_bucket.state",
                    "tooltip.gtlendless.first_water_bucket.temperature",
                    "tooltip.gtlendless.first_water_bucket.capacity"
            },
            null
    );

    public static final AnimationConfig LOG = new AnimationConfig(
            titleKey -> github.forilusa.gtlendless.util.AnimatedTextUtil.createTranslatedBlinkingTitle(titleKey),
            author -> github.forilusa.gtlendless.client.AnimatedTextManager.createDynamicTranslatedRainbowWave(author),
            "tooltip.gtlendless.first_log.title",
            "tooltip.gtlendless.first_log.description",
            new String[]{},
            null
    );

    public static final AnimationConfig PROTON = new AnimationConfig(
            titleKey -> github.forilusa.gtlendless.util.AnimatedTextUtil.createTranslatedDarkRedBloodWave(titleKey),
            author -> github.forilusa.gtlendless.client.AnimatedTextManager.createDynamicTranslatedBlackGrayWave(author),
            "tooltip.gtlendless.final_proton.title",
            "tooltip.gtlendless.final_proton.description",
            new String[]{},
            ItemCategory.CategoryType.FINAL
    );

    public static final AnimationConfig GENESIS = new AnimationConfig(
            titleKey -> github.forilusa.gtlendless.util.AnimatedTextUtil.createTranslatedCyanGreenWave(titleKey),
            author -> github.forilusa.gtlendless.client.AnimatedTextManager.createDynamicTranslatedGoldenWave(author),
            "tooltip.gtlendless.genesis_factor.title",
            "tooltip.gtlendless.genesis_factor.description",
            new String[]{},
            ItemCategory.CategoryType.GENESIS
    );

    public static final AnimationConfig MULTIBLOCK_SCANNER = new AnimationConfig(
            titleKey -> github.forilusa.gtlendless.util.AnimatedTextUtil.createTranslatedCyanGreenWave(titleKey),
            author -> github.forilusa.gtlendless.client.AnimatedTextManager.createDynamicTranslatedRainbowWave(author),
            "tooltip.gtlendless.multiblock_scanner.title",
            "tooltip.gtlendless.multiblock_scanner.description",
            new String[]{
                    "tooltip.gtlendless.multiblock_scanner.usage1",
                    "tooltip.gtlendless.multiblock_scanner.usage2",
                    "tooltip.gtlendless.multiblock_scanner.usage3",
                    "tooltip.gtlendless.multiblock_scanner.export_path",
                    "tooltip.gtlendless.multiblock_scanner.block_type_limit"
            },
            null
    );
}