package org.bcnlab.beaconlabscore.commands.teleport;

import org.bukkit.Bukkit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bcnlab.beaconlabscore.commands.teleport.TpaCommand.tpaRequests;

public class TpAcceptCommand implements io.papermc.paper.command.brigadier.BasicCommand {

    private final Component pluginPrefix;

    public TpAcceptCommand(String pluginPrefix) {
        this.pluginPrefix = LegacyComponentSerializer.legacyAmpersand().deserialize(pluginPrefix);
    }

    @Override
    public void execute(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (!(sender instanceof Player)) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>This command can only be used by players.")));
            return;
        }

        Player target = (Player) sender;

        if (!sender.hasPermission("beaconlabs.core.tpaccept")) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>You do not have permission to use this command.")));
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>Usage: /tpaccept <player>")));
            return;
        }

        String requestingPlayerName = args[0];
        if (!tpaRequests.containsKey(target.getName()) || !tpaRequests.get(target.getName()).equals(requestingPlayerName)) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>No pending teleport request from " + requestingPlayerName + ".")));
            return;
        }

        Player requester = Bukkit.getPlayer(requestingPlayerName);
        if (requester == null) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>Player " + requestingPlayerName + " is not online.")));
            return;
        }

        requester.teleport(target);
        requester.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>Teleport request accepted. Teleporting to " + target.getName() + ".")));
        target.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>You have accepted the teleport request from " + requester.getName() + ".")));

        tpaRequests.remove(target.getName());
        return;
    }

    @Override
    public java.util.Collection<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        return args.length <= 1 ? org.bcnlab.beaconlabscore.commands.CommandCompletion.players(
                org.bcnlab.beaconlabscore.commands.CommandCompletion.argument(args, 0)) : java.util.List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.tpaccept");
    }
}
