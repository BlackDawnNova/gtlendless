package github.forilusa.gtlendless;

import github.forilusa.gtlendless.network.ClearScannerSelectionsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

//  网络数据包注册类
public class GTLNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(GTLendless.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(
                packetId++,
                ClearScannerSelectionsPacket.class,
                ClearScannerSelectionsPacket::encode,
                ClearScannerSelectionsPacket::new,
                ClearScannerSelectionsPacket::handle
        );
    }
}