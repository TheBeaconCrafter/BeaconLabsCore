package org.bcnlab.beaconlabscore.commands.weather;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.World;

public class ClearCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public ClearCommand(BeaconLabsCore plugin) {
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
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /clear");
            return true;
        }

        World world = player.getWorld();

        world.setStorm(false);
        world.setThundering(false);
        player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Set weather to clear.");

        return true;
    }
}
