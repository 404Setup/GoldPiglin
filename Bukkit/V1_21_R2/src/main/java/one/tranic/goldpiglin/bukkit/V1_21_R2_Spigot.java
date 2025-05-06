package one.tranic.goldpiglin.bukkit;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.BehaviorUtil;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import one.tranic.goldpiglin.common.GoldPiglinLogger;
import one.tranic.goldpiglin.common.config.Config;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class V1_21_R2_Spigot extends BukkitBase {
    @Override
    public String getTargetSign() {
        return "Spigot 1.21.3";
    }

    @Override
    public boolean canSeeNative(Player player, Entity target) {
        return BehaviorUtil.b((EntityLiving) target, (EntityLiving) player);
    }

    @Override
    public boolean readItemStack(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy(itemStack);

        ArmorTrim trim = item.a().a(DataComponents.U);
        if (trim == null || trim.a() == null) {
            if (Config.isDebug())
                GoldPiglinLogger.logger.info("NBT tag is null for {}", itemStack.getType());
            return false;
        }
        var b = trim.a().a(TrimMaterials.f);
        if (Config.isDebug())
            GoldPiglinLogger.logger.info("NBT tag is {} for {}", b, itemStack.getType());
        return b;
    }
}
