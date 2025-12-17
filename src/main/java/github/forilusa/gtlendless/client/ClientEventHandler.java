package github.forilusa.gtlendless.client;

import github.forilusa.gtlendless.GTLendless;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

//  客户端事件处理器
@Mod.EventBusSubscriber(modid = GTLendless.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            AnimatedTextManager.onClientTick();
        }
    }

    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        Player player = event.getEntity();
        ItemStack stack = player.getMainHandItem();

        if (stack.getItem() instanceof github.forilusa.gtlendless.item.custom.MultiblockScannerItem &&
                net.minecraft.client.gui.screens.Screen.hasAltDown()) {

            if (GTLendless.NETWORK_CHANNEL != null) {
                GTLendless.NETWORK_CHANNEL.sendToServer(
                        new github.forilusa.gtlendless.network.ClearScannerSelectionsPacket()
                );
            }

            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseButton.Pre event) {
        if (event.getButton() == 0 && event.getAction() == 1) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.screen == null && minecraft.player != null) {
                ItemStack stack = minecraft.player.getMainHandItem();

                if (stack.getItem() instanceof github.forilusa.gtlendless.item.custom.MultiblockScannerItem &&
                        net.minecraft.client.gui.screens.Screen.hasAltDown()) {

                    if (GTLendless.NETWORK_CHANNEL != null) {
                        GTLendless.NETWORK_CHANNEL.sendToServer(
                                new github.forilusa.gtlendless.network.ClearScannerSelectionsPacket()
                        );
                    }

                    event.setCanceled(true);
                }
            }
        }
    }
}