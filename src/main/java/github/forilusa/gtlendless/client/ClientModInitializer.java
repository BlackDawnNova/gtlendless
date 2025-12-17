package github.forilusa.gtlendless.client;

import github.forilusa.gtlendless.config.ScannerConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = "gtlendless", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
@OnlyIn(Dist.CLIENT)
// 扫描器创建客户端初始化类
public class ClientModInitializer {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ScannerConfig.loadConfig();
    }
}