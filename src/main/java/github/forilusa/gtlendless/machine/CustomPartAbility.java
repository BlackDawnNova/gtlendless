package github.forilusa.gtlendless.machine;

import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;

// 自定义Part
public class CustomPartAbility {

    public static final PartAbility SCANNER_CONTROLLER = new PartAbility("scanner_controller");

    public interface IScannerController {
        int getCurrentParallel();
        void setCurrentParallel(int parallel);
        default char getScanMarker() { return 'K'; }
        default void recordPlayerFacing(net.minecraft.core.Direction playerFacing) {}
        default net.minecraft.core.Direction getRecordedPlayerFacing() { return net.minecraft.core.Direction.NORTH; }
        default net.minecraft.core.Direction getBlockFrontFacing() { return getRecordedPlayerFacing().getOpposite(); }
        default boolean hasFacingRecorded() { return false; }
    }
}