package org.bcnlab.beaconlabscore.commands.teleport;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bcnlab.beaconlabscore.utils.WarpManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DelWarpCommand implements io.papermc.paper.command.brigadier.BasicCommand {
    
    private final BeaconLabsCore plugin;
    private final WarpManager warpManager;
    
    public DelWarpCommand(BeaconLabsCore plugin, WarpManager warpManager) {
        this.plugin = plugin;
        this.warpManager = warpManager;
    }
    
    @Override
    public void execute(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (!sender.hasPermission("beaconlabs.core.delwarp")) {
            sender.sendMessage(plugin.getPrefix(sender).append(LegacyComponentSerializer.legacyAmpersand().deserialize(plugin.getNoPermsMessage())));
            return;
        }
        
        if (args.length < 1) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Usage: /delwarp <name>")));
            return;
        }
        
        String warpName = args[0];
        
        if (!warpManager.warpExists(warpName)) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Warp '" + warpName + "' does not exist!")));
            return;
        }
        
        boolean success = warpManager.deleteWarp(warpName);
        
        if (success) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Warp '" + warpName + "' has been deleted.")));
        } else {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Failed to delete warp '" + warpName + "'.")));
        }
        
        return;
    }
    
    @Override
    public List<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (args.length <= 1 && sender.hasPermission("beaconlabs.core.delwarp")) {
            String partialName = org.bcnlab.beaconlabscore.commands.CommandCompletion
                    .argument(args, 0).toLowerCase();
            return warpManager.getWarpNames().stream()
                    .filter(warp -> warp.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.delwarp");
    }
}
