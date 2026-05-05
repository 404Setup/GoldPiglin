package one.pkg.goldpiglin.paper;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import one.pkg.goldpiglin.common.BaseTarget;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public abstract class PaperBase extends BaseTarget {
    @EventHandler
    public void onPlayerArmorChange(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR)) return;
        playerCache.remove(player.getUniqueId());
    }
}
