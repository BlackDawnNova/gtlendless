package github.forilusa.gtlendless.block;

import github.forilusa.gtlendless.item.category.ItemCategory;
import github.forilusa.gtlendless.machine.ModMultiblockMachines;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SteamMachineBlock extends Block {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private final ItemCategory.CategoryType categoryType;

    public SteamMachineBlock(String frontTexture, String otherTexture, ItemCategory.CategoryType categoryType) {
        super(BlockBehaviour.Properties.of()
                .strength(3.0f, 6.0f)
                .requiresCorrectToolForDrops());

        this.categoryType = categoryType;

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (categoryType != null) {
            tooltip.add(ItemCategory.getCategoryComponent(categoryType));
            tooltip.add(Component.literal(""));
        }

        tooltip.add(Component.translatable("gtlendless.pseudo_machine.description"));
        tooltip.add(Component.literal(""));

        tooltip.add(Component.translatable("gtceu.universal.tooltip.uses_steam"));
        tooltip.add(Component.literal(""));

        tooltip.add(Component.translatable("gtlendless.machine.steam_final_furnace.parallel", ModMultiblockMachines.PARALLEL_AMOUNT));
        tooltip.add(Component.translatable("gtlendless.machine.time_reduction_factor", String.format("%.1f", ModMultiblockMachines.TIME_REDUCTION_FACTOR)));
        tooltip.add(Component.translatable("gtlendless.machine.energy_cost_factor", String.format("%.0f", ModMultiblockMachines.ENERGY_COST_FACTOR * 100)));
        tooltip.add(Component.literal(""));

        tooltip.add(Component.translatable("gtlendless.pseudo_machine.hint"));
        tooltip.add(Component.literal(""));


        tooltip.add(github.forilusa.gtlendless.util.AnimatedTextUtil.createTranslatedBlackGrayWave("gtlendless.by"));
    }
}