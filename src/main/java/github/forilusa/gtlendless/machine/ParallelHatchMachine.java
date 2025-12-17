package github.forilusa.gtlendless.machine;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IParallelHatch;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredPartMachine;
import com.gregtechceu.gtceu.api.gui.fancy.ConfiguratorPanel;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.UpdateListener;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.misc.ItemStackTransfer;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import github.forilusa.gtlendless.util.LanguageHelper;

// 并行控制仓-主UI设置
public class ParallelHatchMachine extends TieredPartMachine implements IParallelHatch {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            ParallelHatchMachine.class,
            TieredPartMachine.MANAGED_FIELD_HOLDER
    );

    private final int maxConfig;
    private final int maxParallel;
    private static final int FIXED_BASE_PARALLEL = 4;

    @Persisted @Getter
    protected final ItemStackTransfer circuitInventory;

    @Persisted @DescSynced @UpdateListener(methodName = "onParallelUpdated")
    private int currentParallel = FIXED_BASE_PARALLEL;

    @Persisted @DescSynced
    private boolean circuitMode = true;

    @Persisted @DescSynced
    private int preciseModeParallel = FIXED_BASE_PARALLEL;

    public ParallelHatchMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
        this.maxConfig = calculateMaxConfig(tier);
        this.maxParallel = calculateMaxParallel(tier, maxConfig);
        this.circuitInventory = createCircuitInventory();
        updateCurrentParallel();
    }

    private ItemStackTransfer createCircuitInventory() {
        ItemStackTransfer transfer = new ItemStackTransfer(1) {
            @Override
            public void onContentsChanged() {
                super.onContentsChanged();
                if (circuitMode) {
                    updateCurrentParallel();
                    notifyControllers();
                }
            }

            @Override
            public int getSlotLimit(int slot) { return 1; }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return IntCircuitBehaviour.isIntegratedCircuit(stack);
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY; // 虚拟电路，禁止取出
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                return IntCircuitBehaviour.isIntegratedCircuit(stack) ?
                        super.insertItem(slot, stack, simulate) : stack;
            }
        };
        transfer.setFilter(IntCircuitBehaviour::isIntegratedCircuit);
        return transfer;
    }

    public void setCurrentParallel(int parallelAmount) {
        int newParallel = Mth.clamp(parallelAmount, 1, maxParallel);
        if (this.currentParallel != newParallel) {
            this.currentParallel = newParallel;
            if (!circuitMode) {
                this.preciseModeParallel = newParallel;
            }
            notifyControllers();
        }
    }

    public void setCircuitMode(boolean circuitMode) {
        if (this.circuitMode != circuitMode) {
            this.circuitMode = circuitMode;
            if (circuitMode) {
                updateCurrentParallel();
            } else {
                this.currentParallel = this.preciseModeParallel;
            }
            notifyControllers();
        }
    }

    public boolean isCircuitMode() {
        return circuitMode;
    }

    public void updateCurrentParallel() {
        if (!circuitMode) return;
        int newParallel = calculateParallel();
        if (currentParallel != newParallel) {
            currentParallel = newParallel;
        }
    }

    public void notifyControllers() {
        for (var controller : getControllers()) {
            // 通知控制器更新
            controller.onStructureFormed();
        }
    }

    private void onParallelUpdated(int newValue, int oldValue) {}

    private int calculateParallel() {
        int circuitConfig = getCircuitConfiguration();
        if (circuitConfig == 0) {
            return FIXED_BASE_PARALLEL;
        } else {
            long multiplier = (long) Math.pow(4, circuitConfig + 1);
            long result = (long) FIXED_BASE_PARALLEL * multiplier;
            return (int) Math.min(result, Math.min(maxParallel, Integer.MAX_VALUE));
        }
    }

    private int getCircuitConfiguration() {
        ItemStack circuitStack = circuitInventory.getStackInSlot(0);
        if (circuitStack.isEmpty()) return 0;

        int config = IntCircuitBehaviour.getCircuitConfiguration(circuitStack);
        int limitedConfig = Mth.clamp(config, 0, maxConfig);

        if (config != limitedConfig) {
            setCircuitConfiguration(limitedConfig);
        }
        return limitedConfig;
    }

    private void setCircuitConfiguration(int config) {
        int limitedConfig = Mth.clamp(config, 0, maxConfig);
        ItemStack circuitStack = limitedConfig == 0 ?
                ItemStack.EMPTY : IntCircuitBehaviour.stack(limitedConfig);
        circuitInventory.setStackInSlot(0, circuitStack);
    }

    private int calculateMaxConfig(int tier) {
        if (tier >= GTValues.MAX) return 10;
        if (tier >= GTValues.OpV) return 9;
        if (tier >= GTValues.UXV) return 8;
        if (tier >= GTValues.UIV) return 7;
        if (tier >= GTValues.UEV) return 6;
        if (tier >= GTValues.UHV) return 5;
        if (tier >= GTValues.UV) return 4;
        if (tier >= GTValues.ZPM) return 3;
        if (tier >= GTValues.LuV) return 2;
        if (tier >= GTValues.IV) return 1;
        return 0;
    }

    private int calculateMaxParallel(int tier, int maxConfigForTier) {
        if (maxConfigForTier == 0) return FIXED_BASE_PARALLEL;
        long multiplier = (long) Math.pow(4, maxConfigForTier + 1);
        long result = (long) FIXED_BASE_PARALLEL * multiplier;
        return (int) Math.min(result, Integer.MAX_VALUE);
    }

    @Override
    public int getCurrentParallel() {
        return this.currentParallel;
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup mainGroup = new WidgetGroup(0, 0, 176, 120);
        int y = 8;
        int lineHeight = 16;

        // 基本信息显示
        mainGroup.addWidget(new LabelWidget(8, y,
                LanguageHelper.getLocalizedString("并行控制仓", "Parallel Control Hatch"))
                .setTextColor(0xFFFFFF).setDropShadow(true));
        y += lineHeight;

        mainGroup.addWidget(new LabelWidget(8, y,
                LanguageHelper.getLocalizedString("等级: ", "Tier: ") + getTierName(getTier()))
                .setTextColor(0xFFFFFF).setDropShadow(true));
        y += lineHeight;

        mainGroup.addWidget(new LabelWidget(8, y, () ->
                LanguageHelper.getLocalizedString("当前模式: ", "Current Mode: ") +
                        (circuitMode ? LanguageHelper.getLocalizedString("电路模式", "Circuit Mode") :
                                LanguageHelper.getLocalizedString("精准模式", "Precise Mode")))
                .setTextColor(0x00FFFF).setDropShadow(true));
        y += lineHeight;

        mainGroup.addWidget(new LabelWidget(8, y, () ->
                LanguageHelper.getLocalizedString("当前并行: ", "Current Parallel: ") + currentParallel)
                .setTextColor(0x00FF00).setDropShadow(true));
        y += lineHeight;

        mainGroup.addWidget(new LabelWidget(8, y, () ->
                LanguageHelper.getLocalizedString("最大并行: ", "Max Parallel: ") + maxParallel)
                .setTextColor(0xFFFF00).setDropShadow(true));
        y += lineHeight;

        if (circuitMode) {
            // 电路模式
            WidgetGroup configRow = new WidgetGroup(0, y, 176, 18);
            configRow.addWidget(new LabelWidget(8, 1, () -> {
                int config = getCircuitConfiguration();
                return LanguageHelper.getLocalizedString("配置: ", "Cfg: ") +
                        (config == 0 ? LanguageHelper.getLocalizedString("默认", "Def") : String.valueOf(config));
            }).setTextColor(0xFFFFFF).setDropShadow(true));

            SlotWidget circuitSlot = new SlotWidget(circuitInventory, 0, 60, 0)
                    .setCanTakeItems(false)
                    .setBackgroundTexture(new GuiTextureGroup(GuiTextures.SLOT, GuiTextures.INT_CIRCUIT_OVERLAY));
            configRow.addWidget(circuitSlot);
            mainGroup.addWidget(configRow);
        } else {
            // 精准模式
            WidgetGroup parallelRow = new WidgetGroup(0, y, 176, 18);
            parallelRow.addWidget(new LabelWidget(8, 3,
                    LanguageHelper.getLocalizedString("设置:", "Set:"))
                    .setTextColor(0xFFFFFF).setDropShadow(true));

            TextFieldWidget parallelInput = new TextFieldWidget(40, 1, 50, 16,
                    () -> String.valueOf(preciseModeParallel),
                    value -> {
                        try {
                            setCurrentParallel(Integer.parseInt(value));
                        } catch (NumberFormatException e) {
                            // 忽略无效输入
                        }
                    });
            parallelInput.setNumbersOnly(1, maxParallel);
            parallelRow.addWidget(parallelInput);
            mainGroup.addWidget(parallelRow);
        }

        return mainGroup;
    }

    @Override
    public void attachConfigurators(ConfiguratorPanel configuratorPanel) {
        super.attachConfigurators(configuratorPanel);
        configuratorPanel.attachConfigurators(new ParallelCircuitConfigurator(circuitInventory, maxConfig, this));
    }

    private String getTierName(int tier) {
        return (tier >= 0 && tier < GTValues.VN.length) ? GTValues.VN[tier] : "Unknown";
    }

    @Override
    @NotNull
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public boolean canShared() {
        return true;
    }
}