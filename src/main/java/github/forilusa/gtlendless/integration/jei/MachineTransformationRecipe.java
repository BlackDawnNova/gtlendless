package github.forilusa.gtlendless.integration.jei;

import lombok.Getter;
import net.minecraft.world.item.ItemStack;

@Getter
public class MachineTransformationRecipe {
    private final ItemStack input; // 伪机器
    private final ItemStack output; // 真机器

    public MachineTransformationRecipe(ItemStack input, ItemStack output) {
        this.input = input;
        this.output = output;
    }
}