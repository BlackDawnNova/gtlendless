package github.forilusa.gtlendless.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mixin(Block.class)
public class BlockGetDropsMixin {

    @Inject(
            method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void gtlendless$onGetDrops(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            @Nullable BlockEntity blockEntity,
            @Nullable Entity entity,
            ItemStack tool,
            CallbackInfoReturnable<List<ItemStack>> cir
    ) {
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (blockId == null || !blockId.getNamespace().equals("gtlendless")) {
            return;
        }

        String path = blockId.getPath();


        if (path.startsWith("pseudo_")) {
            return;
        }


        ItemStack pseudoItem = getPseudoItemForMachine(path);
        if (!pseudoItem.isEmpty()) {
            List<ItemStack> result = new ArrayList<>();
            result.add(pseudoItem);
            cir.setReturnValue(result);
        }
    }

    private static ItemStack getPseudoItemForMachine(String machinePath) {
        String pseudoPath = "pseudo_" + machinePath;
        ResourceLocation pseudoItemId = new ResourceLocation("gtlendless", pseudoPath);

        var pseudoItem = ForgeRegistries.ITEMS.getValue(pseudoItemId);
        if (pseudoItem != null) {
            return new ItemStack(pseudoItem);
        }

        return ItemStack.EMPTY;
    }
}