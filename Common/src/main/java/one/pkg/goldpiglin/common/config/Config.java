package one.pkg.goldpiglin.common.config;

import one.pkg.goldpiglin.common.GoldPiglinLogger;
import one.pkg.tinyutils.minecraft.Platform;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Config {
    private static File configFile;
    private static YamlConfiguration configuration;

    private static Hatred hatred = new Hatred();
    private static String adapter;
    private static boolean debug = false;
    private static boolean updateMessage = true;

    public static Hatred getHatred() {
        return hatred;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static synchronized void reload(JavaPlugin plugin) {
        configFile = plugin.getDataFolder().toPath().getParent().resolve("GoldPiglin").resolve("config.yml").toFile();
        if (!configFile.exists()) {
            try {
                if (!configFile.getParentFile().exists()) {
                    configFile.getParentFile().mkdirs();
                }
                configFile.createNewFile();
            } catch (IOException e) {
                GoldPiglinLogger.logger.error("Could not create configuration file!", e);
            }
        }
        configuration = YamlConfiguration.loadConfiguration(configFile);
        save();
        read();
    }

    public static synchronized void save() {
        configuration.addDefault("adapter", Platform.get() == Platform.Paper ? "Paper" : "Spigot");
        configuration.addDefault("debug", false);
        configuration.addDefault("update-message", true);
        configuration.addDefault("hatred.expiration-time", 20L);
        configuration.addDefault("hatred.expiration-scanner-time", 40L);
        configuration.addDefault("hatred.near.enabled", false);
        configuration.addDefault("hatred.near.x", 6);
        configuration.addDefault("hatred.near.y", 6);
        configuration.addDefault("hatred.near.z", 6);
        configuration.addDefault("hatred.can-see.enabled", true);
        configuration.addDefault("hatred.can-see.native", false);
        configuration.addDefault("hatred.can-see.reversal", false);

        configuration.setComments("adapter",
                List.of("Select according to your needs.",
                        "Supported adapters: Spigot, Paper, NBTAPI, Rtag",
                        "The Spigot adapter is compatible with 1.20.1-26.2, if Minecraft releases",
                        "an update then you have to wait for the new GoldPiglin version",
                        " (Paper adapter does not need to wait for updates most of the time).")
        );
        configuration.setComments("hatred.near.enabled", List.of("Area-wide hatred, closer to vanilla behavior, but may take longer to calculate."));
        configuration.setComments("hatred.can-see.enabled", List.of("Whether only Piglin within the player's sight will trigger hatred"));
        configuration.setComments("hatred.can-see.native", List.of("Use Spigot's own canSee API instead of GoldPiglin's line of sight calculation"));
        configuration.setComments("hatred.can-see.reversal", List.of("Inverted line of sight calculations to calculate entity line of sight instead of player line of sight"));

        configuration.options().copyDefaults(true);
        try {
            configuration.save(configFile);
        } catch (IOException e) {
            GoldPiglinLogger.logger.error("Could not save configuration file!", e);
        }
    }

    private static synchronized void read() {
        adapter = configuration.getString("adapter");
        debug = configuration.getBoolean("debug");
        updateMessage = configuration.getBoolean("update-message");
        hatred = new Hatred();
        hatred.setExpirationTime(configuration.getLong("hatred.expiration-time"));
        hatred.setExpirationScannerTime(configuration.getLong("hatred.expiration-scanner-time"));
        hatred.setNear(configuration.getBoolean("hatred.near.enabled"));
        hatred.setNearX(configuration.getInt("hatred.near.x"));
        hatred.setNearY(configuration.getInt("hatred.near.y"));
        hatred.setNearZ(configuration.getInt("hatred.near.z"));
        hatred.setCanSee(configuration.getBoolean("hatred.can-see.enabled"));
        hatred.setNativeCanSee(configuration.getBoolean("hatred.can-see.native"));
        hatred.setReversalCanSee(configuration.getBoolean("hatred.can-see.reversal"));
    }

    public static String getAdapter() {
        return adapter;
    }

    public static boolean isUpdateMessage() {
        return updateMessage;
    }
}
