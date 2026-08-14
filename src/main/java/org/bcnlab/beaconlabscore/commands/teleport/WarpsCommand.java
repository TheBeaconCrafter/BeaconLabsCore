package org.bcnlab.beaconlabscore.commands.teleport;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bcnlab.beaconlabscore.utils.WarpManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WarpsCommand implements io.papermc.paper.command.brigadier.BasicCommand {
    
    private final BeaconLabsCore plugin;
    private final WarpManager warpManager;
    
    public WarpsCommand(BeaconLabsCore plugin, WarpManager warpManager) {
        this.plugin = plugin;
        this.warpManager = warpManager;
    }
    
    @Override
    public void execute(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (!sender.hasPermission("beaconlabs.core.warps")) {
            sender.sendMessage(plugin.getPrefix(sender).append(LegacyComponentSerializer.legacyAmpersand().deserialize(plugin.getNoPermsMessage())));
            return;
        }
        
        Set<String> warpNames = warpManager.getWarpNames();
        
        if (warpNames.isEmpty()) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>There are no warps set on this server.")));
            return;
        }
        
        sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Available warps:")));
        
        StringBuilder warpList = new StringBuilder();
        int count = 0;
        
        for (String warp : warpNames) {
            if (count > 0) {
                warpList.append("<gray>, ");
            }
            warpList.append("<gray>").append(warp);
            count++;
        }
        
        sender.sendMessage(MiniMessage.miniMessage().deserialize(warpList.toString()));
        
        return;
    }
    
    @Override
    public List<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        // No tab completion for /warps command as it just shows the list
        return new ArrayList<>();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.warps");
    }
}
