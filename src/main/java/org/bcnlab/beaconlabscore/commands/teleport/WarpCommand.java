package org.bcnlab.beaconlabscore.commands.teleport;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bcnlab.beaconlabscore.utils.WarpManager;
import org.bukkit.Bukkit;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class WarpCommand implements CommandExecutor, TabCompleter {
    
    private final BeaconLabsCore plugin;
    private final WarpManager warpManager;
    private final Map<UUID, ScheduledTask> pendingTeleports;
    
    public WarpCommand(BeaconLabsCore plugin, WarpManager warpManager) {
        this.plugin = plugin;
        this.warpManager = warpManager;
        this.pendingTeleports = new HashMap<>();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>This command can only be used by players.")));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("beaconlabs.core.warp")) {
            player.sendMessage(plugin.getPrefix(player).append(LegacyComponentSerializer.legacyAmpersand().deserialize(plugin.getNoPermsMessage())));
            return true;
        }
        
        if (args.length < 1) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Usage: /warp <name>")));
            return true;
        }
        
        String warpName = args[0];
        
        if (!warpManager.warpExists(warpName)) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Warp '" + warpName + "' does not exist!")));
            return true;
        }
        
        // Check if player has a pending teleport
        if (pendingTeleports.containsKey(player.getUniqueId())) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>You already have a pending warp teleport!")));
            return true;
        }
        
        // Get teleport delay from config
        int delay = plugin.getWarpDelay();
        
        // Check for bypass permission
        if (player.hasPermission("beaconlabs.core.warp.nodelay") || delay <= 0) {
            teleportToWarp(player, warpName);
        } else {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Teleporting to warp '" + warpName + "' in " + delay + " seconds. Don't move!")));
            
            // Player's current location for movement check
            Location startLocation = player.getLocation();
            
            // Schedule delayed teleport
            ScheduledTask task = player.getScheduler().runDelayed(plugin, (scheduledTask) -> {
                // Check if player moved
                if (!locationEquals(startLocation, player.getLocation())) {
                    player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Teleport cancelled because you moved!")));
                } else {
                    teleportToWarp(player, warpName);
                }
                pendingTeleports.remove(player.getUniqueId());
            }, null, delay * 20L); // Convert seconds to ticks
            
            pendingTeleports.put(player.getUniqueId(), task);
        }
        
        return true;
    }
    
    private void teleportToWarp(Player player, String warpName) {
        Location location = warpManager.getWarp(warpName);
        player.teleportAsync(location);
        player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Teleported to warp '" + warpName + "'.")));
    }
    
    private boolean locationEquals(Location loc1, Location loc2) {
        return loc1.getWorld().equals(loc2.getWorld()) && 
               loc1.getBlockX() == loc2.getBlockX() && 
               loc1.getBlockY() == loc2.getBlockY() && 
               loc1.getBlockZ() == loc2.getBlockZ();
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String partialName = args[0].toLowerCase();
            return warpManager.getWarpNames().stream()
                    .filter(warp -> warp.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
