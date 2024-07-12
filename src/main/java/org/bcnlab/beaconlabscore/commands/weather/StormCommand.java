package org.bcnlab.beaconlabscore.commands.weather;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.World;

public class StormCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public StormCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("beaconlabs.core.weather")) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "You do not have permission to change the weather.");
            return true;
        }

        if (args.length > 0) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /storm");
            return true;
        }

        World world = player.getWorld();

        world.setStorm(true);
        world.setThundering(true);
        player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Set weather to storm.");

        return true;
    }
}
