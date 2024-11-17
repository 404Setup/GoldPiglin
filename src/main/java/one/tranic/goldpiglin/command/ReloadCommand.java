package one.tranic.goldpiglin.command;

import one.tranic.goldpiglin.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends Command {
    private final JavaPlugin plugin;

    public ReloadCommand(JavaPlugin plugin) {
        super("greload");
        this.plugin = plugin;
        this.setPermission("goldpiglin.command.greload");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        Config.reload(this.plugin);
        sender.sendMessage(ChatColor.GREEN + "Reloaded!");
        return true;
    }
}
