package org.bcnlab.beaconlabscore.commands.time;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimeCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public TimeCommand(BeaconLabsCore plugin) {
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
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /time <value|day|night|noon|midnight>");
            return true;
        }

        World world = player.getWorld();
        String timeArg = args[0].toLowerCase();

        switch (timeArg) {
            case "day":
                world.setTime(0);
                player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Set time to day.");
                break;
            case "night":
                world.setTime(13000);
                player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Set time to night.");
                break;
            case "noon":
                world.setTime(6000);
                player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Set time to noon.");
                break;
            case "midnight":
                world.setTime(18000);
                player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Set time to midnight.");
                break;
            default:
                try {
                    long timeValue = Long.parseLong(timeArg);
                    world.setTime(timeValue);
                    player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Set time to " + timeValue + ".");
                } catch (NumberFormatException e) {
                    player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Invalid time value or keyword. Use: day, night, noon, midnight, or a number.");
                }
                break;
        }

        return true;
    }
}
