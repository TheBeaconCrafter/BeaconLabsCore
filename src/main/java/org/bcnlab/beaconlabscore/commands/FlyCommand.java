package org.bcnlab.beaconlabscore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {

    private final String pluginPrefix;

    public FlyCommand(String pluginPrefix) {
        this.pluginPrefix = ChatColor.translateAlternateColorCodes('&', pluginPrefix);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(pluginPrefix + ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0) {
            if (!sender.hasPermission("beaconlabs.core.fly.others")) {
                sender.sendMessage(pluginPrefix + ChatColor.RED + "You do not have permission to fly others.");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(pluginPrefix + ChatColor.RED + "Player not found: " + args[0]);
                return true;
            }

            boolean notify = true;
            if (args.length > 1 && (args[args.length - 1].equalsIgnoreCase("nonotify") || args[args.length - 1].equalsIgnoreCase("n"))) {
                notify = false;
            }

            toggleFlight(target, notify);

            if (notify) {
                sender.sendMessage(pluginPrefix + ChatColor.GREEN + "Toggled fly mode for " + target.getName() + ".");
                target.sendMessage(pluginPrefix + ChatColor.GREEN + "Your fly mode was toggled by " + player.getName() + ".");
            } else {
                sender.sendMessage(pluginPrefix + ChatColor.GREEN + "Toggled fly mode for " + target.getName() + " without notifying.");
            }
        } else {
            if (!sender.hasPermission("beaconlabs.core.fly.self")) {
                sender.sendMessage(pluginPrefix + ChatColor.RED + "You do not have permission to use this command.");
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
                player.sendMessage(pluginPrefix + ChatColor.RED + "Fly mode disabled.");
            }
        } else {
            player.setAllowFlight(true);
            player.setFlying(true);
            if (notify) {
                player.sendMessage(pluginPrefix + ChatColor.GREEN + "Fly mode enabled.");
            }
        }
    }
}
