package one.tranic.goldpiglin.common;

import one.tranic.goldpiglin.common.config.Config;
import one.tranic.goldpiglin.common.data.ExpiringHashMap;
import one.tranic.goldpiglin.common.data.Scheduler;
import one.tranic.goldpiglin.common.data.Util;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BaseTarget implements Listener {
    public final ExpiringHashMap<UUID, TargetEntry> targets = new ExpiringHashMap<>(Config.getHatred().getExpirationTime(), Config.getHatred().getExpirationScannerTime());

    @EventHandler
    public void onPiglinDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Piglin entity)
            targets.remove(entity.getUniqueId());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (targets.isEmpty()) return;
        Scheduler.singleExecute(() -> {
            List<Map.Entry<UUID, TargetEntry>> ls = targets.filter((it) -> it.getValue().targetId() == event.getEntity().getUniqueId());
            if (ls.isEmpty()) return;
            for (int i = 0; i < ls.size(); i++) {
                Map.Entry<UUID, TargetEntry> entry = ls.get(i);
                targets.remove(entry.getKey());
            }
        }); // Don't put it in the main thread // Dispatching to a queue instead of a new thread to avoid data contention
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Piglin entity && event.getDamager() instanceof Player player) {
            targets.set(entity.getUniqueId(), new TargetEntry(player.getUniqueId(), entity.getUniqueId()));
            if (Config.getHatred().isNear()) getEntityStats(player);
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

    private boolean hasGoldArmor(@NotNull ItemStack[] armors) {
        if (armors.length == 0) return false;
        boolean v = false;
        for (@Nullable ItemStack armor : armors) {
            if (armor == null) continue;
            if (isGoldArmor(armor.getType())) {
                v = false;
                break; // If it's golden armor, use vanilla behavior
            }
            if (readItemStack(armor)) v = true;
        }
        return v;
    }

    private boolean isGoldArmor(Material armor) {
        return armor == Material.GOLDEN_BOOTS ||
                armor == Material.GOLDEN_HELMET ||
                armor == Material.GOLDEN_CHESTPLATE ||
                armor == Material.GOLDEN_LEGGINGS;
    }


    public boolean readItemStack(ItemStack itemStack) {
        return false;
    }

    public boolean canSeeNative(Player player, Entity entity) {
        return player.canSee(entity);
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

        if (angle > Math.toRadians(viewAngle) || playerLocation.distance(entityLocation) > maxDistance) return false;

        RayTraceResult result = player.getWorld().rayTraceBlocks(playerLocation, directionToEntity, maxDistance);
        return result == null || result.getHitBlock() == null || !(result.getHitPosition().distance(playerLocation.toVector()) < entityLocation.toVector().distance(playerLocation.toVector()));
    }

    private void getEntityStats(Player player) {
        List<Entity> entities = player.getNearbyEntities(Config.getHatred().getNearX(), Config.getHatred().getNearY(), Config.getHatred().getNearZ());
        List<Entity> finallyEntities = Util.newArrayList();
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if (e instanceof Player || !(e instanceof Piglin)) continue;
            if (Config.getHatred().isCanSee()) {
                boolean v = Config.getHatred().isNativeCanSee() ? canSeeNative(player, e) : canSee(player, (LivingEntity) e);
                if (!v) continue;
            }
            finallyEntities.add(e);
        }
        for (int i = 0; i < finallyEntities.size(); i++) {
            Entity e = finallyEntities.get(i);
            targets.set(e.getUniqueId(), new TargetEntry(player.getUniqueId(), e.getUniqueId()));
        }
    }
}
