package one.tranic.goldpiglin.common.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Config {
    private static Hatred hatred = new Hatred();
    private static boolean useConcurrentMap = false;

    public static Hatred getHatred() {
        return hatred;
    }

    public static synchronized void reload(JavaPlugin plugin) {
        FileConfiguration config = plugin.getConfig();

        config.addDefault("use-concurrent-map", false);
        config.addDefault("hatred.expiration-time", 20L);
        config.addDefault("hatred.expiration-scanner-time", 40L);
        config.addDefault("hatred.near.enabled", false);
        config.addDefault("hatred.near.x", 6);
        config.addDefault("hatred.near.y", 6);
        config.addDefault("hatred.near.z", 6);
        config.addDefault("hatred.can-see.enabled", true);
        config.addDefault("hatred.can-see.native", false);
        config.addDefault("hatred.can-see.reversal", false);

        config.setComments("hatred.near.enabled", List.of("Area-wide hatred, closer to vanilla behavior, but may take longer to calculate."));
        config.setComments("hatred.can-see.enabled", List.of("Whether only Piglin within the player's sight will trigger hatred"));
        config.setComments("hatred.can-see.native", List.of("Use Spigot's own canSee API instead of GoldPiglin's line of sight calculation"));
        config.set("hatred.can-see.reversal", List.of("Inverted line of sight calculations to calculate entity line of sight instead of player line of sight"));

        config.options().copyDefaults(true);
        plugin.saveConfig();

        readConfig(config);
    }

    private static synchronized void readConfig(FileConfiguration config) {
        useConcurrentMap = config.getBoolean("use-concurrent-map");
        hatred = new Hatred();
        hatred.setExpirationTime(config.getLong("hatred.expiration-time"));
        hatred.setExpirationScannerTime(config.getLong("hatred.expiration-scanner-time"));
        hatred.setNear(config.getBoolean("hatred.near.enabled"));
        hatred.setNearX(config.getInt("hatred.near.x"));
        hatred.setNearY(config.getInt("hatred.near.y"));
        hatred.setNearZ(config.getInt("hatred.near.z"));
        hatred.setCanSee(config.getBoolean("hatred.can-see.enabled"));
        hatred.setNativeCanSee(config.getBoolean("hatred.can-see.native"));
        hatred.setReversalCanSee(config.getBoolean("hatred.can-see.reversal"));
    }

    public static boolean isUseConcurrentMap() {
        return useConcurrentMap;
    }
}
