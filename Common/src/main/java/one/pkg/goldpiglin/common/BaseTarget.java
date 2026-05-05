package one.pkg.goldpiglin.common;

import one.pkg.goldpiglin.common.config.Config;
import one.pkg.goldpiglin.common.data.ExpiringHashMap;
import one.pkg.goldpiglin.common.data.Scheduler;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class BaseTarget implements Listener {
    private static final double VIEW_ANGLE = 45.0;
    private static final double MAX_DISTANCE = 50.0;
    public final ExpiringHashMap<UUID, TargetEntry> targets = new ExpiringHashMap<>(Config.getHatred().getExpirationTime(), Config.getHatred().getExpirationScannerTime());
    public final ExpiringHashMap<UUID, Boolean> playerCache = new ExpiringHashMap<>(120, 24);
    private final Logger logger = LoggerFactory.getLogger("GoldPiglinTarget");

    public abstract String getTargetSign();

    @EventHandler
    public void onPiglinDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof PiglinAbstract entity)
            targets.remove(entity.getUniqueId());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (targets.isEmpty()) return;
        Scheduler.execute(() -> {
            List<Map.Entry<UUID, TargetEntry>> ls = targets.filter((it) -> it.getValue().targetId() == event.getEntity().getUniqueId());
            if (ls.isEmpty()) return;
            for (Map.Entry<UUID, TargetEntry> entry : ls)
                targets.remove(entry.getKey());
        }); // Don't put it in the main thread // Dispatching to a queue instead of a new thread to avoid data contention
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || !event.getDamager().getWorld().isPiglinSafe()) return;
        if (event.getEntity() instanceof PiglinAbstract entity && event.getDamager() instanceof Player player) {
            if (player.getGameMode().equals(GameMode.CREATIVE)) return;
            targets.put(entity.getUniqueId(), new TargetEntry(player.getUniqueId(), entity.getUniqueId()));
            if (Config.getHatred().isNear()) getEntityStats(player);
        }
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        var player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE) ||
                !player.getWorld().isPiglinSafe() ||
                !Config.getHatred().isNear())
            return;

        Material block = event.getBlock().getType();
        if (isNetherOre(block)) getEntityStats(player);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        var player = event.getPlayer();
        if (!player.getWorld().isPiglinSafe() || !Config.getHatred().isNear()) return;
        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.CHEST) return;
        getEntityStats(player);
    }

    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        if (event.isCancelled()) {
            if (Config.isDebug()) logger.info("EntityTargetLivingEntityEvent: The event was cancelled");
            return;
        }
        if (event.getEntity() instanceof PiglinAbstract entity && event.getTarget() instanceof Player player) {
            var piglinTarget = this.targets.get(entity.getUniqueId());
            if (player.getGameMode().equals(GameMode.CREATIVE) ||
                    !player.getWorld().isPiglinSafe() ||
                    piglinTarget != null) {
                if (Config.isDebug()) logger.info("""
                                EntityTargetLivingEntityEvent:
                                GameMode: {},
                                PiglinSafe: {},
                                PiglinTarget: {},
                                Player: {}
                                """,
                        player.getGameMode(),
                        player.getWorld().isPiglinSafe(),
                        piglinTarget == null ? "Unknown" : piglinTarget.targetId(),
                        player.getName());
                return;
            }

            ItemStack[] armors = player.getInventory().getArmorContents();
            Boolean status = playerCache.get(player.getUniqueId());
            if (status == null) {
                status = hasGoldArmor(armors);
                playerCache.put(player.getUniqueId(), status);
            }
            if (Config.isDebug()) logger.info("EntityTargetLivingEntityEvent: GoldArmorStatus {}", status);
            if (status) event.setCancelled(true);
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
        if (armors.length == 0) {
            if (Config.isDebug()) logger.info("ItemStack: is empty");
            return false;
        }
        boolean v = false;
        int i = 0;
        for (@Nullable ItemStack armor : armors) {
            if (armor == null) continue;
            if (isGoldArmor(armor.getType())) {
                v = false;
                break; // If it's golden armor, use vanilla behavior
            }
            var iR = readItemStack(armor);
            if (Config.isDebug()) logger.info("ItemStack: {} {} {}", i, armor.getType(), iR);
            if (iR) v = true;

            i++;
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
        return Config.getHatred().isReversalCanSee() ? isEntityVisible(entity, player) : isEntityVisible(player, entity);
    }

    // Spigot's native canSee seems to be not very sensitive, you should probably turn off the canSee setting.
    // Or just enable canSee without enabling nativeCanSee
    private boolean isEntityVisible(LivingEntity player, LivingEntity entity) {
        Location playerLocation = player.getEyeLocation();
        Vector playerDirection = playerLocation.getDirection();
        Location entityLocation = entity.getLocation().add(0, entity.getHeight() / 2, 0);
        Vector directionToEntity = entityLocation.toVector().subtract(playerLocation.toVector()).normalize();

        double angle = playerDirection.angle(directionToEntity);

        if (angle > Math.toRadians(VIEW_ANGLE) || playerLocation.distance(entityLocation) > MAX_DISTANCE) return false;

        RayTraceResult result = player.getWorld().rayTraceBlocks(playerLocation, directionToEntity, MAX_DISTANCE);
        return result == null || result.getHitBlock() == null || !(result.getHitPosition().distance(playerLocation.toVector()) < entityLocation.toVector().distance(playerLocation.toVector()));
    }

    private void getEntityStats(Player player) {
        List<Entity> entities = player.getNearbyEntities(Config.getHatred().getNearX(), Config.getHatred().getNearY(), Config.getHatred().getNearZ());
        for (Entity e : entities) {
            if (e instanceof Player || !(e instanceof PiglinAbstract)) continue;
            if (Config.getHatred().isCanSee()) {
                boolean v = Config.getHatred().isNativeCanSee() ? canSeeNative(player, e) : canSee(player, (LivingEntity) e);
                if (!v) continue;
            }
            targets.put(e.getUniqueId(), new TargetEntry(player.getUniqueId(), e.getUniqueId()));
        }
    }
}
