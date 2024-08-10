package org.bcnlab.beaconlabscore.commands.time;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DayCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public DayCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("beaconlabs.core.time")) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "You do not have permission to change the time.");
            return true;
        }

        if (args.length > 0) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /day");
            return true;
        }

        World world = player.getWorld();

        world.setTime(0);
        player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Set time to day.");

        return true;
    }
}
