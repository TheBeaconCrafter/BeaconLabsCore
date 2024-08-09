package org.bcnlab.beaconlabscore.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;

public class RandomTeleportCommand implements CommandExecutor {

    private final String pluginPrefix;
    private final int maxRange; // Maximum range for teleportation (e.g., 5000 blocks)
    private final int minHeight = 64; // Minimum height to start checking for safe locations
    private final int maxHeight = 256; // Maximum height to check for safe locations

    public RandomTeleportCommand(String pluginPrefix, int maxRange) {
        this.pluginPrefix = ChatColor.translateAlternateColorCodes('&', pluginPrefix);
        this.maxRange = maxRange;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(pluginPrefix + ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        Location randomLocation = getRandomSafeLocation(player.getWorld(), maxRange);

        if (!sender.hasPermission("beaconlabs.core.rtp")) {
            sender.sendMessage(pluginPrefix + ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (randomLocation != null) {
            player.teleport(randomLocation);
            player.sendMessage(pluginPrefix + ChatColor.GREEN + "Teleported to a random location: " +
                    "X: " + randomLocation.getBlockX() +
                    " Y: " + randomLocation.getBlockY() +
                    " Z: " + randomLocation.getBlockZ());
        } else {
            player.sendMessage(pluginPrefix + ChatColor.RED + "Failed to find a safe location to teleport.");
        }

        return true;
    }

    private Location getRandomSafeLocation(World world, int range) {
        Random random = new Random();
        Location randomLocation = null;

        for (int i = 0; i < 10; i++) { // Try up to 10 times to find a safe location
            int x = random.nextInt(range * 2) - range;
            int z = random.nextInt(range * 2) - range;
            int y = getSafeY(world, x, z);

            if (y != -1) {
                randomLocation = new Location(world, x, y, z);
                break;
            }
        }

        return randomLocation;
    }

    private int getSafeY(World world, int x, int z) {
        for (int y = minHeight; y < maxHeight; y++) {
            Location location = new Location(world, x, y, z);
            if (isSafeLocation(location)) {
                return y;
            }
        }
        return -1;
    }

    private boolean isSafeLocation(Location location) {
        Material feetBlock = location.getBlock().getType();
        Material headBlock = location.clone().add(0, 1, 0).getBlock().getType();
        Material groundBlock = location.clone().subtract(0, 1, 0).getBlock().getType();

        // Check if the feet and head locations are air and the block beneath is solid
        return feetBlock == Material.AIR && headBlock == Material.AIR && groundBlock.isSolid() && groundBlock != Material.LAVA;
    }
}
