package github.forilusa.gtlendless.config;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.format.ConfigFormats;
import github.forilusa.gtlendless.GTLendless;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.List;

@Config(id = GTLendless.MOD_ID)
public class GTLendlessConfig {

    public static GTLendlessConfig INSTANCE;
    private static boolean isLoaded = false;
    private static dev.toma.configuration.config.ConfigHolder<GTLendlessConfig> configHolder;

    public static GTLendlessConfig get() {
        return INSTANCE;
    }

    public static void init() {
        try {
            System.out.println("Initializing GTLendlessConfig...");

            @SuppressWarnings("removal")
            var format = ConfigFormats.yaml();
            configHolder = Configuration.registerConfig(GTLendlessConfig.class, format);

            INSTANCE = configHolder.getConfigInstance();
            System.out.println("GTLendlessConfig initialized successfully");

            configHolder.save();

        } catch (Exception e) {
            System.err.println("Failed to initialize GTLendlessConfig: " + e.getMessage());
            e.printStackTrace();
            INSTANCE = new GTLendlessConfig();
        }
    }

    public static void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getModId().equals(GTLendless.MOD_ID)) {
            System.out.println("GTLendless config loading...");
            isLoaded = true;
        }
    }

    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getModId().equals(GTLendless.MOD_ID)) {
            System.out.println("GTLendless config reloading...");
            isLoaded = true;
        }
    }

    public static boolean isConfigValid() {
        return INSTANCE != null && isLoaded;
    }

    @Configurable
    @Configurable.Comment({"Settings for tools that make block drops go directly to player inventory"})
    public ToolListSettings toolListSettings = new ToolListSettings();

    @Configurable
    @Configurable.Comment({"Block behavior settings"})
    public BlockSettings blockSettings = new BlockSettings();

    public static class ToolListSettings {
        @Configurable
        @Configurable.Comment({"Enable tool list feature"})
        public boolean enableToolList = true;

        @Configurable
        @Configurable.Comment({"Use default tool (gtceu:echoite_vajra)"})
        public boolean useDefaultTool = true;

        @Configurable
        @Configurable.Comment({"Tool 1"})
        @Configurable.Gui.CharacterLimit(100)
        public String tool1 = "delete";

        @Configurable
        @Configurable.Comment({"Tool 2"})
        @Configurable.Gui.CharacterLimit(100)
        public String tool2 = "delete";

        @Configurable
        @Configurable.Comment({"Tool 3"})
        @Configurable.Gui.CharacterLimit(100)
        public String tool3 = "delete";

        @Configurable
        @Configurable.Comment({"Tool 4"})
        @Configurable.Gui.CharacterLimit(100)
        public String tool4 = "delete";

        @Configurable
        @Configurable.Comment({"Tool 5"})
        @Configurable.Gui.CharacterLimit(100)
        public String tool5 = "delete";

        @Configurable
        @Configurable.Comment({"Tool 6"})
        @Configurable.Gui.CharacterLimit(100)
        public String tool6 = "delete";

        @Configurable
        @Configurable.Comment({"Tool 7"})
        @Configurable.Gui.CharacterLimit(100)
        public String tool7 = "delete";

        @Configurable
        @Configurable.Comment({"Tool 8"})
        @Configurable.Gui.CharacterLimit(100)
        public String tool8 = "delete";
    }

    public static class BlockSettings {
        @Configurable
        @Configurable.Comment({"Enable direct inventory feature"})
        public boolean enableDirectInventory = false;

        @Configurable
        @Configurable.Comment({"Check inventory space before adding items"})
        public boolean checkInventorySpace = true;

        @Configurable
        @Configurable.Comment({"Enable for all blocks"})
        public boolean enableForAllBlocks = false;

        @Configurable
        @Configurable.Comment({"Block blacklist (use # for tags, e.g. #minecraft:logs)"})
        @Configurable.Gui.CharacterLimit(2048)
        public String additionalBlockBlacklist = "minecraft:lapis_ore";
    }

    public List<String> getDirectInventoryTools() {
        List<String> tools = new ArrayList<>();

        if (!toolListSettings.enableToolList) {
            return tools;
        }

        // 默认工具
        if (toolListSettings.useDefaultTool) {
            tools.add("gtceu:echoite_vajra");
        }

        // 自定义工具
        addToolIfValid(tools, toolListSettings.tool1);
        addToolIfValid(tools, toolListSettings.tool2);
        addToolIfValid(tools, toolListSettings.tool3);
        addToolIfValid(tools, toolListSettings.tool4);
        addToolIfValid(tools, toolListSettings.tool5);
        addToolIfValid(tools, toolListSettings.tool6);
        addToolIfValid(tools, toolListSettings.tool7);
        addToolIfValid(tools, toolListSettings.tool8);

        System.out.println("Loaded tools for direct inventory: " + tools);
        return tools;
    }

    private void addToolIfValid(List<String> tools, String tool) {
        if (tool != null && !tool.trim().isEmpty() && !tool.trim().equals("delete")) {
            String trimmed = tool.trim();
            if (trimmed.contains(":")) {
                tools.add(trimmed);
            }
        }
    }

    public List<String> getBlacklist() {
        return parseConfigList(blockSettings.additionalBlockBlacklist);
    }

    private List<String> parseConfigList(String configString) {
        List<String> list = new ArrayList<>();
        if (configString != null && !configString.trim().isEmpty()) {
            if (configString.startsWith("[Ljava.lang.String;")) {
                return list;
            }
            String[] array = configString.split(",");
            for (String item : array) {
                String trimmed = item.trim();
                if (!trimmed.isEmpty()) {
                    list.add(trimmed);
                }
            }
        }
        return list;
    }

    public static void fixCorruptedConfig() {
        if (INSTANCE != null && INSTANCE.blockSettings != null) {
            String value = INSTANCE.blockSettings.additionalBlockBlacklist;
            if (value != null && value.startsWith("[Ljava.lang.String;")) {
                INSTANCE.blockSettings.additionalBlockBlacklist = "minecraft:lapis_ore";
                System.out.println("Fixed corrupted config value");
                if (configHolder != null) {
                    configHolder.save();
                }
            }
        }
    }
}