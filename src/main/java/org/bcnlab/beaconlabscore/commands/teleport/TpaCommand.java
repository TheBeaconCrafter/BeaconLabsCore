package org.bcnlab.beaconlabscore.commands.teleport;

import org.bukkit.Bukkit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TpaCommand implements io.papermc.paper.command.brigadier.BasicCommand {

    private final Component pluginPrefix;
    public static final Map<String, String> tpaRequests = new HashMap<>();

    public TpaCommand(String pluginPrefix) {
        this.pluginPrefix = LegacyComponentSerializer.legacyAmpersand().deserialize(pluginPrefix);
    }

    @Override
    public void execute(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (!(sender instanceof Player)) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>This command can only be used by players.")));
            return;
        }

        Player player = (Player) sender;

        if (!sender.hasPermission("beaconlabs.core.tpa")) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>You do not have permission to use this command.")));
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>Usage: /tpa <player>")));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>Player not found: " + args[0])));
            return;
        }

        if (target.getName().equals(player.getName())) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>You cannot send a teleport request to yourself.")));
            return;
        }

        tpaRequests.put(target.getName(), player.getName());
        sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>Teleport request sent to " + target.getName() + ".")));
        target.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gold>" + player.getName() + " has requested to teleport to you. Type<gold> /tpaccept " + player.getName() + "<gold> to accept or<gold> /tpdeny<gold> to deny.")));

        return;
    }

    @Override
    public java.util.Collection<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        return args.length <= 1 ? org.bcnlab.beaconlabscore.commands.CommandCompletion.players(
                org.bcnlab.beaconlabscore.commands.CommandCompletion.argument(args, 0)) : java.util.List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.tpa");
    }
}
