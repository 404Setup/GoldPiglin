package one.pkg.goldpiglin.bukkit;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class V1_20_R1_NAPI_Bukkit extends BukkitBase {
    @Override
    public String getTargetSign() {
        return "NBTAPI (Bukkit) 1.20.1";
    }

    @Override
    public boolean readItemStack(ItemStack armor) {
        ReadableNBT nbt = NBT.itemStackToNBT(armor).getCompound("Trim");
        return nbt != null && Objects.equals(nbt.getString("material"), "minecraft:gold");
    }
}
