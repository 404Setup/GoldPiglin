package one.tranic.goldpiglin.command;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import one.tranic.goldpiglin.GoldPiglin;
import one.tranic.goldpiglin.common.config.Config;
import one.tranic.t.utils.Collections;
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

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (args.length == 0) {
            execute(sender);
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
        execute(sender);
        return true;
    }

    private TextComponent createVersionComponent() {
        TextComponent versionPrefix = new TextComponent("Plugin Version: ");
        versionPrefix.setColor(ChatColor.YELLOW.asBungee());

        TextComponent versionNumber = new TextComponent(GoldPiglin.getPlugin().getDescription().getVersion());
        versionNumber.setColor(ChatColor.AQUA.asBungee());

        TextComponent space = new TextComponent(" ");

        TextComponent star = new TextComponent("*");
        star.setColor(ChatColor.WHITE.asBungee());

        TextComponent updateText = new TextComponent("(Update available [Click to download update])");
        updateText.setColor(ChatColor.YELLOW.asBungee());

        updateText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to download update").create()));

        updateText.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, GoldPiglin.getFetchVersion().getResourceURL()));

        TextComponent fullMsg = new TextComponent("");
        fullMsg.addExtra(versionPrefix);
        fullMsg.addExtra(versionNumber);
        fullMsg.addExtra(space);
        fullMsg.addExtra(star);
        fullMsg.addExtra(space);
        fullMsg.addExtra(updateText);

        return fullMsg;
    }

    private void execute(@NotNull CommandSender sender) {
        if (!sender.hasPermission("goldpiglin.command")) {
            sender.sendMessage(permissionMessage);
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "============" + ChatColor.AQUA + " GoldPiglin " + ChatColor.YELLOW + "============");
        sender.sendMessage(ChatColor.BLUE + "When you wear armor with gold patterns, the effect is the same as wearing gold armor.");
        sender.sendMessage(ChatColor.YELLOW + "2024 - 2025 by " + ChatColor.AQUA + "404");
        sender.sendMessage(ChatColor.YELLOW + "<Apache 2.0 License>");

        if (GoldPiglin.getFetchVersion().checkForUpdates()) {
            sender.spigot().sendMessage(createVersionComponent());
        } else
            sender.sendMessage(ChatColor.YELLOW + "Plugin Version: " + ChatColor.AQUA + GoldPiglin.getPlugin().getDescription().getVersion() + ChatColor.YELLOW + " (Latest)");

        sender.sendMessage(ChatColor.YELLOW + "Driver: " + ChatColor.AQUA + GoldPiglin.getTargetSign());

        // Github
        TextComponent prefix1 = new TextComponent("Github: ");
        prefix1.setColor(ChatColor.YELLOW.asBungee());

        TextComponent link1 = new TextComponent("https://github.com/404Setup/GoldPiglin");
        link1.setUnderlined(true);
        link1.setColor(ChatColor.AQUA.asBungee());
        link1.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/404Setup/GoldPiglin"));
        link1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Github link").create()));

        TextComponent fullMsg1 = new TextComponent("");
        fullMsg1.addExtra(prefix1);
        fullMsg1.addExtra(link1);

        sender.spigot().sendMessage(fullMsg1);

        // Discord
        TextComponent prefix2 = new TextComponent("Discord: ");
        prefix2.setColor(ChatColor.YELLOW.asBungee());

        TextComponent link2 = new TextComponent("https://discord.gg/PxgFqNmR2h");
        link2.setUnderlined(true);
        link2.setColor(ChatColor.AQUA.asBungee());
        link2.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/PxgFqNmR2h"));
        link2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Discord link").create()));

        TextComponent fullMsg2 = new TextComponent("");
        fullMsg2.addExtra(prefix2);
        fullMsg2.addExtra(link2);

        sender.spigot().sendMessage(fullMsg2);

        // Patreon
        TextComponent prefix3 = new TextComponent("Patreon: ");
        prefix3.setColor(ChatColor.YELLOW.asBungee());

        TextComponent link3 = new TextComponent("https://www.patreon.com/tranic");
        link3.setUnderlined(true);
        link3.setColor(ChatColor.AQUA.asBungee());
        link3.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.patreon.com/tranic"));
        link3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Patreon link").create()));

        TextComponent fullMsg3 = new TextComponent("");
        fullMsg3.addExtra(prefix3);
        fullMsg3.addExtra(link3);

        sender.spigot().sendMessage(fullMsg3);
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
        if (GoldPiglin.getFetchVersion().checkForUpdates())
            sender.sendMessage(GoldPiglin.getFetchVersion().getUpdateMessage());
        else sender.sendMessage(GoldPiglin.getFetchVersion().getNoUpdateMessage());
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        List<String> list = Collections.newArrayList();
        if (args.length == 1) {
            if (sender.hasPermission("goldpiglin.command.reload")) list.add("reload");
            if (sender.hasPermission("goldpiglin.command.version")) list.add("version");
        }
        return list;
    }
}
