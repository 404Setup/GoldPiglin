package one.tranic.goldpiglin.paper.v1_21_3;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import one.tranic.goldpiglin.common.BaseTarget;
import one.tranic.goldpiglin.paper.PaperBase;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class P1213_Target extends PaperBase {
    @Override
    public String getTargetSign() {
        return "Paper 1.21.3";
    }

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
