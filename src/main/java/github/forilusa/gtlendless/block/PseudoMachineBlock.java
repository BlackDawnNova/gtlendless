package github.forilusa.gtlendless.block;

import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import github.forilusa.gtlendless.item.category.ItemCategory;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public class PseudoMachineBlock extends SteamMachineBlock {

    public PseudoMachineBlock() {

        super("gtlendless:block/pseudo_steam_final_furnace/front",
                "gtlendless:block/pseudo_steam_final_furnace/side",
                ItemCategory.CategoryType.FINAL);
        System.out.println("GTLendless: PseudoMachineBlock instance created");
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide() && placer != null) {
            replaceWithRealMachine(level, pos, placer);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    private void replaceWithRealMachine(Level level, BlockPos pos, LivingEntity placer) {
        try {
            ResourceLocation realMachineId = new ResourceLocation("gtlendless", "steam_final_furnace");
            MachineDefinition realMachine = GTRegistries.MACHINES.get(realMachineId);

            if (realMachine != null && realMachine.getBlock() != null) {
                Direction facing = placer.getDirection().getOpposite();
                if (facing.getAxis().isVertical()) {
                    facing = Direction.NORTH;
                }

                level.removeBlock(pos, false);

                BlockState realMachineState = realMachine.getBlock().defaultBlockState();

                if (realMachineState.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING)) {
                    realMachineState = realMachineState.setValue(
                            net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING,
                            facing
                    );
                }

                level.setBlock(pos, realMachineState, 3);
                System.out.println("GTLendless: Successfully replaced pseudo machine with real machine at " + pos);
            } else {
                System.out.println("GTLendless: Failed to find real machine definition");
            }
        } catch (Exception e) {
            System.out.println("GTLendless: Error replacing with real machine: " + e.getMessage());
            e.printStackTrace();
        }
    }
}