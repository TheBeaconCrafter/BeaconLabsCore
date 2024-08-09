package org.bcnlab.beaconlabscore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TpaCommand implements CommandExecutor {

    private final String pluginPrefix;
    public static final Map<String, String> tpaRequests = new HashMap<>();

    public TpaCommand(String pluginPrefix) {
        this.pluginPrefix = ChatColor.translateAlternateColorCodes('&', pluginPrefix);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(pluginPrefix + ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (!sender.hasPermission("beaconlabs.core.tpa")) {
            sender.sendMessage(pluginPrefix + ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(pluginPrefix + ChatColor.RED + "Usage: /tpa <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(pluginPrefix + ChatColor.RED + "Player not found: " + args[0]);
            return true;
        }

        if (target.getName().equals(player.getName())) {
            sender.sendMessage(pluginPrefix + ChatColor.RED + "You cannot send a teleport request to yourself.");
            return true;
        }

        tpaRequests.put(target.getName(), player.getName());
        sender.sendMessage(pluginPrefix + ChatColor.GREEN + "Teleport request sent to " + target.getName() + ".");
        target.sendMessage(pluginPrefix + ChatColor.GOLD + player.getName() + " has requested to teleport to you. Type" + ChatColor.RED +" /tpaccept " + player.getName() + ChatColor.GOLD +" to accept or" + ChatColor.RED + "/tpdeny" + ChatColor.GOLD + " to deny.");

        return true;
    }
}
