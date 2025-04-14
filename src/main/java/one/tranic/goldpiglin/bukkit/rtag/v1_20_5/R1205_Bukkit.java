package one.tranic.goldpiglin.bukkit.rtag.v1_20_5;

import com.saicone.rtag.RtagItem;
import com.saicone.rtag.data.ComponentType;
import one.tranic.goldpiglin.bukkit.BukkitBase;

import java.util.Objects;

@SuppressWarnings("all")
public class R1205_Bukkit extends BukkitBase {
    @Override
    public String getTargetSign() {
        return "RTag (Bukkit) 1.20.5";
    }

    @Override
    public boolean readItemStack(org.bukkit.inventory.ItemStack armor) {
        var item = new RtagItem(armor);
        var c = item.getComponent("minecraft:trim");
        if (c == null) return false;
        var t1 = ComponentType.encodeJson("minecraft:trim", c);
        if (t1.isEmpty()) return false;
        var material = t1.get().getAsJsonObject().get("material");
        return material != null && Objects.equals(material.getAsString(), "minecraft:gold");
    }
}
