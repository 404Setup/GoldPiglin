package one.tranic.goldpiglin.base;

import one.tranic.goldpiglin.data.ExpiringHashMap;
import one.tranic.goldpiglin.data.Scheduler;
import org.bukkit.Material;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;

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
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        if (event.getEntity() instanceof Piglin entity && event.getTarget() instanceof Player player) {
            if (this.targets.get(entity.getUniqueId()) != null) return;
            ItemStack[] armors = player.getInventory().getArmorContents();
            if (hasGoldArmor(armors)) event.setCancelled(true);
        }
    }

    private boolean hasGoldArmor(ItemStack[] armors) {
        for (ItemStack armor : armors) {
            if (armor == null || isGoldArmor(armor)) continue; // If it's golden armor, use vanilla behavior
            if (readItemStack(armor)) return true;
        }
        return false;
    }

    private boolean isGoldArmor(ItemStack armor) {
        Material type = armor.getType();
        return type == Material.GOLDEN_BOOTS ||
                type == Material.GOLDEN_HELMET ||
                type == Material.GOLDEN_CHESTPLATE ||
                type == Material.GOLDEN_LEGGINGS;
    }

    public boolean readItemStack(ItemStack itemStack) {
        return false;
    }
}
