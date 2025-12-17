package github.forilusa.gtlendless.item.custom;

import github.forilusa.gtlendless.item.animation.AnimationConfig;
import github.forilusa.gtlendless.item.category.ItemCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BaseAnimatedItem extends Item {
    protected final AnimationConfig animationConfig;
    protected final boolean hasEnchantmentEffect;

    public BaseAnimatedItem(Properties properties, AnimationConfig animationConfig, boolean hasEnchantmentEffect) {
        super(properties);
        this.animationConfig = animationConfig;
        this.hasEnchantmentEffect = hasEnchantmentEffect;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        try {
            super.appendHoverText(stack, level, tooltip, flag);
        } catch (Exception e) {
            // 忽略异常
        }
        addAnimatedTooltip(tooltip);
    }

    protected void addAnimatedTooltip(List<Component> tooltip) {
        // 添加分类标签（如果有）
        if (animationConfig.getCategoryType() != null) {
            tooltip.add(ItemCategory.getCategoryComponent(animationConfig.getCategoryType()));
        } else {
            // 添加标题动画
            tooltip.add(animationConfig.getTitleAnimation().apply(animationConfig.getTitleKey()));
        }

        tooltip.add(Component.literal(""));

        // 添加描述
        tooltip.add(Component.translatable(animationConfig.getDescriptionKey()));
        tooltip.add(Component.literal(""));

        // 添加额外行
        for (String lineKey : animationConfig.getAdditionalLines()) {
            tooltip.add(Component.translatable(lineKey));
        }

        if (animationConfig.getAdditionalLines().length > 0) {
            tooltip.add(Component.literal(""));
        }

        // 添加作者信息 - 使用彩虹渐变效果
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