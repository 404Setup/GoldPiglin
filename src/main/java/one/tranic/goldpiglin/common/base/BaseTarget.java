package one.tranic.goldpiglin.common.base;

import one.tranic.goldpiglin.common.config.Config;
import one.tranic.goldpiglin.common.data.ExpiringHashMap;
import one.tranic.goldpiglin.common.data.Scheduler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
            if (Config.getHatred().isNear() && getEntityStats(player)) return;
            targets.set(entity.getUniqueId(), new TargetEntry(player.getUniqueId(), entity.getUniqueId()));
        }
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        if (!event.getPlayer().getWorld().isPiglinSafe() || !Config.getHatred().isNear()) return;
        Material block = event.getBlock().getType();
        if (isNetherOre(block)) getEntityStats(event.getPlayer());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getPlayer().getWorld().isPiglinSafe() || !Config.getHatred().isNear()) return;
        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.CHEST) return;
        getEntityStats(event.getPlayer());
    }

    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        if (event.getEntity() instanceof Piglin entity && event.getTarget() instanceof Player player) {
            if (this.targets.get(entity.getUniqueId()) != null) return;
            ItemStack[] armors = player.getInventory().getArmorContents();
            if (hasGoldArmor(armors)) event.setCancelled(true);
        }
    }

    private boolean isNetherOre(Material block) {
        return block == Material.NETHER_GOLD_ORE ||
                block == Material.GILDED_BLACKSTONE ||
                block == Material.NETHER_QUARTZ_ORE ||
                block == Material.ANCIENT_DEBRIS ||
                block == Material.GOLD_BLOCK;
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

    private boolean canSee(Player player, LivingEntity entity) {
        return Config.getHatred().isReversalCanSee() ? canSeeEntity(entity, player) : canSeeEntity(player, entity);
    }

    // Spigot's native canSee seems to be not very sensitive, you should probably turn off the canSee setting.
    // Or just enable canSee without enabling nativeCanSee
    private boolean canSeeEntity(LivingEntity player, LivingEntity entity) {
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

    private boolean getEntityStats(Player player) {
        List<Entity> entitys = player.getNearbyEntities(Config.getHatred().getNearX(), Config.getHatred().getNearY(), Config.getHatred().getNearZ());
        for (Entity e : entitys) {
            if (e instanceof Player || !(e instanceof Piglin)) continue;
            if (Config.getHatred().isCanSee()) {
                boolean v = Config.getHatred().isNativeCanSee() ? player.canSee(e) : canSee(player, (LivingEntity) e);
                if (!v) continue;
            }
            targets.set(e.getUniqueId(), new TargetEntry(player.getUniqueId(), e.getUniqueId()));
            return true;
        }
        return false;
    }
}
