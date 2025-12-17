package github.forilusa.gtlendless.registration;

import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import github.forilusa.gtlendless.GTLendless;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

public class GTLEndlessRegistrate extends GTRegistrate {
    public static final GTLEndlessRegistrate REGISTRATE = new GTLEndlessRegistrate();

    private GTLEndlessRegistrate() {
        super(GTLendless.MOD_ID);
    }

    static {
        REGISTRATE.defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
    }
}