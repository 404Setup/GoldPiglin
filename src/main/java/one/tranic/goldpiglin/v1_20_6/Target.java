package one.tranic.goldpiglin.v1_20_6;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import one.tranic.goldpiglin.base.BaseTarget;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class Target extends BaseTarget {
    @Override
    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        if (event.getEntity() instanceof Piglin entity && event.getTarget() instanceof Player player) {
            if (this.targets.get(entity.getUniqueId()) != null) return;
            ItemStack[] armors = player.getInventory().getArmorContents();
            if (armors.length == 0) return;
            for (ItemStack armor : armors) {
                if (armor == null) continue;
                ReadWriteNBT nbt = NBT.itemStackToNBT(armor);
                ReadableNBT comp = nbt.getCompound("components");
                if (comp == null) continue;
                ReadableNBT trim = comp.getCompound("minecraft:trim");
                if (trim != null && Objects.equals(trim.getString("material"), "minecraft:gold")) {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }
}
