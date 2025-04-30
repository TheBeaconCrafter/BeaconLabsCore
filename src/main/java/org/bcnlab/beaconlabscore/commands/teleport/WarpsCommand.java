package org.bcnlab.beaconlabscore.commands.teleport;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bcnlab.beaconlabscore.utils.WarpManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WarpsCommand implements CommandExecutor, TabCompleter {
    
    private final BeaconLabsCore plugin;
    private final WarpManager warpManager;
    
    public WarpsCommand(BeaconLabsCore plugin, WarpManager warpManager) {
        this.plugin = plugin;
        this.warpManager = warpManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("beaconlabs.core.warps")) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.translateAlternateColorCodes('&', plugin.getNoPermsMessage()));
            return true;
        }
        
        Set<String> warpNames = warpManager.getWarpNames();
        
        if (warpNames.isEmpty()) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + "There are no warps set on this server.");
            return true;
        }
        
        sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Available warps:");
        
        StringBuilder warpList = new StringBuilder();
        int count = 0;
        
        for (String warp : warpNames) {
            if (count > 0) {
                warpList.append(ChatColor.GRAY).append(", ");
            }
            warpList.append(ChatColor.AQUA).append(warp);
            count++;
        }
        
        sender.sendMessage(warpList.toString());
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // No tab completion for /warps command as it just shows the list
        return new ArrayList<>();
    }
}
