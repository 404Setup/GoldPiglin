package one.tranic.goldpiglin.base;

import one.tranic.goldpiglin.config.Config;
import one.tranic.goldpiglin.data.ExpiringHashMap;
import one.tranic.goldpiglin.data.Scheduler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BaseTarget implements Listener {
    public final ExpiringHashMap<UUID, TargetEntry> targets = new ExpiringHashMap<>(Config.getHatred().getExpirationTime(), Config.getHatred().getExpirationScannerTime());

    @EventHandler
    public void onPiglinDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Piglin entity) targets.remove(entity.getUniqueId());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (targets.isEmpty()) return;
        Scheduler.singleExecute(() -> {
            List<Map.Entry<UUID, TargetEntry>> ls = targets.filter((it) -> it.getValue().targetId() == event.getEntity().getUniqueId());
            if (ls.isEmpty()) return;
            for (Map.Entry<UUID, TargetEntry> entry : ls) {
                targets.remove(entry.getKey());
            }
        }); // Don't put it in the main thread // Dispatching to a queue instead of a new thread to avoid data contention
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Piglin entity && event.getDamager() instanceof Player player) {
            Scheduler.singleExecute(() -> {
                if (Config.getHatred().isNear()) {
                    List<Entity> entitys = player.getNearbyEntities(Config.getHatred().getNearX(), Config.getHatred().getNearY(), Config.getHatred().getNearZ());
                    if (!entitys.isEmpty()) {
                        for (Entity e : entitys) {
                            if (e instanceof Player || !(e instanceof Piglin)) continue;
                            if (Config.getHatred().isCanSee()) {
                                boolean v = Config.getHatred().isNativeCanSee() ? player.canSee(e) : canPlayerSeeEntity(player, (LivingEntity) e);
                                if (!v) continue;
                            }
                            targets.set(e.getUniqueId(), new TargetEntry(player.getUniqueId(), e.getUniqueId()));
                        }
                        return;
                    }
                }
                targets.set(entity.getUniqueId(), new TargetEntry(player.getUniqueId(), entity.getUniqueId()));
            });
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

    private boolean canPlayerSeeEntity(Player player, LivingEntity entity) {
        Location playerLocation = player.getEyeLocation();
        Vector playerDirection = playerLocation.getDirection();
        double viewAngle = 45;
        double maxDistance = 50;

        Location entityLocation = entity.getLocation().add(0, entity.getHeight() / 2, 0);

        Vector directionToEntity = entityLocation.toVector().subtract(playerLocation.toVector()).normalize();
        double angle = playerDirection.angle(directionToEntity);

        if (angle > Math.toRadians(viewAngle)) {
            return false;
        }

        if (playerLocation.distance(entityLocation) > maxDistance) return false;

        RayTraceResult result = player.getWorld().rayTraceBlocks(playerLocation, directionToEntity, maxDistance);
        return result == null || result.getHitBlock() == null || !(result.getHitPosition().distance(playerLocation.toVector()) < entityLocation.toVector().distance(playerLocation.toVector()));
    }
}
