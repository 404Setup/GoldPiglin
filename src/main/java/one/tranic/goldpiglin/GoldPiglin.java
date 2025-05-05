package one.tranic.goldpiglin;

import one.tranic.goldpiglin.bukkit.common.UpdateEvent;
import one.tranic.goldpiglin.command.GPiglinCommand;
import one.tranic.goldpiglin.common.Adapter;
import one.tranic.goldpiglin.common.BaseTarget;
import one.tranic.goldpiglin.common.Version;
import one.tranic.goldpiglin.common.config.Config;
import one.tranic.goldpiglin.common.data.FetchVersion;
import one.tranic.goldpiglin.common.data.Scheduler;
import one.tranic.goldpiglin.common.exception.UnsupportedVersionException;
import one.tranic.goldpiglin.common.metrics.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class GoldPiglin extends JavaPlugin {
    public static final org.slf4j.Logger logger = LoggerFactory.getLogger("GoldPiglin");
    private static FetchVersion fetchVersion;
    private static String targetSign;
    private static GoldPiglin instance;
    private Metrics metrics;

    public static FetchVersion getFetchVersion() {
        return fetchVersion;
    }

    public static GoldPiglin getPlugin() {
        return instance;
    }

    public static String getTargetSign() {
        return targetSign;
    }

    @Override
    public void onEnable() {
        if (Version.getMinor() < 20)
            throw new UnsupportedVersionException("GoldPiglin cannot run on this version of the server!");

        instance = this;
        Config.reload(this);

        registerTargetHandler();

        SimpleCommandMap commandMap = getCommandMap();
        commandMap.register("gpiglin", "goldpiglin", new GPiglinCommand(this));

        metrics = new Metrics(this, 23906);

        fetchVersion = new FetchVersion(getDescription().getVersion());
        if (fetchVersion.checkForUpdates() && Config.isUpdateMessage())
            getServer().getConsoleSender().sendMessage(fetchVersion.getUpdateMessage());
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

    private SimpleCommandMap getCommandMap() {
        try {
            Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            return (SimpleCommandMap) commandMapField.get(Bukkit.getPluginManager());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void registerTargetHandler() {
        BaseTarget target = createTargetForCurrentVersion();
        if (target == null)
            throw new UnsupportedVersionException("GoldPiglin could not find any available adapters on this server!");
        targetSign = target.getTargetSign();
        logger.info("GoldPiglin is running on {}, Adapter: {}",
                Bukkit.getServer().getName(), targetSign);
        register(target);
    }

    private BaseTarget createTargetForCurrentVersion() {
        Adapter adapter = Adapter.getAdapter();
        if (!adapter.isPresent()) return null;

        return adapter.createTarget();
    }

    private void register(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }
}
