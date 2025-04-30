package org.bcnlab.beaconlabscore.commands.player;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the gamemode shortcut commands: /gms, /gmc, /gma, /gmsp
 */
public class GamemodeShortcutCommand implements CommandExecutor {
    
    private final BeaconLabsCore plugin;
    private final GameMode gameMode;
    private final String gameModeName;
    
    public GamemodeShortcutCommand(BeaconLabsCore plugin, GameMode gameMode) {
        this.plugin = plugin;
        this.gameMode = gameMode;
        
        // Set the friendly name for the gamemode
        switch (gameMode) {
            case CREATIVE:
                gameModeName = "Creative";
                break;
            case SURVIVAL:
                gameModeName = "Survival";
                break;
            case ADVENTURE:
                gameModeName = "Adventure";
                break;
            case SPECTATOR:
                gameModeName = "Spectator";
                break;
            default:
                gameModeName = gameMode.name();
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check for self permission or others permission
        if (args.length == 0) {
            // Setting own gamemode
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Console cannot change its own gamemode!");
                return true;
            }
            
            Player player = (Player) sender;
            if (!player.hasPermission("beaconlabs.core.gamemode.self")) {
                player.sendMessage(plugin.getPrefix() + ChatColor.translateAlternateColorCodes('&', plugin.getNoPermsMessage()));
                return true;
            }
            
            changeGameMode(player, player, false);
        } else {
            // Setting another player's gamemode
            if (!sender.hasPermission("beaconlabs.core.gamemode.others")) {
                sender.sendMessage(plugin.getPrefix() + ChatColor.translateAlternateColorCodes('&', plugin.getNoPermsMessage()));
                return true;
            }
            
            String targetName = args[0];
            Player target = Bukkit.getPlayer(targetName);
            
            if (target == null) {
                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Player '" + targetName + "' not found or is not online.");
                return true;
            }
            
            boolean silent = args.length > 1 && args[1].equalsIgnoreCase("nonotify");
            changeGameMode(sender, target, silent);
        }
        
        return true;
    }
    
    private void changeGameMode(CommandSender sender, Player target, boolean silent) {
        target.setGameMode(gameMode);
        
        if (!silent) {
            target.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Your gamemode has been changed to " + ChatColor.GOLD + gameModeName + ChatColor.GREEN + ".");
        }
        
        if (sender != target && !silent) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Changed " + ChatColor.GOLD + target.getName() + ChatColor.GREEN + "'s gamemode to " + ChatColor.GOLD + gameModeName + ChatColor.GREEN + ".");
        }
    }
}
