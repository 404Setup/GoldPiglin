package one.tranic.goldpiglin;

import one.tranic.goldpiglin.bukkit.common.UpdateEvent;
import one.tranic.goldpiglin.command.GPiglinCommand;
import one.tranic.goldpiglin.common.Version;
import one.tranic.goldpiglin.common.VersionUtils;
import one.tranic.goldpiglin.common.config.Config;
import one.tranic.goldpiglin.common.data.FetchVersion;
import one.tranic.goldpiglin.common.data.Scheduler;
import one.tranic.goldpiglin.common.exception.DependencyNotFoundException;
import one.tranic.goldpiglin.common.exception.UnsupportedVersionException;
import one.tranic.goldpiglin.common.metrics.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class GoldPiglin extends JavaPlugin {
    private static FetchVersion fetchVersion;
    private Metrics metrics;

    public static FetchVersion getFetchVersion() {
        return fetchVersion;
    }

    @Override
    public void onEnable() {
        if (Version.getMinor() < 20)
            throw new UnsupportedVersionException("GoldPiglin cannot run on this version of the server!");

        Config.reload(this);

        if (!Config.isUseNms()) {
            try {
                Class.forName("de.tr7zw.nbtapi.NBT");
            } catch (ClassNotFoundException e) {
                throw new DependencyNotFoundException("useNms is not enabled, but dependency is not installed: NBTAPI!");
            }
        }

        registerTargetHandler();

        try {
            Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            SimpleCommandMap commandMap = (SimpleCommandMap) commandMapField.get(Bukkit.getPluginManager());

            commandMap.register("gpiglin", "goldpiglin", new GPiglinCommand(this));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        metrics = new Metrics(this, 23906);

        fetchVersion = new FetchVersion(getDescription().getVersion());
        if (fetchVersion.checkForUpdates()) {
            getServer().getConsoleSender().sendMessage(fetchVersion.getUpdateMessage());
        }
        fetchVersion.run();

        register(new UpdateEvent());
    }

    @Override
    public void onDisable() {
        if (metrics != null) {
            metrics.shutdown();
        }
        fetchVersion.stop();
        Scheduler.shutdown();
    }

    private void registerTargetHandler() {
        Listener target = createTargetForCurrentVersion();
        register(target);
    }

    private Listener createTargetForCurrentVersion() {
        boolean isPaperWithNms = VersionUtils.isPaper() && Config.isUseNms();
        boolean is1205 = Version.isMinimumVersion(20, 5);
        boolean is1213 = Version.isMinimumVersion(21, 3);

        if (is1213)
            return isPaperWithNms
                    ? new one.tranic.goldpiglin.paper.v1_21_3.Target()
                    : new one.tranic.goldpiglin.bukkit.v1_20_5.Target();

        if (is1205)
            return isPaperWithNms
                    ? new one.tranic.goldpiglin.paper.v1_20_6.Target()
                    : new one.tranic.goldpiglin.bukkit.v1_20_5.Target();

        return isPaperWithNms
                ? new one.tranic.goldpiglin.paper.v1_20_1.Target()
                : new one.tranic.goldpiglin.bukkit.v1_20_1.Target();
    }

    private void register(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }
}
