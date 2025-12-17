package github.forilusa.gtlendless.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredPartMachine;
import com.gregtechceu.gtceu.api.gui.fancy.ConfiguratorPanel;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.UpdateListener;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.util.Mth;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import static github.forilusa.gtlendless.util.LanguageHelper.getLocalizedString;

// 扫描器控制器机器，用于多方块结构扫描器系统中记录玩家朝向和配置并行数
public class ScannerControllerMachine extends TieredPartMachine
        implements CustomPartAbility.IScannerController {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            ScannerControllerMachine.class,
            TieredPartMachine.MANAGED_FIELD_HOLDER
    );

    private static final int MIN_PARALLEL = 1;
    private static final int MAX_PARALLEL = 99999;
    private static final int FIXED_BASE_PARALLEL = 4;

    @Persisted @DescSynced @UpdateListener(methodName = "onParallelUpdated")
    private int currentParallel = FIXED_BASE_PARALLEL;

    @Persisted @DescSynced
    private Direction placedPlayerFacing = Direction.NORTH;
    @Persisted @DescSynced
    private boolean hasFacingRecorded = false;

    public ScannerControllerMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
        initializeFacingFromBlockState();
    }

    private void initializeFacingFromBlockState() {
        if (!hasFacingRecorded) {
            BlockState blockState = getHolder().self().getBlockState();
            if (blockState.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING)) {
                Direction facing = blockState.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING);
                this.placedPlayerFacing = facing;
                this.hasFacingRecorded = true;
            }
        }
    }

    @Override
    public void recordPlayerFacing(Direction playerFacing) {
        this.placedPlayerFacing = playerFacing.getOpposite();
        this.hasFacingRecorded = true;
        markDirty();
    }

    @Override
    public Direction getRecordedPlayerFacing() {
        return placedPlayerFacing.getOpposite();
    }

    @Override
    public Direction getBlockFrontFacing() {
        return placedPlayerFacing;
    }

    @Override
    public boolean hasFacingRecorded() {
        return hasFacingRecorded;
    }

    public static int getMinParallel() {
        return MIN_PARALLEL;
    }

    public static int getMaxParallel() {
        return MAX_PARALLEL;
    }

    @Override
    public int getCurrentParallel() {
        return this.currentParallel;
    }

    @Override
    public void setCurrentParallel(int parallel) {
        int newParallel = Mth.clamp(parallel, MIN_PARALLEL, MAX_PARALLEL);
        if (this.currentParallel != newParallel) {
            this.currentParallel = newParallel;
            notifyControllers();
        }
    }

    @Override
    public char getScanMarker() {
        return 'K';
    }

    private void notifyControllers() {
        for (var controller : getControllers()) {
            controller.onStructureFormed();
        }
    }

    private void onParallelUpdated(int newValue, int oldValue) {
        markDirty();
    }

    @Override
    public Widget createUIWidget() {
        int width = 176;
        int height = 106;

        WidgetGroup mainGroup = new WidgetGroup(0, 0, width, height);

        mainGroup.setBackground(GuiTextures.BACKGROUND_INVERSE);

        int borderSize = 4;
        int contentX = borderSize;
        int contentY = borderSize;
        int contentWidth = width - 2 * borderSize;
        int contentHeight = height - 2 * borderSize;

        WidgetGroup contentGroup = new WidgetGroup(contentX, contentY, contentWidth, contentHeight);

        contentGroup.setBackground(GuiTextures.DISPLAY);

        contentGroup.addWidget(new LabelWidget(8, 8,
                getLocalizedString("扫描器控制器", "Scanner Controller")));

        contentGroup.addWidget(new LabelWidget(8, 20,
                () -> getLocalizedString("当前并行数：", "Current Parallel: ") + currentParallel));

        contentGroup.addWidget(new LabelWidget(8, 40,
                getLocalizedString("功能区域（预留）", "Feature Area (Reserved)")));

        contentGroup.addWidget(new LabelWidget(12, 52,
                getLocalizedString("功能名（待实现）", "Feature Name (To be implemented)")));

        contentGroup.addWidget(new LabelWidget(12, 64,
                getLocalizedString("功能名（待实现）", "Feature Name (To be implemented)")));

        contentGroup.addWidget(new LabelWidget(12, 76,
                getLocalizedString("功能名（待实现）", "Feature Name (To be implemented)")));

        contentGroup.addWidget(new LabelWidget(12, 88,
                getLocalizedString("功能名（待实现）", "Feature Name (To be implemented)")));

        mainGroup.addWidget(contentGroup);

        return mainGroup;
    }

    @Override
    public void attachConfigurators(ConfiguratorPanel configuratorPanel) {
        super.attachConfigurators(configuratorPanel);
        configuratorPanel.attachConfigurators(new ScannerParallelConfigurator(this));
    }

    @Override
    @NotNull
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public boolean canShared() {
        return false;
    }
}