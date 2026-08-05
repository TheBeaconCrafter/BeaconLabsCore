package org.bcnlab.beaconlabscore.commands.teleport;

import org.bukkit.Bukkit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bcnlab.beaconlabscore.commands.teleport.TpaCommand.tpaRequests;

public class TpAcceptCommand implements CommandExecutor {

    private final Component pluginPrefix;

    public TpAcceptCommand(String pluginPrefix) {
        this.pluginPrefix = LegacyComponentSerializer.legacyAmpersand().deserialize(pluginPrefix);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<red>This command can only be used by players.")));
            return true;
        }

        Player target = (Player) sender;

        if (!sender.hasPermission("beaconlabs.core.tpaccept")) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<red>You do not have permission to use this command.")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<red>Usage: /tpaccept <player>")));
            return true;
        }

        String requestingPlayerName = args[0];
        if (!tpaRequests.containsKey(target.getName()) || !tpaRequests.get(target.getName()).equals(requestingPlayerName)) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<red>No pending teleport request from " + requestingPlayerName + ".")));
            return true;
        }

        Player requester = Bukkit.getPlayer(requestingPlayerName);
        if (requester == null) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<red>Player " + requestingPlayerName + " is not online.")));
            return true;
        }

        requester.teleport(target);
        requester.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Teleport request accepted. Teleporting to " + target.getName() + ".")));
        target.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>You have accepted the teleport request from " + requester.getName() + ".")));

        tpaRequests.remove(target.getName());
        return true;
    }
}
