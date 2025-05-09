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
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GPiglinCommand extends Command {
    private final String permissionMessage = ChatColor.AQUA + "[GoldPiglin] " + ChatColor.RED + "You don't have permission to use this command!";

    public GPiglinCommand() {
        super("gpiglin");
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

    private TextComponent createLinkComponent(String prefix, String url, String hoverText) {
        TextComponent prefixComponent = new TextComponent(prefix);
        prefixComponent.setColor(ChatColor.YELLOW.asBungee());

        TextComponent linkComponent = new TextComponent(url);
        linkComponent.setUnderlined(true);
        linkComponent.setColor(ChatColor.AQUA.asBungee());
        linkComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        linkComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(hoverText).create()));

        TextComponent fullComponent = new TextComponent("");
        fullComponent.addExtra(prefixComponent);
        fullComponent.addExtra(linkComponent);

        return fullComponent;
    }

    private TextComponent createVersionComponent() {
        TextComponent versionPrefix = new TextComponent("Plugin Version: ");
        versionPrefix.setColor(ChatColor.YELLOW.asBungee());

        TextComponent versionNumber = new TextComponent(GoldPiglin.getPlugin().getDescription().getVersion());
        versionNumber.setColor(ChatColor.AQUA.asBungee());
        versionNumber.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Click to download update").create()));
        versionNumber.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                GoldPiglin.getFetchVersion().getResourceURL()));

        TextComponent fullMsg = new TextComponent("");
        fullMsg.addExtra(versionPrefix);
        fullMsg.addExtra(versionNumber);

        TextComponent space = new TextComponent(" ");
        TextComponent star = new TextComponent("*");
        star.setColor(ChatColor.WHITE.asBungee());

        TextComponent updateText = new TextComponent("(Update available)");
        updateText.setColor(ChatColor.GOLD.asBungee());
        updateText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Click to download update").create()));
        updateText.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                GoldPiglin.getFetchVersion().getResourceURL()));

        fullMsg.addExtra(space);
        fullMsg.addExtra(star);
        fullMsg.addExtra(space);
        fullMsg.addExtra(updateText);

        return fullMsg;
    }

    private TextComponent createGithubComponent() {
        return createLinkComponent("Github: ",
                "https://github.com/404Setup/GoldPiglin",
                "Github link");
    }

    private TextComponent createDiscordComponent() {
        return createLinkComponent("Discord: ",
                "https://discord.gg/PxgFqNmR2h",
                "Discord link");
    }

    private TextComponent createPatreonComponent() {
        return createLinkComponent("Patreon: ",
                "https://www.patreon.com/tranic",
                "Patreon link");
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

        sender.sendMessage(ChatColor.YELLOW + "Adapter: " + ChatColor.AQUA + GoldPiglin.getTargetSign());

        sender.spigot().sendMessage(createGithubComponent());
        sender.spigot().sendMessage(createDiscordComponent());
        sender.spigot().sendMessage(createPatreonComponent());
    }

    private void executeReload(@NotNull CommandSender sender) {
        if (!sender.hasPermission("goldpiglin.command.reload")) {
            sender.sendMessage(permissionMessage);
            return;
        }
        Config.reload(GoldPiglin.getPlugin());
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
