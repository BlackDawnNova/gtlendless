package github.forilusa.gtlendless.machine.registry;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import github.forilusa.gtlendless.machine.ParallelHatchMachine;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static github.forilusa.gtlendless.registration.GTLEndlessRegistrate.REGISTRATE;


// 注册ES并行控制仓
public class ParallelHatchRegistration {
    public static MachineDefinition[] PARALLEL_HATCHES = new MachineDefinition[GTValues.MAX + 1];
    private static boolean hasRegistered = false;

    public static void init() {
        if (hasRegistered) return;

        PARALLEL_HATCHES = registerTieredParallelHatches();
        hasRegistered = true;
    }

    private static MachineDefinition[] registerTieredParallelHatches() {
        int[] tiers = {IV, LuV, ZPM, UV, UHV, UEV, UIV, UXV, OpV, MAX};
        MachineDefinition[] definitions = new MachineDefinition[GTValues.MAX + 1];

        for (int tier : tiers) {
            String tierName = GTValues.VN[tier].toLowerCase();
            int maxParallel = calculateMaxParallel(tier);

            MutableComponent tierLabel = Component.translatable("gtlendless.parallel_hatch.tooltip.tier.label");
            Component coloredTierTooltip = tierLabel.append(GTValues.VN[tier]);

            MutableComponent parallelLabel = Component.translatable("gtlendless.parallel_hatch.tooltip.max_parallel.label");
            Component parallelTooltip = parallelLabel.append(String.valueOf(maxParallel));

            definitions[tier] = REGISTRATE
                    .machine("parallel_hatch_" + tierName, holder -> new ParallelHatchMachine(holder, tier))
                    .langValue("ES-并行控制仓 (" + GTValues.VN[tier] + ")")
                    .rotationState(RotationState.ALL)
                    .abilities(PartAbility.PARALLEL_HATCH)
                    .tier(tier)
                    .compassNode("parallel_hatch")
                    .tooltips(
                            Component.translatable("gtlendless.machine.parallel_hatch.tooltip"),
                            Component.translatable("gtlendless.parallel_hatch.tooltip.mode.description"),
                            coloredTierTooltip,
                            parallelTooltip,
                            Component.translatable("gtlendless.parallel_hatch.tooltip.right_click"),
                            Component.translatable("gtlendless.parallel_hatch.tooltip.note")
                    )
                    .workableTieredHullRenderer(new ResourceLocation("gtlendless", "block/machines/parallel_hatch_mk" + (tier - 4)))
                    .register();
        }
        return definitions;
    }

    public static int calculateMaxParallel(int tier) {
        int maxConfig = getMaxConfig(tier);
        if (maxConfig == 0) return 4;
        long multiplier = (long) Math.pow(4, maxConfig + 1);
        long result = 4L * multiplier;
        return (int) Math.min(result, Integer.MAX_VALUE);
    }

    private static int getMaxConfig(int tier) {
        if (tier >= MAX) return 10;
        if (tier >= OpV) return 9;
        if (tier >= UXV) return 8;
        if (tier >= UIV) return 7;
        if (tier >= UEV) return 6;
        if (tier >= UHV) return 5;
        if (tier >= UV) return 4;
        if (tier >= ZPM) return 3;
        if (tier >= LuV) return 2;
        if (tier >= IV) return 1;
        return 0;
    }

    public static MachineDefinition getParallelHatch(int tier) {
        return tier >= IV && tier <= MAX ? PARALLEL_HATCHES[tier] : null;
    }

    public static boolean hasParallelHatch(int tier) {
        return getParallelHatch(tier) != null;
    }

    public static MachineDefinition[] getAllParallelHatches() {
        return PARALLEL_HATCHES;
    }

    public static int getRegisteredCount() {
        int count = 0;
        for (MachineDefinition hatch : PARALLEL_HATCHES) {
            if (hatch != null) count++;
        }
        return count;
    }
}