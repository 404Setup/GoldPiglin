package one.tranic.goldpiglin.paper.v1_20_1;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import one.tranic.goldpiglin.common.BaseTarget;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
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
        CompoundTag nbt = item.getTag();
        if (nbt == null) return false;
        return nbt.getCompound("Trim").getString("material") == "minecraft:gold";
    }
}
