package org.bcnlab.beaconlabscore.commands.weather;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.World;

public class WeatherCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public WeatherCommand(BeaconLabsCore plugin) {
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

        if (args.length == 0) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /weather <clear|rain|storm>");
            return true;
        }

        World world = player.getWorld();
        String weatherType = args[0].toLowerCase();

        switch (weatherType) {
            case "clear":
                world.setStorm(false);
                world.setThundering(false);
                player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Set weather to clear.");
                break;
            case "rain":
                world.setStorm(true);
                world.setThundering(false);
                player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Set weather to rain.");
                break;
            case "storm":
                world.setStorm(true);
                world.setThundering(true);
                player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Set weather to thunderstorm.");
                break;
            default:
                player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Invalid weather type. Use: clear, rain, storm");
                break;
        }

        return true;
    }
}
