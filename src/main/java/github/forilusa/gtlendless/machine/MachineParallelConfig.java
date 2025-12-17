package github.forilusa.gtlendless.machine;

import com.gregtechceu.gtceu.api.GTValues;


// 并行控制仓基础并行数配置
public class MachineParallelConfig {
    public static class BaseParallel {
        public static final int DEFAULT = 4;
    }


    public static int getBaseParallel(String machineId) {
        // 添加特定机器的并行数
        // if (machineId.contains("distillation_tower")) return 6;
        return BaseParallel.DEFAULT;
    }

    public static int getMaxHatchSetting(int machineTier) {
        if (machineTier < GTValues.IV) return 0;

        if (machineTier >= GTValues.MAX) return 10;
        if (machineTier >= GTValues.OpV) return 9;
        if (machineTier >= GTValues.UXV) return 8;
        if (machineTier >= GTValues.UIV) return 7;
        if (machineTier >= GTValues.UEV) return 6;
        if (machineTier >= GTValues.UHV) return 5;
        if (machineTier >= GTValues.UV) return 4;
        if (machineTier >= GTValues.ZPM) return 3;
        if (machineTier >= GTValues.LuV) return 2;
        if (machineTier >= GTValues.IV) return 1;

        return 0;
    }

    public static boolean canUseParallelHatch(int machineTier) {
        return getMaxHatchSetting(machineTier) > 0;
    }

    public static String getTierName(int tier) {
        if (tier >= 0 && tier < GTValues.VN.length) {
            return GTValues.VN[tier];
        }
        return "Unknown";
    }

    public static String getHatchDescription(int tier) {
        int maxSetting = getMaxHatchSetting(tier);
        int baseParallel = BaseParallel.DEFAULT;
        int maxParallel = baseParallel * (int) Math.pow(4, maxSetting);

        return String.format("并行控制仓 (%s): 设置范围 1-%d, 最大并行数 %d",
                getTierName(tier), maxSetting, maxParallel);
    }
}