package one.tranic.goldpiglin.bukkit.napi.v1_20_1;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import one.tranic.goldpiglin.paper.PaperBase;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class N1201_Paper extends PaperBase {
    @Override
    public String getTargetSign() {
        return "NBTAPI (Paper) 1.20.1";
    }

    @Override
    public boolean readItemStack(ItemStack armor) {
        ReadableNBT nbt = NBT.itemStackToNBT(armor).getCompound("Trim");
        return nbt != null && Objects.equals(nbt.getString("material"), "minecraft:gold");
    }
}
