package github.forilusa.gtlendless.machine;

import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyConfigurator;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.misc.ItemStackTransfer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;
import github.forilusa.gtlendless.util.LanguageHelper;


// 并行控制仓-子UI设置
public class ParallelCircuitConfigurator implements IFancyConfigurator {
    private final ItemStackTransfer circuitSlot;
    private final int maxConfig;
    private final ParallelHatchMachine machine;

    public ParallelCircuitConfigurator(ItemStackTransfer circuitSlot, int maxConfig, ParallelHatchMachine machine) {
        this.circuitSlot = circuitSlot;
        this.maxConfig = maxConfig;
        this.machine = machine;
    }

    @Override
    public Component getTitle() {
        return Component.literal(LanguageHelper.getLocalizedString("并行配置", "Parallel Configuration"));
    }

    @Override
    public IGuiTexture getIcon() {
        ItemStack currentStack = this.circuitSlot.getStackInSlot(0);
        return new ItemStackTexture(IntCircuitBehaviour.isIntegratedCircuit(currentStack) ?
                currentStack : IntCircuitBehaviour.stack(0));
    }

    @Override
    public WidgetGroup createConfigurator() {
        int totalHeight = calculateTotalHeight(maxConfig);
        var group = new WidgetGroup(0, 0, 174, totalHeight);
        int y = 8;

        WidgetGroup titleRow = new WidgetGroup(0, y, 174, 18);
        titleRow.addWidget(new LabelWidget(9, 1,
                LanguageHelper.getLocalizedString("模式切换", "Mode Switch"))
                .setTextColor(0xCCCCCC).setDropShadow(true));

        ButtonWidget modeButton = new ButtonWidget(174 - 50 - 9, 0, 50, 16,
                new GuiTextureGroup(GuiTextures.BUTTON,
                        new TextTexture(() -> machine.isCircuitMode() ?
                                LanguageHelper.getLocalizedString("电路", "Circuit") :
                                LanguageHelper.getLocalizedString("精准", "Precise"))
                                .setColor(0xFFFFFF).setDropShadow(true)),
                clickData -> {
                    if (!clickData.isRemote) {
                        machine.setCircuitMode(!machine.isCircuitMode());
                    }
                });
        titleRow.addWidget(modeButton);
        group.addWidget(titleRow);

        y += 20;

        SlotWidget circuitSlotWidget = new SlotWidget(circuitSlot, 0, (174 - 18) / 2, y)
                .setCanTakeItems(false)
                .setBackgroundTexture(new GuiTextureGroup(GuiTextures.SLOT, GuiTextures.INT_CIRCUIT_OVERLAY));
        group.addWidget(circuitSlotWidget);

        y += 25;

        createCompactGroupedButtons(group, maxConfig, y);
        return group;
    }

    private int calculateTotalHeight(int maxConfig) {
        int baseHeight = 120;

        if (maxConfig > 0) {
            int buttonsPerRow = 5;
            int totalRows = (maxConfig + buttonsPerRow - 1) / buttonsPerRow;
            int maxRows = Math.min(totalRows, 3);
            baseHeight += maxRows * 20 + 15;
        }

        return Math.min(baseHeight, 180);
    }

    private void createCompactGroupedButtons(WidgetGroup group, int maxConfig, int startY) {
        int currentY = startY;

        group.addWidget(new LabelWidget(10, currentY,
                LanguageHelper.getLocalizedString("默认设置", "Default"))
                .setTextColor(0x66CCFF).setDropShadow(true));
        currentY += 15;
        createBasicModeButton(group, (174 - 18) / 2, currentY);
        currentY += 25;

        if (maxConfig > 0) {
            group.addWidget(new LabelWidget(10, currentY,
                    LanguageHelper.getLocalizedString("级别设置", "Level"))
                    .setTextColor(0xCC99FF).setDropShadow(true));
            currentY += 18;
            createCompactAdvancedButtons(group, maxConfig, currentY);
        }
    }

