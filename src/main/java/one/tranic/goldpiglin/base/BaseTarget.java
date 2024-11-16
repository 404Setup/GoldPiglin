package one.tranic.goldpiglin.base;

import one.tranic.goldpiglin.data.ExpiringHashMap;
import one.tranic.goldpiglin.data.Scheduler;
import one.tranic.goldpiglin.data.TargetEntry;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BaseTarget implements Listener {
    public final ExpiringHashMap<UUID, TargetEntry> targets = new ExpiringHashMap<>(15, 30);

    @EventHandler
    public void onPiglinDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Piglin entity) targets.remove(entity.getUniqueId());
    }

    @EventHandler
    public void onPlayerDeath(EntityDeathEvent event) {
        if (targets.isEmpty()) return;
        Scheduler.execute(() -> {
            List<Map.Entry<UUID, TargetEntry>> ls = targets.filter((it) -> it.getValue().targetId() == event.getEntity().getUniqueId());
            if (ls.isEmpty()) return;
            for (Map.Entry<UUID, TargetEntry> entry : ls) {
                targets.remove(entry.getKey());
            }
        });
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Piglin entity && event.getDamager() instanceof Player player) {
            targets.set(entity.getUniqueId(), new TargetEntry(player.getUniqueId(), entity.getUniqueId()));
        }
    }

    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {}
}
