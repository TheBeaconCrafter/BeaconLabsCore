package org.bcnlab.beaconlabscore.commands;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoreCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public CoreCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        if (!sender.hasPermission("beaconlabs.core.info")) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        Player player = (Player) sender;
        player.sendMessage(plugin.getPrefix() + ChatColor.RED + "BeaconLabsCore Version " + ChatColor.GOLD + plugin.getVersion() + ChatColor.RED + " by ItsBeacon");
        return true;
    }
}
