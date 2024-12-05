package one.tranic.goldpiglin.common.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Config {
    private static File configFile;
    private static YamlConfiguration configuration;

    private static Hatred hatred = new Hatred();
    private static boolean useConcurrentMap = false;
    private static boolean useNms = false;

    public static Hatred getHatred() {
        return hatred;
    }

    public static synchronized void reload(JavaPlugin plugin) {
        configFile = plugin.getDataFolder().toPath().getParent().resolve("GoldPiglin").resolve("config.yml").toFile();
        try {
            if (!configFile.exists()) {
                if (!configFile.getParentFile().exists()) {
                    configFile.getParentFile().mkdir();
                }
                configFile.createNewFile();
            }
            configuration = YamlConfiguration.loadConfiguration(configFile);
            save();
            read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void save() throws IOException {
        configuration.addDefault("use-nms", true);
        configuration.addDefault("use-concurrent-map", false);
        configuration.addDefault("hatred.expiration-time", 20L);
        configuration.addDefault("hatred.expiration-scanner-time", 40L);
        configuration.addDefault("hatred.near.enabled", false);
        configuration.addDefault("hatred.near.x", 6);
        configuration.addDefault("hatred.near.y", 6);
        configuration.addDefault("hatred.near.z", 6);
        configuration.addDefault("hatred.can-see.enabled", true);
        configuration.addDefault("hatred.can-see.native", false);
        configuration.addDefault("hatred.can-see.reversal", false);

        configuration.setComments("use-nms", List.of("NMS mode is only available in Paper"));
        configuration.setComments("hatred.near.enabled", List.of("Area-wide hatred, closer to vanilla behavior, but may take longer to calculate."));
        configuration.setComments("hatred.can-see.enabled", List.of("Whether only Piglin within the player's sight will trigger hatred"));
        configuration.setComments("hatred.can-see.native", List.of("Use Spigot's own canSee API instead of GoldPiglin's line of sight calculation"));
        configuration.setComments("hatred.can-see.reversal", List.of("Inverted line of sight calculations to calculate entity line of sight instead of player line of sight"));

        configuration.options().copyDefaults(true);
        configuration.save(configFile);
    }

    private static synchronized void read() {
        useNms = configuration.getBoolean("use-nms");
        useConcurrentMap = configuration.getBoolean("use-concurrent-map");
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

    public static boolean isUseConcurrentMap() {
        return useConcurrentMap;
    }

    public static boolean isUseNms() {
        return useNms;
    }
}
