package github.forilusa.gtlendless.machine;

import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.logic.OCParams;
import com.gregtechceu.gtceu.api.recipe.logic.OCResult;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.machine.multiblock.steam.SteamParallelMultiblockMachine;
import github.forilusa.gtlendless.registration.GTLEndlessRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import java.util.List;
import java.util.function.Supplier;

public class ModMultiblockMachines {

    public static MultiblockMachineDefinition REAL_STEAM_FINAL_FURNACE;
    private static boolean hasRegistered = false;

    private static final Supplier<Block> BRONZE_CASING = GTBlocks.CASING_BRONZE_BRICKS;
    private static final Supplier<Block> BRONZE_FIREBOX = GTBlocks.FIREBOX_BRONZE::get;
    private static final Supplier<Block> STEAM_INPUT_BUS = () -> GTMachines.STEAM_IMPORT_BUS.get().self();
    private static final Supplier<Block> STEAM_OUTPUT_BUS = () -> GTMachines.STEAM_EXPORT_BUS.get().self();

    // 并行
    public static final int PARALLEL_AMOUNT = 16;
    // 耗时
    public static final double TIME_REDUCTION_FACTOR = 0.6;
    // 耗能
    public static final double ENERGY_COST_FACTOR = 0.8;

    public static void init() {
        if (hasRegistered) return;

        try {
            REAL_STEAM_FINAL_FURNACE = GTLEndlessRegistrate.REGISTRATE
                    .multiblock("steam_final_furnace", SteamParallelMultiblockMachine::new)
                    .langValue("蒸汽终焉熔炉")
                    .rotationState(RotationState.NON_Y_AXIS)
                    .recipeType(GTRecipeTypes.FURNACE_RECIPES)
                    .appearanceBlock(BRONZE_CASING)

                    .pattern(definition -> {
                        TraceabilityPredicate steamPredicate = Predicates.abilities(PartAbility.STEAM)
                                .setMaxGlobalLimited(1)
                                .setPreviewCount(1);

                        TraceabilityPredicate inputBusPredicate = Predicates.blocks(STEAM_INPUT_BUS.get())
                                .setMaxGlobalLimited(1)
                                .setPreviewCount(1);

                        TraceabilityPredicate outputBusPredicate = Predicates.blocks(STEAM_OUTPUT_BUS.get())
                                .setMaxGlobalLimited(1)
                                .setPreviewCount(1);

                        TraceabilityPredicate fPredicate = Predicates.blocks(BRONZE_FIREBOX.get())
                                .or(steamPredicate);

                        return FactoryBlockPattern.start()
                                .aisle("BBB", "CCC", "CCC")
                                .aisle("BBB", "CAC", "CCC")
                                .aisle("BBB", "CKC", "CCC")
                                .where('K', Predicates.controller(Predicates.blocks(definition.getBlock())))
                                .where('B', fPredicate)
                                .where('C', Predicates.blocks(BRONZE_CASING.get())
                                        .or(inputBusPredicate)
                                        .or(outputBusPredicate))
                                .where('A', Predicates.air())
                                .build();
                    })
                    .recipeModifier(ModMultiblockMachines::customRecipeModifier, true)
                    .workableCasingRenderer(
                            new ResourceLocation("gtceu", "block/casings/solid/machine_casing_bronze_plated_bricks"),
                            new ResourceLocation("gtlendless", "block/multiblock/steam_final_furnace"),
                            false
                    )
                    .register();

            hasRegistered = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 在蒸汽机器注册完成后，注册测试电力机器

    }

    public static GTRecipe customRecipeModifier(MetaMachine machine, GTRecipe recipe, OCParams params, OCResult result) {
        var parallelResult = GTRecipeModifiers.accurateParallel(machine, recipe, PARALLEL_AMOUNT, false);
        GTRecipe modifiedRecipe = parallelResult.getFirst();

        if (modifiedRecipe != null) {
            long inputEUt = RecipeHelper.getInputEUt(modifiedRecipe);
            modifiedRecipe.tickInputs.put(
                    EURecipeCapability.CAP,
                    List.of(new Content(
                            (long) Math.max(1.0, inputEUt * ENERGY_COST_FACTOR),
                            100, 100, 0, null, null
                    ))
            );

            modifiedRecipe.duration = (int) Math.max(
                    1.0,
                    modifiedRecipe.duration * TIME_REDUCTION_FACTOR
            );
        }

        return modifiedRecipe;
    }

    public static MultiblockMachineDefinition getRealSteamFinalFurnace() {
        return REAL_STEAM_FINAL_FURNACE;
    }


}