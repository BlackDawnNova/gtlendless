package github.forilusa.gtlendless.item.custom;

import com.gregtechceu.gtceu.api.GTValues;
import github.forilusa.gtlendless.GTLendless;
import github.forilusa.gtlendless.machine.ModMultiblockMachines;
import github.forilusa.gtlendless.machine.registry.ParallelHatchRegistration;
import github.forilusa.gtlendless.machine.registry.ScannerControllerRegistration;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

// 创造标签
public class ModCreativeModeTab {
    public static final String ENDLESS_TAB_STRING = "creativetab.endless_tab";
    public static final String ENDLESS_BLOCK_TAB_STRING = "creativetab.endless_block_tab";
    public static final String ENDLESS_MACHINE_TAB_STRING = "creativetab.endless_machine_tab";

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GTLendless.MOD_ID);

    public static final RegistryObject<CreativeModeTab> ENDLESS_TAB = CREATIVE_MODE_TABS.register("endless_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.FIRST_WATER_BUCKET.get()))
                    .title(Component.translatable(ENDLESS_TAB_STRING))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.FIRST_WATER_BUCKET.get());
                        pOutput.accept(ModItems.FINAL_PROTON.get());
                        pOutput.accept(ModItems.GENESIS_FACTOR.get());
                        pOutput.accept(ModItems.MULTIBLOCK_SCANNER.get());
                    })
                    .build()
    );

    public static final RegistryObject<CreativeModeTab> ENDLESS_BLOCK_TAB = CREATIVE_MODE_TABS.register("endless_block_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.FIRST_LOG.get()))
                    .title(Component.translatable(ENDLESS_BLOCK_TAB_STRING))
                    .displayItems((pParameters, pOutput) -> {

                        // 第一块原木
                        pOutput.accept(ModItems.FIRST_LOG.get());

                        // 添加扫描器控制器
                        if (ScannerControllerRegistration.getScannerController() != null) {
                            pOutput.accept(ScannerControllerRegistration.getScannerController().getItem());
                        }

                        // 并行控制仓
                        if (ParallelHatchRegistration.getAllParallelHatches() != null) {
                            for (int tier = GTValues.IV; tier <= GTValues.MAX; tier++) {
                                var hatch = ParallelHatchRegistration.getParallelHatch(tier);
                                if (hatch != null) {
                                    pOutput.accept(hatch.getItem());
                                }
                            }
                        }
                    })
                    .build()
    );

    public static final RegistryObject<CreativeModeTab> ENDLESS_MACHINE_TAB = CREATIVE_MODE_TABS.register("endless_machine_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.FINAL_PROTON.get()))
                    .title(Component.translatable(ENDLESS_MACHINE_TAB_STRING))
                    .displayItems((pParameters, pOutput) -> {
                        // 伪.终焉蒸汽熔炉
                        pOutput.accept(ModItems.PSEUDO_STEAM_FINAL_FURNACE.get());


                        // 添加测试电力机器
                        /*if (ModMultiblockMachines.getTestElectricMachine() != null) {
                            pOutput.accept(ModMultiblockMachines.getTestElectricMachine().getItem());
                        }*/
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}