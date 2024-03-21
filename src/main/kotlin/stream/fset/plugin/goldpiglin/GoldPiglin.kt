package stream.fset.plugin.goldpiglin

import org.bukkit.plugin.java.JavaPlugin

class GoldPiglin :JavaPlugin() {

    override fun onEnable() {
        server.pluginManager.registerEvents(EntityTargetLivingEntity(), this)
    }

    override fun onDisable() {
        Scheduler.shutdown()
    }
}
