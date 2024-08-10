package org.bcnlab.beaconlabscore.commands;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpcCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public TpcCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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

        if (args.length < 3) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /tp <player> <x> <y> <z> | /tp <x> <y> <z>");
            return false;
        }

        if (args.length == 4) {
            // Teleport a player to coordinates
            String targetPlayerName = args[0];
            double x, y, z;
            try {
                x = Double.parseDouble(args[1]);
                y = Double.parseDouble(args[2]);
                z = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Coordinates must be numbers.");
                return false;
            }

            Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
            if (targetPlayer == null) {
                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Player not found.");
                return false;
            }

            targetPlayer.teleport(new org.bukkit.Location(targetPlayer.getWorld(), x, y, z));
            sender.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "Teleported " + targetPlayerName + " to (" + x + ", " + y + ", " + z + ").");

        } else if (args.length == 3) {
            // Teleport the sender to coordinates
            double x, y, z;
            try {
                x = Double.parseDouble(args[0]);
                y = Double.parseDouble(args[1]);
                z = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Coordinates must be numbers.");
                return false;
            }

            player.teleport(new org.bukkit.Location(player.getWorld(), x, y, z));
            sender.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "Teleported you to (" + x + ", " + y + ", " + z + ").");

        } else {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /tp <player> <x> <y> <z> | /tp <x> <y> <z>");
            return false;
        }

        return true;
    }
}