    private void createBasicModeButton(WidgetGroup group, int x, int y) {
        IGuiTexture buttonTexture = new GuiTextureGroup(
                GuiTextures.SLOT,
                new ItemStackTexture(IntCircuitBehaviour.stack(0)).scale(0.875f)
        );

        ButtonWidget button = new ButtonWidget(x, y, 18, 18,
                buttonTexture,
                clickData -> {
                    if (!clickData.isRemote) {
                        setCircuitValue(0);
                        machine.updateCurrentParallel();
                        machine.notifyControllers();
                    }
                });

        List<Component> tooltips = new ArrayList<>();
        tooltips.add(Component.literal(LanguageHelper.getLocalizedString("默认并行", "Default Parallel")));
        tooltips.add(Component.literal(LanguageHelper.getLocalizedString("使用基础并行数", "Use base parallel number")));
        tooltips.add(Component.literal(LanguageHelper.getLocalizedString("基础并行 = 4", "Base Parallel = 4")));
        button.setHoverTooltips(tooltips);

        group.addWidget(button);
    }

    private void createCompactAdvancedButtons(WidgetGroup group, int maxConfig, int startY) {
        int centerX = 174 / 2;
        int buttonsPerRow = 5;
        int maxRows = 3;
        int buttonsToShow = Math.min(maxConfig, buttonsPerRow * maxRows);

        for (int row = 0; row < maxRows; row++) {
            int buttonsInThisRow = Math.min(buttonsPerRow, buttonsToShow - row * buttonsPerRow);
            if (buttonsInThisRow <= 0) break;

            int rowWidth = buttonsInThisRow * 18;
            int startX = centerX - rowWidth / 2;

            for (int col = 0; col < buttonsInThisRow; col++) {
                int configValue = row * buttonsPerRow + col + 1;
                if (configValue > maxConfig) break;

                createAdvancedModeButton(group, configValue,
                        startX + col * 18, startY + 5 + row * 20);
            }
        }
    }

    private void createAdvancedModeButton(WidgetGroup group, int configValue, int x, int y) {
        IGuiTexture buttonTexture = new GuiTextureGroup(
                GuiTextures.SLOT,
                new ItemStackTexture(IntCircuitBehaviour.stack(configValue)).scale(0.875f)
        );

        ButtonWidget button = new ButtonWidget(x, y, 18, 18,
                buttonTexture,
                clickData -> {
                    if (!clickData.isRemote) {
                        setCircuitValue(configValue);
                        machine.updateCurrentParallel();
                        machine.notifyControllers();
                    }
                });

        button.setHoverTooltips(createAdvancedButtonTooltip(configValue));
        group.addWidget(button);
    }

    private void setCircuitValue(int configValue) {
        int safeValue = Math.max(0, Math.min(configValue, maxConfig));
        ItemStack newStack = safeValue == 0 ?
                ItemStack.EMPTY : IntCircuitBehaviour.stack(safeValue);
        circuitSlot.setStackInSlot(0, newStack);
        circuitSlot.onContentsChanged();
    }

    private List<Component> createAdvancedButtonTooltip(int configValue) {
        List<Component> tooltips = new ArrayList<>();
        long multiplier = (long) Math.pow(4, configValue + 1);
        long result = 4L * multiplier;

        tooltips.add(Component.literal(LanguageHelper.getLocalizedString("级别 ", "Level ") + configValue));
        tooltips.add(Component.literal(
                LanguageHelper.getLocalizedString("并行倍数: 4^(", "Parallel Multiplier: 4^(") +
                        configValue + "+1) = " + multiplier + "x"));
        tooltips.add(Component.literal(LanguageHelper.getLocalizedString("基础并行: 4", "Base Parallel: 4")));
        tooltips.add(Component.literal(
                LanguageHelper.getLocalizedString("预计并行: ", "Expected Parallel: ") +
                        (result > Integer.MAX_VALUE ?
                                LanguageHelper.getLocalizedString("> 21亿", "> 2.1B") :
                                result)));
        tooltips.add(Component.literal(
                LanguageHelper.getLocalizedString("公式: 基础并行 × ", "Formula: Base Parallel × ") + multiplier));

        return tooltips;
    }
}