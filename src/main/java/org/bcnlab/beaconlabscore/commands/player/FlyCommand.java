package org.bcnlab.beaconlabscore.commands.player;

import org.bukkit.Bukkit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {

    private final Component pluginPrefix;

    public FlyCommand(String pluginPrefix) {
        this.pluginPrefix = LegacyComponentSerializer.legacyAmpersand().deserialize(pluginPrefix);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<red>This command can only be used by players.")));
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0) {
            if (!sender.hasPermission("beaconlabs.core.fly.others")) {
                sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<red>You do not have permission to fly others.")));
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<red>Player not found: " + args[0])));
                return true;
            }

            boolean notify = true;
            if (args.length > 1 && (args[args.length - 1].equalsIgnoreCase("nonotify") || args[args.length - 1].equalsIgnoreCase("n"))) {
                notify = false;
            }

            toggleFlight(target, notify);

            if (notify) {
                sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Toggled fly mode for " + target.getName() + ".")));
                target.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Your fly mode was toggled by " + player.getName() + ".")));
            } else {
                sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Toggled fly mode for " + target.getName() + " without notifying.")));
            }
        } else {
            if (!sender.hasPermission("beaconlabs.core.fly.self")) {
                sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<red>You do not have permission to use this command.")));
                return true;
            }

            toggleFlight(player, true);
        }
        return true;
    }

    private void toggleFlight(Player player, boolean notify) {
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            if (notify) {
                player.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<red>Fly mode disabled.")));
            }
        } else {
            player.setAllowFlight(true);
            player.setFlying(true);
            if (notify) {
                player.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Fly mode enabled.")));
            }
        }
    }
}
