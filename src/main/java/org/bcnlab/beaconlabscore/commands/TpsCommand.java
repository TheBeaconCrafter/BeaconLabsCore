package org.bcnlab.beaconlabscore.commands;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class TpsCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public TpsCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("beaconlabs.core.stats")) {
                player.sendMessage(plugin.getPrefix() + "§cYou do not have permission to use this command.");
                return true;
            }
        }

        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();

        int totalEntities = 0;
        int livingEntities = 0;

        for (org.bukkit.World world : Bukkit.getWorlds()) { // Iterate over all worlds
            for (Entity entity : world.getEntities()) {
                totalEntities++;
                if (entity.getType().isAlive()) {
                    livingEntities++;
                }
            }
        }

        String message = String.format(
                "§aMemory Usage:\n§bUsed: §e%.2f MB\n§bFree: §e%.2f MB\n§bTotal: §e%.2f MB\n§bMax: §e%.2f MB\n" +
                        "§aEntities:\n§bTotal Entities: §e%d\n§bLiving Entities: §e%d",
                usedMemory / 1048576.0,
                freeMemory / 1048576.0,
                totalMemory / 1048576.0,
                maxMemory / 1048576.0,
                totalEntities,
                livingEntities
        );

        sender.sendMessage(plugin.getPrefix() + message);
        return true;
    }
}
