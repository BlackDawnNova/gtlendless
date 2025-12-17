package github.forilusa.gtlendless.item.custom;

import github.forilusa.gtlendless.item.animation.AnimationConfig;
import github.forilusa.gtlendless.item.category.ItemCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BaseAnimatedBlockItem extends BlockItem {
    protected final AnimationConfig animationConfig;
    protected final boolean hasEnchantmentEffect;

    public BaseAnimatedBlockItem(Block block, Properties properties, AnimationConfig animationConfig, boolean hasEnchantmentEffect) {
        super(block, properties);
        this.animationConfig = animationConfig;
        this.hasEnchantmentEffect = hasEnchantmentEffect;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        try {
            super.appendHoverText(stack, level, tooltip, flag);
        } catch (Exception e) {
            // 静默处理异常
        }
        addAnimatedTooltip(tooltip);
    }

    protected void addAnimatedTooltip(List<Component> tooltip) {

        if (animationConfig.getCategoryType() != null) {
            tooltip.add(ItemCategory.getCategoryComponent(animationConfig.getCategoryType()));
        } else {
            tooltip.add(animationConfig.getTitleAnimation().apply(animationConfig.getTitleKey()));
        }

        tooltip.add(Component.literal(""));


        tooltip.add(Component.translatable(animationConfig.getDescriptionKey()));
        tooltip.add(Component.literal(""));


        for (String lineKey : animationConfig.getAdditionalLines()) {
            tooltip.add(Component.translatable(lineKey));
        }

        if (animationConfig.getAdditionalLines().length > 0) {
            tooltip.add(Component.literal(""));
        }


        tooltip.add(animationConfig.getAuthorAnimation().apply("gtlendless.by"));
    }

    @Override
    public boolean canBeDepleted() {
        return false;
    }

    @Override
    public void onDestroyed(ItemEntity entity) {
        if (!entity.level().isClientSide) {
            entity.setUnlimitedLifetime();
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasEnchantmentEffect;
    }
}