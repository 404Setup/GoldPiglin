package one.tranic.goldpiglin.paper.v1_20_6;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterials;
import one.tranic.goldpiglin.common.BaseTarget;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Target extends BaseTarget {
    @Override
    public boolean canSeeNative(Player player, Entity target) {
        return BehaviorUtils.canSee((LivingEntity) target, (LivingEntity) player);
    }

    @Override
    public boolean readItemStack(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack item = CraftItemStack.unwrap(itemStack);
        ArmorTrim trim = item.getComponents().get(DataComponents.TRIM);
        if (trim == null || trim.material() == null) return false;
        return trim.material().is(TrimMaterials.GOLD);
    }
}
