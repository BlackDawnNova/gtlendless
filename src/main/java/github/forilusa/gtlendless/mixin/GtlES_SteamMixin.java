package github.forilusa.gtlendless.mixin;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.steam.SteamParallelMultiblockMachine;
import github.forilusa.gtlendless.machine.ModMultiblockMachines;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = SteamParallelMultiblockMachine.class, priority = 2000)
public class GtlES_SteamMixin extends WorkableMultiblockMachine {

    public GtlES_SteamMixin(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Inject(method = "addDisplayText", at = @At("TAIL"), remap = false)
    private void overrideParallelText(List<Component> textList, CallbackInfo ci) {
        if (this.getDefinition() != ModMultiblockMachines.REAL_STEAM_FINAL_FURNACE) {
            return;
        }

        for (int i = 0; i < textList.size(); i++) {
            String text = textList.get(i).getString();

            if (text.contains("同时处理至多") && text.contains("个配方")) {
                String newText = "同时处理至多 " + ModMultiblockMachines.PARALLEL_AMOUNT + " 个配方";
                textList.set(i, Component.literal(newText));
                break;
            }

            if (text.contains("Performing up to") && text.contains("Recipes in Parallel")) {
                String newText = "Performing up to " + ModMultiblockMachines.PARALLEL_AMOUNT + " Recipes in Parallel";
                textList.set(i, Component.literal(newText));
                break;
            }

            if (text.contains("同時処理") || text.contains("レシピ") || text.contains("並列")) {

                String newText = "最大 " + ModMultiblockMachines.PARALLEL_AMOUNT + " レシピを同時処理";
                textList.set(i, Component.literal(newText));
                break;
            }
        }
    }
}