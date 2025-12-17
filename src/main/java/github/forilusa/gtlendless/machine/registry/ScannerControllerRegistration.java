package github.forilusa.gtlendless.machine.registry;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import github.forilusa.gtlendless.machine.CustomPartAbility;
import github.forilusa.gtlendless.machine.ScannerControllerMachine;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static github.forilusa.gtlendless.registration.GTLEndlessRegistrate.REGISTRATE;



// 扫描器控制器注册
public class ScannerControllerRegistration {
    public static MachineDefinition SCANNER_CONTROLLER;
    private static boolean hasRegistered = false;

    public static void init() {
        if (hasRegistered) return;

        SCANNER_CONTROLLER = registerScannerController();
        hasRegistered = true;
    }

    private static MachineDefinition registerScannerController() {
        return REGISTRATE
                .machine("scanner_controller",
                        holder -> new ScannerControllerMachine(holder, GTValues.LV))
                .langValue("扫描器控制器")
                .rotationState(RotationState.ALL)
                .abilities(CustomPartAbility.SCANNER_CONTROLLER)
                .tier(GTValues.LV)
                .compassNode("scanner_controller")
                .tooltips(
                        Component.empty(),
                        Component.translatable("gtlendless.machine.scanner_controller.features_title")
                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                        Component.literal("  §71. §f").append(
                                Component.translatable("gtlendless.machine.scanner_controller.feature1")),
                        Component.literal("  §72. §f").append(
                                Component.translatable("gtlendless.machine.scanner_controller.feature2")),
                        Component.literal("  §73. §f").append(
                                Component.translatable("gtlendless.machine.scanner_controller.feature3")),
                        Component.empty(),
                        Component.translatable("gtlendless.tooltip.hold_shift")
                )
                .tooltipBuilder((stack, tooltips) -> {
                    boolean isShiftDown = false;
                    try {
                        if (net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT) {
                            isShiftDown = Screen.hasShiftDown();
                        }
                    } catch (Exception e) {
                        // 服务器端忽略
                    }

                    if (isShiftDown) {
                        tooltips.add(Component.empty());
                        tooltips.add(Component.translatable("gtlendless.machine.scanner_controller.usage_title")
                                .withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD));
                        tooltips.add(Component.literal("  §71. §f").append(
                                Component.translatable("gtlendless.machine.scanner_controller.usage1")));
                        tooltips.add(Component.literal("  §72. §f").append(
                                Component.translatable("gtlendless.machine.scanner_controller.usage2")));
                        tooltips.add(Component.literal("  §73. §f").append(
                                Component.translatable("gtlendless.machine.scanner_controller.usage3")));
                        tooltips.add(Component.literal("  §74. §f").append(
                                Component.translatable("gtlendless.machine.scanner_controller.usage4")));
                        tooltips.add(Component.literal("  §75. §f").append(
                                Component.translatable("gtlendless.machine.scanner_controller.usage5")));

                    }
                })
                .workableTieredHullRenderer(new ResourceLocation("gtlendless", "block/machines/scanner_controller"))
                .register();
    }

    public static MachineDefinition getScannerController() {
        return SCANNER_CONTROLLER;
    }
}