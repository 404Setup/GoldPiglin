package one.tranic.goldpiglin.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {
    private static Hatred hatred;
    private static boolean useConcurrentMap = false;

    public static Hatred getHatred() {
        return hatred;
    }

    public static synchronized void reload(JavaPlugin plugin) {
        FileConfiguration config = plugin.getConfig();

        config.addDefault("use-concurrent-map", false);
        config.addDefault("hatred.expiration-time", 25L);
        config.addDefault("hatred.expiration-scanner-time", 30L);
        config.options().copyDefaults(true);
        plugin.saveConfig();

        readConfig(config);
    }

    private static synchronized void readConfig(FileConfiguration config) {
        useConcurrentMap = config.getBoolean("use-concurrent-map");
        hatred = new Hatred();
        hatred.setExpirationTime(config.getLong("hatred.expiration-time"));
        hatred.setExpirationScannerTime(config.getLong("hatred.expiration-scanner-time"));
    }

    public static boolean isUseConcurrentMap() {
        return useConcurrentMap;
    }
}
