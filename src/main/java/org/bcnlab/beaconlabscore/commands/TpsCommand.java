package org.bcnlab.beaconlabscore.commands;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class TpsCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public TpsCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("beaconlabs.core.tps")) {
                player.sendMessage(plugin.getPrefix() + "§cYou do not have permission to use this command.");
                return true;
            }
        }

        double tps = getServerTps();
        int totalEntities = 0;
        int livingEntities = 0;

        for (Entity entity : Bukkit.getWorlds().get(0).getEntities()) { // Iterating over entities in the first world
            totalEntities++;
            if (entity.getType().isAlive()) {
                livingEntities++;
            }
        }

        String message = String.format(
                "§aServer TPS: §b%.2f\n§aTotal Entities: §b%d\n§aLiving Entities: §b%d",
                tps, totalEntities, livingEntities
        );

        sender.sendMessage(plugin.getPrefix() + message);
        return true;
    }

    private double getServerTps() {
        try {
            return Bukkit.getServerTickManager().getTickRate(); // Returning the 1-minute TPS
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
