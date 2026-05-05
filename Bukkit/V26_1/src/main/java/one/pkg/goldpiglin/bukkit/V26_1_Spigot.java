package one.pkg.goldpiglin.bukkit;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class V26_1_Spigot extends BukkitBase {
    @Override
    public String getTargetSign() {
        return "Spigot 26.1";
    }

    @Override
    public boolean canSeeNative(Player player, Entity target) {
        return BehaviorUtils.canSee((LivingEntity) target, (LivingEntity) player);
    }

    @Override
    public boolean readItemStack(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy(itemStack);

        ArmorTrim trim = item.getComponents().get(DataComponents.TRIM);
        if (trim == null || trim.material() == null) return false;
        return trim.material().is(TrimMaterials.GOLD);
    }
}
