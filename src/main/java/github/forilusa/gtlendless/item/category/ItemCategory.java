package github.forilusa.gtlendless.item.category;

import net.minecraft.network.chat.Component;


public class ItemCategory {

    // 获取分类
    public static Component getCategoryComponent(CategoryType type) {
        switch (type) {
            case FINAL:

                return github.forilusa.gtlendless.util.AnimatedTextUtil.createTranslatedDarkRedBloodWave("category.gtlendless.final");
            case GENESIS:

                return github.forilusa.gtlendless.util.AnimatedTextUtil.createTranslatedCyanGreenWave("category.gtlendless.genesis");
            default:
                return Component.empty();
        }
    }

    // 对应分类
    public enum CategoryType {
        FINAL,      // 终焉
        GENESIS     // 创生
    }
}