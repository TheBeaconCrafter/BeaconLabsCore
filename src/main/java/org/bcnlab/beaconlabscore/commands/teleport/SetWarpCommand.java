package org.bcnlab.beaconlabscore.commands.teleport;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bcnlab.beaconlabscore.utils.WarpManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetWarpCommand implements CommandExecutor {
    
    private final BeaconLabsCore plugin;
    private final WarpManager warpManager;
    
    public SetWarpCommand(BeaconLabsCore plugin, WarpManager warpManager) {
        this.plugin = plugin;
        this.warpManager = warpManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>This command can only be used by players.")));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("beaconlabs.core.setwarp")) {
            player.sendMessage(plugin.getPrefix(player).append(LegacyComponentSerializer.legacyAmpersand().deserialize(plugin.getNoPermsMessage())));
            return true;
        }
        
        if (args.length < 1) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Usage: /setwarp <name>")));
            return true;
        }
        
        String warpName = args[0];
        
        // Validate warp name (alphanumeric and underscore only)
        if (!warpName.matches("^[a-zA-Z0-9_]+$")) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Warp names can only contain letters, numbers, and underscores.")));
            return true;
        }
        
        if (warpManager.warpExists(warpName)) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>A warp with that name already exists! Use /delwarp first if you want to replace it.")));
            return true;
        }
        
        boolean success = warpManager.createWarp(warpName, player.getLocation());
        
        if (success) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Warp '" + warpName + "' has been created.")));
        } else {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Failed to create warp '" + warpName + "'.")));
        }
        
        return true;
    }
}
