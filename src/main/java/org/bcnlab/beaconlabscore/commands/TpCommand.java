package org.bcnlab.beaconlabscore.commands;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public TpCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the command sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        // Check permission
        if (!player.hasPermission("beaconlabs.core.tp")) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        // Validate command usage
        if (args.length < 1 || args.length > 2) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /tp <target> [destination]");
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        // Check if the target player is online
        if (target == null || !target.isOnline()) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Player '" + targetName + "' is not online.");
            return true;
        }

        // If only one argument is provided, teleport sender to target
        if (args.length == 1) {
            player.teleport(target.getLocation());
            player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Teleported to " + target.getName() + ".");
        } else if (args.length == 2) {
            String destinationName = args[1];
            Player destination = Bukkit.getPlayer(destinationName);

            // Check if the destination player is online
            if (destination == null || !destination.isOnline()) {
                player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Player '" + destinationName + "' is not online.");
                return true;
            }

            // Teleport target to destination
            target.teleport(destination.getLocation());
            player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Teleported " + target.getName() + " to " + destination.getName() + ".");
        }

        return true;
    }
}
