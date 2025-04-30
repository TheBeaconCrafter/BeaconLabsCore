package org.bcnlab.beaconlabscore.commands.teleport;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bcnlab.beaconlabscore.utils.WarpManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DelWarpCommand implements CommandExecutor, TabCompleter {
    
    private final BeaconLabsCore plugin;
    private final WarpManager warpManager;
    
    public DelWarpCommand(BeaconLabsCore plugin, WarpManager warpManager) {
        this.plugin = plugin;
        this.warpManager = warpManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("beaconlabs.core.delwarp")) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.translateAlternateColorCodes('&', plugin.getNoPermsMessage()));
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /delwarp <name>");
            return true;
        }
        
        String warpName = args[0];
        
        if (!warpManager.warpExists(warpName)) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Warp '" + warpName + "' does not exist!");
            return true;
        }
        
        boolean success = warpManager.deleteWarp(warpName);
        
        if (success) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Warp '" + warpName + "' has been deleted.");
        } else {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Failed to delete warp '" + warpName + "'.");
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("beaconlabs.core.delwarp")) {
            String partialName = args[0].toLowerCase();
            return warpManager.getWarpNames().stream()
                    .filter(warp -> warp.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
