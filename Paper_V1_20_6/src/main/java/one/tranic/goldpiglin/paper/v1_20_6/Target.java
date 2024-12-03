package one.tranic.goldpiglin.paper.v1_20_6;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.item.Item;
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
        System.out.println(item.getPrototype().toString());
        return false;
    }
}
