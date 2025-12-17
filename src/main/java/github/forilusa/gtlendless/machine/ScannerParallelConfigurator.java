package github.forilusa.gtlendless.machine;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyConfigurator;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import net.minecraft.network.chat.Component;
import github.forilusa.gtlendless.util.LanguageHelper;

/**
 * 扫描器控制器 - 子UI
 */
public class ScannerParallelConfigurator implements IFancyConfigurator {
    private final ScannerControllerMachine scannerController;

    public ScannerParallelConfigurator(ScannerControllerMachine scannerController) {
        this.scannerController = scannerController;
    }

    @Override
    public Component getTitle() {
        return Component.literal(LanguageHelper.getLocalizedString("并行数设置", "Parallel Settings"));
    }

    @Override
    public com.lowdragmc.lowdraglib.gui.texture.IGuiTexture getIcon() {
        // 使用正确的TextTexture方法
        return new GuiTextureGroup(GuiTextures.SLOT, new TextTexture("P"));
    }

    @Override
    public WidgetGroup createConfigurator() {
        int totalHeight = 80;
        var group = new WidgetGroup(0, 0, 174, totalHeight);
        int y = 10;

        // 标题
        group.addWidget(new LabelWidget(10, y,
                LanguageHelper.getLocalizedString("并行数设置", "Parallel Settings"))
                .setTextColor(0x00FF00).setDropShadow(true));
        y += 20;

        // 当前并行数显示
        group.addWidget(new LabelWidget(10, y, () ->
                LanguageHelper.getLocalizedString("当前并行数: ", "Current Parallel: ") +
                        scannerController.getCurrentParallel())
                .setTextColor(0xFFFFFF).setDropShadow(true));
        y += 20;

        // 并行数输入框
        WidgetGroup inputGroup = new WidgetGroup(0, y, 174, 20);
        inputGroup.addWidget(new LabelWidget(10, 2,
                LanguageHelper.getLocalizedString("设置并行数: ", "Set Parallel: "))
                .setTextColor(0xFFFFFF).setDropShadow(true));

        TextFieldWidget parallelInput = new TextFieldWidget(80, 0, 60, 16,
                () -> String.valueOf(scannerController.getCurrentParallel()),
                value -> {
                    try {
                        int newParallel = Integer.parseInt(value);
                        int min = ScannerControllerMachine.getMinParallel();
                        int max = ScannerControllerMachine.getMaxParallel();

                        // 限制范围
                        if (newParallel < min) newParallel = min;
                        if (newParallel > max) newParallel = max;

                        scannerController.setCurrentParallel(newParallel);
                    } catch (NumberFormatException e) {
                        // 忽略无效输入
                    }
                });
        parallelInput.setNumbersOnly(
                ScannerControllerMachine.getMinParallel(),
                ScannerControllerMachine.getMaxParallel());
        inputGroup.addWidget(parallelInput);

        group.addWidget(inputGroup);

        return group;
    }
}