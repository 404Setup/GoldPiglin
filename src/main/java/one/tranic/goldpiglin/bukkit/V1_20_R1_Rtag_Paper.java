package one.tranic.goldpiglin.bukkit;

import com.saicone.rtag.RtagItem;
import one.tranic.goldpiglin.paper.PaperBase;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class V1_20_R1_Rtag_Paper extends PaperBase {
    @Override
    public String getTargetSign() {
        return "RTag (Paper) 1.20.1";
    }

    @Override
    public boolean readItemStack(ItemStack armor) {
        var nbt = new RtagItem(armor).getOptional("Trim", "material");
        return nbt.isNotEmpty() && Objects.equals(nbt.asString(), "minecraft:gold");
    }
}
