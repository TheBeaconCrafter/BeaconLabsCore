package org.bcnlab.beaconlabscore;

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
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.sendMessage(pluginPrefix + ChatColor.RED + "Fly mode disabled.");
        } else {
            player.setAllowFlight(true);
            player.sendMessage(pluginPrefix + ChatColor.GREEN + "Fly mode enabled.");
        }
        return true;
    }
}
