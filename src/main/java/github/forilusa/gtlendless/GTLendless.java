package github.forilusa.gtlendless;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.mojang.logging.LogUtils;

import github.forilusa.gtlendless.block.custom.ModBlocks;
import github.forilusa.gtlendless.config.GTLendlessConfig;
import github.forilusa.gtlendless.item.custom.ModCreativeModeTab;
import github.forilusa.gtlendless.item.custom.ModItems;
import github.forilusa.gtlendless.machine.ModMultiblockMachines;
import github.forilusa.gtlendless.machine.registry.ParallelHatchRegistration;
import github.forilusa.gtlendless.machine.registry.ScannerControllerRegistration;
import github.forilusa.gtlendless.registration.GTLEndlessRegistrate;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.simple.SimpleChannel;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

//  GTLendless模组主类
@Mod(GTLendless.MOD_ID)
public class GTLendless {
    public static final String MOD_ID = "gtlendless";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static SimpleChannel NETWORK_CHANNEL;

    public GTLendless() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        GTLEndlessRegistrate.REGISTRATE.registerEventListeners(modEventBus);

        GTLendlessConfig.init();
        GTLendlessConfig.fixCorruptedConfig();
        modEventBus.addListener(GTLendlessConfig::onConfigLoad);
        modEventBus.addListener(GTLendlessConfig::onConfigReload);

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeModeTab.register(modEventBus);

        modEventBus.addGenericListener(MachineDefinition.class, this::onRegisterMachines);
        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onRegisterMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        ParallelHatchRegistration.init();
        ScannerControllerRegistration.init();
        ModMultiblockMachines.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            GTLNetwork.register();
            NETWORK_CHANNEL = GTLNetwork.CHANNEL;

            try {
                Files.createDirectories(Paths.get("config/gtlendless/structures"));
            } catch (IOException e) {}
        });
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static ResourceLocation crossModId(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }
}