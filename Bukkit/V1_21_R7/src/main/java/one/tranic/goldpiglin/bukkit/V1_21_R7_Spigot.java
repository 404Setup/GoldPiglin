package one.tranic.goldpiglin.bukkit;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.BehaviorUtil;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import org.bukkit.craftbukkit.v1_21_R7.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class V1_21_R7_Spigot extends BukkitBase {
    @Override
    public String getTargetSign() {
        return "Spigot 1.21.11";
    }

    @Override
    public boolean canSeeNative(Player player, Entity target) {
        return BehaviorUtil.b((EntityLiving) target, (EntityLiving) player);
    }

    @Override
    public boolean readItemStack(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy(itemStack);

        ArmorTrim trim = item.a().a(DataComponents.ad);
        if (trim == null || trim.a() == null) return false;
        return trim.a().a(TrimMaterials.f);
    }
}
