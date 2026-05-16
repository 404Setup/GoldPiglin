package one.pkg.goldpiglin;

import one.pkg.goldpiglin.command.DiffCommand;
import one.pkg.goldpiglin.command.GPiglinCommand;
import one.pkg.goldpiglin.common.*;
import one.pkg.goldpiglin.common.config.Config;
import one.pkg.goldpiglin.common.data.FetchVersion;
import one.pkg.goldpiglin.common.data.Scheduler;
import one.pkg.goldpiglin.common.metrics.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class GoldPiglin extends JavaPlugin {
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
        if (Version.getMajor() < 2 && Version.getMinor() < 20)
            throw new UnsupportedOperationException("GoldPiglin cannot run on this version of the server!");

        instance = this;
        Config.reload(this);

        registerTargetHandler();

        SimpleCommandMap commandMap = getCommandMap();
        commandMap.register("gpiglin", "goldpiglin", new GPiglinCommand());
        if (false) commandMap.register("diff", "system", new DiffCommand()); // only debug

        metrics = new Metrics(this, 23906);

        fetchVersion = new FetchVersion(getDescription().getVersion());
        if (fetchVersion.checkForUpdates() && Config.isUpdateMessage())
            getServer().getConsoleSender().sendMessage(fetchVersion.getUpdateMessage());
        fetchVersion.run();

        register(new UpdateEvent());
    }

    @Override
    public void onDisable() {
        if (metrics != null) metrics.shutdown();
        if (fetchVersion != null) fetchVersion.stop();
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
            throw new UnsupportedOperationException("GoldPiglin could not find any available adapters on this server!");
        targetSign = target.getTargetSign();
        GoldPiglinLogger.logger.info("GoldPiglin is running on {}, Adapter: {}",
                Bukkit.getServer().getName(), targetSign);
        register(target);
    }

    private BaseTarget createTargetForCurrentVersion() {
        Adapter adapter = Adapter.getAdapter();
        if (Config.isDebug()) {
            GoldPiglinLogger.logger.info("Adapter: {}", adapter.getAdapterName());
            GoldPiglinLogger.logger.info("Server report Version: {}", Bukkit.getServer().getVersion());
            GoldPiglinLogger.logger.info("Server report Bukkit Version: {}", Bukkit.getServer().getBukkitVersion());
            GoldPiglinLogger.logger.info("Plugin report Version: {}.{}.{}", Version.getMajor(), Version.getMinor(), Version.getPatch());
        }
        if (!adapter.isPresent()) return null;

        return adapter.createTarget();
    }

    private void register(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }
}
