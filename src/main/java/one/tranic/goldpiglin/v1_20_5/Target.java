package one.tranic.goldpiglin.v1_20_5;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import one.tranic.goldpiglin.common.base.BaseTarget;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class Target extends BaseTarget {
    @Override
    public boolean readItemStack(ItemStack armor) {
        ReadableNBT nbt = NBT.itemStackToNBT(armor).getCompound("components");
        if (nbt == null) return false;
        ReadableNBT trim = nbt.getCompound("minecraft:trim");
        return trim != null && Objects.equals(trim.getString("material"), "minecraft:gold");
    }
}
