package one.tranic.goldpiglin.command;

import one.tranic.goldpiglin.GoldPiglin;
import one.tranic.goldpiglin.common.config.Config;
import one.tranic.goldpiglin.common.data.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GPiglinCommand extends Command {
    private final JavaPlugin plugin;
    private final String permissionMessage = ChatColor.AQUA + "[GoldPiglin] " + ChatColor.RED + "You don't have permission to use this command!";

    public GPiglinCommand(JavaPlugin plugin) {
        super("gpiglin");
        this.plugin = plugin;
        this.setUsage("/gpiglin <reload|version>");
    }

    private String getUsageMessage() {
        return ChatColor.AQUA + "[GoldPiglin] " + ChatColor.RED + "Usage: " + this.getUsage();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(getUsageMessage());
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            executeReload(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("version")) {
            executeVersion(sender);
            return true;
        }
        sender.sendMessage(getUsageMessage());
        return true;
    }

    private void executeReload(@NotNull CommandSender sender) {
        if (!sender.hasPermission("goldpiglin.command.reload")) {
            sender.sendMessage(permissionMessage);
            return;
        }
        Config.reload(this.plugin);
        sender.sendMessage(ChatColor.AQUA + "[GoldPiglin] " + ChatColor.GREEN + "The configuration file has been reloaded. Some changes require reloading the plugin or restarting the server to take effect.");
    }

    private void executeVersion(@NotNull CommandSender sender) {
        if (!sender.hasPermission("goldpiglin.command.version")) {
            sender.sendMessage(permissionMessage);
            return;
        }
        if (GoldPiglin.getFetchVersion().checkForUpdates()) sender.sendMessage(GoldPiglin.getFetchVersion().getUpdateMessage());
        else sender.sendMessage(GoldPiglin.getFetchVersion().getNoUpdateMessage());
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        List<String> list = Util.newArrayList();
        if (args.length == 1) {
            if (sender.hasPermission("goldpiglin.command.reload")) list.add("reload");
            if (sender.hasPermission("goldpiglin.command.version")) list.add("version");
        }
        return list;
    }
}
