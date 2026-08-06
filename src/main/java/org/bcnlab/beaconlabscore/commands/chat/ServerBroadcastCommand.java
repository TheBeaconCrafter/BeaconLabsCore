package org.bcnlab.beaconlabscore.commands.chat;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ServerBroadcastCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public ServerBroadcastCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the command sender has permission
        if (!sender.hasPermission("beaconlabs.core.broadcast")) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>You do not have permission to use this command.")));
            return true;
        }

        // Validate command usage
        if (args.length < 1) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Usage: /sbc <message>")));
            return true;
        }

        // Concatenate all arguments to form the message
        String message = String.join(" ", args);

        // Format the broadcast message
        Component formattedMessage = LegacyComponentSerializer.legacyAmpersand().deserialize("&8[&6S-Broadcast&8] &r" + message);

        // Broadcast the message to the entire server
        Bukkit.broadcast(formattedMessage);

        return true;
    }
}
