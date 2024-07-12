package org.bcnlab.beaconlabscore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ServerBroadcastCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public ServerBroadcastCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the command sender has permission
        if (!sender.hasPermission("beaconlab.core.broadcast")) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        // Validate command usage
        if (args.length < 1) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /sbc <message>");
            return true;
        }

        // Concatenate all arguments to form the message
        String message = String.join(" ", args);

        // Format the broadcast message
        String formattedMessage = ChatColor.translateAlternateColorCodes('&', "&8[&6S-Broadcast&8] &r" + message);

        // Broadcast the message to the entire server
        Bukkit.broadcastMessage(formattedMessage);

        return true;
    }
}
