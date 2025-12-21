package github.forilusa.gtlendless.integration.jei;

import net.minecraft.world.item.ItemStack;

/**
 * @param input  伪机器
 * @param output 真机器
 */
public record MachineTransformationRecipe(ItemStack input, ItemStack output) {
}