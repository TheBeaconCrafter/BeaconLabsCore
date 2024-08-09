package org.bcnlab.beaconlabscore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bcnlab.beaconlabscore.commands.TpaCommand.tpaRequests;

public class TpAcceptCommand implements CommandExecutor {

    private final String pluginPrefix;

    public TpAcceptCommand(String pluginPrefix) {
        this.pluginPrefix = ChatColor.translateAlternateColorCodes('&', pluginPrefix);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(pluginPrefix + ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player target = (Player) sender;

        if (!sender.hasPermission("beaconlabs.core.tpaccept")) {
            sender.sendMessage(pluginPrefix + ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(pluginPrefix + ChatColor.RED + "Usage: /tpaccept <player>");
            return true;
        }

        String requestingPlayerName = args[0];
        if (!tpaRequests.containsKey(target.getName()) || !tpaRequests.get(target.getName()).equals(requestingPlayerName)) {
            sender.sendMessage(pluginPrefix + ChatColor.RED + "No pending teleport request from " + requestingPlayerName + ".");
            return true;
        }

        Player requester = Bukkit.getPlayer(requestingPlayerName);
        if (requester == null) {
            sender.sendMessage(pluginPrefix + ChatColor.RED + "Player " + requestingPlayerName + " is not online.");
            return true;
        }

        requester.teleport(target);
        requester.sendMessage(pluginPrefix + ChatColor.GREEN + "Teleport request accepted. Teleporting to " + target.getName() + ".");
        target.sendMessage(pluginPrefix + ChatColor.GREEN + "You have accepted the teleport request from " + requester.getName() + ".");

        tpaRequests.remove(target.getName());
        return true;
    }
}
