package one.tranic.goldpiglin.bukkit.napi.v1_20_5;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import one.tranic.goldpiglin.paper.PaperBase;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class N1205_Paper extends PaperBase {
    @Override
    public String getTargetSign() {
        return "NBTAPI (Paper) 1.20.5";
    }

    @Override
    public boolean readItemStack(ItemStack armor) {
        ReadableNBT nbt = NBT.itemStackToNBT(armor).getCompound("components");
        if (nbt == null) return false;
        ReadableNBT trim = nbt.getCompound("minecraft:trim");
        return trim != null && Objects.equals(trim.getString("material"), "minecraft:gold");
    }
}
