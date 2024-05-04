package stream.fset.plugin.goldpiglin

import de.tr7zw.nbtapi.NBT
import org.bukkit.entity.Piglin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import java.util.*

data class ETLE(val player: UUID, val piglin: UUID)

class EntityTargetLivingEntity : Listener {
    private val attackMmap = ExpiringHashMap<UUID, ETLE>(15, 30)

    @EventHandler
    fun onPiglinDeath(e: EntityDeathEvent) {
        (e.entity as? Piglin)?.let { pig -> attackMmap.remove(pig.uniqueId) }
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        Scheduler.sendTask {
            attackMmap.filter { it.value.player == e.entity.uniqueId }
                .forEach { attackMmap.remove(it.key) }
        }
    }

    @EventHandler
    fun onPlayerAttack(e: EntityDamageByEntityEvent) {
        if (e.entity is Piglin && e.damager is Player) {
            val (pig, p) = e.entity as Piglin to e.damager as Player
            attackMmap[pig.uniqueId] = ETLE(p.uniqueId, pig.uniqueId)
        }
    }

    @EventHandler
    fun onEntityTargetLivingEntityEvent(e: EntityTargetLivingEntityEvent) {
        if (e.entity is Piglin && e.target is Player) {
            val (pig, p) = e.entity as Piglin to e.target as Player
            if (attackMmap[pig.uniqueId] != null) return
            val allGold = p.inventory.armorContents.all { itemStack ->
                itemStack?.let {
                    var isGold = false
                    NBT.get(it) { i ->
                        isGold = i?.getCompound("Trim")?.getString("material") == "minecraft:gold"
                    }
                    isGold
                } ?: false
            }

            if (allGold) {
                e.isCancelled = true
            }
        }
    }
}