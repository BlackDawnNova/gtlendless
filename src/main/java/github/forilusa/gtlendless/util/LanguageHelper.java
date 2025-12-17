package github.forilusa.gtlendless.util;

import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;

public class LanguageHelper {


    public static boolean isChinese() {
        // 客户端检测
        if (Minecraft.getInstance() != null) {
            String language = Minecraft.getInstance().getLanguageManager().getSelected();
            return language.startsWith("zh_");
        }
        return true;
    }

    /**
     * 根据当前语言返回字符串
     * @param chinese 中文文本
     * @param english 英文文本
     * @return 根据当前语言返回对应的文本
     */
    public static String getLocalizedString(String chinese, String english) {
        return isChinese() ? chinese : english;
    }
}