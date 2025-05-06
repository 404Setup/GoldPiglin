package one.tranic.goldpiglin.bukkit;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.BehaviorUtil;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class V1_20_R2_Spigot extends BukkitBase {
    @Override
    public String getTargetSign() {
        return "Spigot 1.20.2";
    }

    @Override
    public boolean canSeeNative(Player player, Entity target) {
        return BehaviorUtil.b((EntityLiving) target, (EntityLiving) player);
    }

    @Override
    public boolean readItemStack(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound nbt = item.v();
        if (nbt == null) return false;
        return nbt.p("Trim").l("material") == "minecraft:gold";
    }
}
