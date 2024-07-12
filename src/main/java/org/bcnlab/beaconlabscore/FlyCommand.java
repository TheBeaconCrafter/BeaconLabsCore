package org.bcnlab.beaconlabscore;

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

            if (player.getAllowFlight()) {
                target.setAllowFlight(false);
            } else {
                target.setAllowFlight(true);
            }

            sender.sendMessage(pluginPrefix + ChatColor.GREEN + "Made " + target.getName() + " fly.");
            target.sendMessage(pluginPrefix + ChatColor.GREEN + "Your fly mode was changed by " + player.getName() + ".");
        } else {
            if (!sender.hasPermission("beaconlabs.core.fly.self")) {
                sender.sendMessage(pluginPrefix + ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }
            if (player.getAllowFlight()) {
                player.setAllowFlight(false);
                player.sendMessage(pluginPrefix + ChatColor.RED + "Fly mode disabled.");
            } else {
                player.setAllowFlight(true);
                player.sendMessage(pluginPrefix + ChatColor.GREEN + "Fly mode enabled.");
            }
        }
        return true;
    }
}
