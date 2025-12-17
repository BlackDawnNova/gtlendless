package github.forilusa.gtlendless.item.custom;

import github.forilusa.gtlendless.item.animation.AnimationConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class AnimatedWaterBucket extends BaseAnimatedItem {
    public AnimatedWaterBucket(Properties properties, AnimationConfig animationConfig, boolean hasEnchantmentEffect) {
        super(properties, animationConfig, hasEnchantmentEffect);
    }

    // 无限水
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos clickedPos = context.getClickedPos();
        Direction clickFace = context.getClickedFace();
        BlockPos placePos = clickedPos.relative(clickFace);

        if (canPlaceWater(level, placePos)) {
            level.setBlockAndUpdate(placePos, Fluids.WATER.defaultFluidState().createLegacyBlock());
            level.playSound(player, placePos, SoundEvents.BUCKET_EMPTY,
                    SoundSource.BLOCKS, 1.0F, 1.0F);

            if (player != null && level.isClientSide()) {
                player.displayClientMessage(Component.translatable("message.gtlendless.water_placed"), true);
            }

            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.FAIL;
    }

    private boolean canPlaceWater(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.canBeReplaced(Fluids.WATER);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }
}