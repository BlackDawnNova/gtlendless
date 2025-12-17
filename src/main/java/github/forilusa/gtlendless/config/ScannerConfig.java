package github.forilusa.gtlendless.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@OnlyIn(Dist.CLIENT)
// 扫描器核心配置类
public class ScannerConfig {
    private static final String CONFIG_DIR = "config/gtlendless/";
    private static final String CONFIG_FILE = "scanner_settings.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int CONFIG_VERSION = 1;

    public static boolean globalMode = true;
    public static boolean compactOutput = false;
    public static boolean renderMode = true;

    private static class ConfigData {
        int version = CONFIG_VERSION;
        boolean globalMode;
        boolean compactOutput;
        boolean renderMode;
    }

    public static void loadConfig() {
        File configFile = new File(CONFIG_DIR + CONFIG_FILE);
        if (!configFile.exists()) {
            saveConfig();
            return;
        }

        try (FileReader reader = new FileReader(configFile)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);
            if (data != null) {
                globalMode = data.globalMode;
                compactOutput = data.compactOutput;
                renderMode = data.renderMode;
            }
        } catch (JsonSyntaxException e) {
            backupCorruptedConfig(configFile);
            saveConfig();
        } catch (IOException e) {
        }
    }

    public static void saveConfig() {
        try {
            Path configDir = Paths.get(CONFIG_DIR);
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }

            ConfigData data = new ConfigData();
            data.globalMode = globalMode;
            data.compactOutput = compactOutput;
            data.renderMode = renderMode;

            try (FileWriter writer = new FileWriter(CONFIG_DIR + CONFIG_FILE)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
        }
    }

    private static void backupCorruptedConfig(File badFile) {
        try {
            Path backupPath = Paths.get(badFile.getParent(), badFile.getName() + ".corrupted_backup");
            Files.move(badFile.toPath(), backupPath);
        } catch (IOException ex) {
        }
    }
}