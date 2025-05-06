package one.tranic.goldpiglin.common;

import one.tranic.goldpiglin.GoldPiglin;
import one.tranic.goldpiglin.common.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!Config.isUpdateMessage()) return;
        Player player = event.getPlayer();
        if ((player.isOp() || player.hasPermission("goldpiglin.update_message")) && GoldPiglin.getFetchVersion().checkForUpdates())
            player.sendMessage(GoldPiglin.getFetchVersion().getUpdateMessage());
    }
}
