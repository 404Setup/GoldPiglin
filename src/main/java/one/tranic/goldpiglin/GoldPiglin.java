package one.tranic.goldpiglin;

import one.tranic.goldpiglin.common.command.ReloadCommand;
import one.tranic.goldpiglin.common.config.Config;
import one.tranic.goldpiglin.common.data.Scheduler;
import one.tranic.goldpiglin.common.metrics.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class GoldPiglin extends JavaPlugin {
    private Metrics metrics;

    @Override
    public void onEnable() {
        String bukkit = Bukkit.getBukkitVersion();
        boolean is126 = bukkit.contains("1.21") || bukkit.contains("1.20.5") || bukkit.contains("1.20.6");
        boolean is120 = bukkit.contains("1.20") && !is126;
        if (!is120 && !is126) {
            throw new IllegalStateException("GoldPiglin Plugin Not Supported");
        }

        Config.reload(this);
        if (is120) {
            getServer().getPluginManager().registerEvents(new one.tranic.goldpiglin.v1_20_1.Target(), this);
        } else {
            getServer().getPluginManager().registerEvents(new one.tranic.goldpiglin.v1_20_5.Target(), this);
        }

        try {
            Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            SimpleCommandMap commandMap = (SimpleCommandMap) commandMapField.get(Bukkit.getPluginManager());

            commandMap.register("greload", "goldpiglin", new ReloadCommand(this));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        metrics = new Metrics(this, 23906);
    }

    @Override
    public void onDisable() {
        if (metrics != null) {
            metrics.shutdown();
        }
        Scheduler.shutdown();
    }
}
