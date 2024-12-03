package one.tranic.goldpiglin.bukkit.common;

import one.tranic.goldpiglin.GoldPiglin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("goldpiglin.update_message")) return;
        if (!GoldPiglin.getFetchVersion().checkForUpdates()) return;
        player.sendMessage(GoldPiglin.getFetchVersion().getUpdateMessage());
    }
}
