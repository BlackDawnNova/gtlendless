package github.forilusa.gtlendless.network;

import github.forilusa.gtlendless.item.custom.MultiblockScannerItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//  实现alt+左键清空标记数据包
public class ClearScannerSelectionsPacket {

    public ClearScannerSelectionsPacket() {}

    public ClearScannerSelectionsPacket(FriendlyByteBuf buf) {}

    public void encode(FriendlyByteBuf buf) {}

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();

                if (stack.getItem() instanceof MultiblockScannerItem) {
                    MultiblockScannerItem scanner = (MultiblockScannerItem) stack.getItem();
                    scanner.clearSelectionsFromPacket(stack, player);
                } else {
                    stack = player.getOffhandItem();
                    if (stack.getItem() instanceof MultiblockScannerItem) {
                        MultiblockScannerItem scanner = (MultiblockScannerItem) stack.getItem();
                        scanner.clearSelectionsFromPacket(stack, player);
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}