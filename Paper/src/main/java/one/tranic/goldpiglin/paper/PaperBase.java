package one.tranic.goldpiglin.paper;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import one.tranic.goldpiglin.common.BaseTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public abstract class PaperBase extends BaseTarget {
    @EventHandler
    public void onPlayerArmorChange(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        playerCache.remove(player.getUniqueId());
    }
}
