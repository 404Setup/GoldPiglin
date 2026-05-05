package one.pkg.goldpiglin.bukkit;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.BehaviorUtil;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterials;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class V1_20_R4_Spigot extends BukkitBase {
    @Override
    public String getTargetSign() {
        return "Spigot 1.20.6";
    }

    @Override
    public boolean canSeeNative(Player player, Entity target) {
        return BehaviorUtil.b((EntityLiving) target, (EntityLiving) player);
    }

    @Override
    public boolean readItemStack(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy(itemStack);

        ArmorTrim trim = item.a().a(DataComponents.K);
        if (trim == null || trim.b() == null) return false;
        return trim.b().a(TrimMaterials.f);
    }
}
