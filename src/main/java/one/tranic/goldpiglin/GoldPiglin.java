package one.tranic.goldpiglin;

import one.tranic.goldpiglin.base.Metrics;
import one.tranic.goldpiglin.data.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class GoldPiglin extends JavaPlugin {
    private Metrics metrics;

    @Override
    public void onEnable() {
        String bukkit = Bukkit.getBukkitVersion();
        boolean is126 = bukkit.contains("1.21") || bukkit.contains("1.20.5") || bukkit.contains("1.20.6");
        boolean is120 = bukkit.contains("1.20") && !is126;
        if (!is120 && !is126) {
            getLogger().warning("GoldPiglin Plugin Not Supported");
            this.getServer().getPluginManager().disablePlugin(this);
        }
        if (is120) {
            getServer().getPluginManager().registerEvents(new one.tranic.goldpiglin.v1_20_1.Target(), this);
        } else if (is126) {
            getServer().getPluginManager().registerEvents(new one.tranic.goldpiglin.v1_20_6.Target(), this);
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
