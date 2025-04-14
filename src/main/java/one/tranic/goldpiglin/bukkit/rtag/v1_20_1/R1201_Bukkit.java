package one.tranic.goldpiglin.bukkit.rtag.v1_20_1;

import com.saicone.rtag.RtagItem;
import one.tranic.goldpiglin.bukkit.BukkitBase;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class R1201_Bukkit extends BukkitBase {
    @Override
    public String getTargetSign() {
        return "RTag (Bukkit) 1.20.1";
    }

    @Override
    public boolean readItemStack(ItemStack armor) {
        var nbt = new RtagItem(armor).getOptional("Trim", "material");
        return nbt.isNotEmpty() && Objects.equals(nbt.asString(), "minecraft:gold");
    }
}
