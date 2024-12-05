package one.tranic.goldpiglin;

import one.tranic.goldpiglin.bukkit.common.UpdateEvent;
import one.tranic.goldpiglin.common.VersionEnum;
import one.tranic.goldpiglin.common.VersionUtils;
import one.tranic.goldpiglin.command.GPiglinCommand;
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
        int version = VersionEnum.getVersion().versionNum;
        if (version == 0) {
            throw new UnsupportedVersionException("GoldPiglin cannot run on this version of the server!");
        }

        boolean is126 = version >= VersionEnum.V1_20_5.versionNum;

        Config.reload(this);

        if (!Config.isUseNms()) {
            try {
                Class.forName("de.tr7zw.nbtapi.NBT");
            } catch (ClassNotFoundException e) {
                throw new DependencyNotFoundException("useNms is not enabled, but dependency is not installed: NBTAPI!");
            }
        }

        if (is126) {
            if (VersionUtils.isPaper() && Config.isUseNms()) register(new one.tranic.goldpiglin.paper.v1_20_6.Target());
            else register(new one.tranic.goldpiglin.bukkit.v1_20_5.Target());
        } else {
            if (VersionUtils.isPaper() && Config.isUseNms()) register(new one.tranic.goldpiglin.paper.v1_20_1.Target());
            else register(new one.tranic.goldpiglin.bukkit.v1_20_1.Target());
        }

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

    private void register(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }
}
