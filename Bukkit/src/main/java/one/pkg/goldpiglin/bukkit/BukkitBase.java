package one.pkg.goldpiglin.bukkit;

import one.pkg.goldpiglin.common.BaseTarget;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public abstract class BukkitBase extends BaseTarget {
    @EventHandler
    public void onPlayerInventoryChange(InventoryClickEvent event) {
        if (event.isCancelled() ||
                !(event.getWhoClicked() instanceof Player player) ||
                player.getGameMode().equals(org.bukkit.GameMode.CREATIVE) ||
                player.getGameMode().equals(org.bukkit.GameMode.SPECTATOR)) return;

        if (event.getSlotType() == InventoryType.SlotType.ARMOR ||
                event.isShiftClick() && event.getCurrentItem() != null && isArmor(event.getCurrentItem().getType())) {
            playerCache.remove(event.getWhoClicked().getUniqueId());
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.isCancelled() ||
                !(event.getEntity() instanceof Player player) ||
                player.getGameMode().equals(org.bukkit.GameMode.CREATIVE) ||
                player.getGameMode().equals(org.bukkit.GameMode.SPECTATOR)) return;

        if (event.getItem().getItemStack() != null &&
                isArmor(event.getItem().getItemStack().getType())) {
            playerCache.remove(player.getUniqueId());
        }
    }

    private boolean isArmor(Material material) {
        return material.name().endsWith("_HELMET") ||
                material.name().endsWith("_CHESTPLATE") ||
                material.name().endsWith("_LEGGINGS") ||
                material.name().endsWith("_BOOTS");
    }

}
