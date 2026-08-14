package org.bcnlab.beaconlabscore.commands.chat;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatSudoCommand implements io.papermc.paper.command.brigadier.BasicCommand {

    private final BeaconLabsCore plugin;
    private final LuckPerms luckPerms;

    public ChatSudoCommand(BeaconLabsCore plugin, LuckPerms luckPerms) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
    }

    @Override
    public void execute(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        // Check if the command sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Only players can use this command!")));
            return;
        }

        Player player = (Player) sender;

        // Check permission
        if (!player.hasPermission("beaconlab.core.csudo")) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>You do not have permission to use this command.")));
            return;
        }

        // Validate command usage
        if (args.length < 2) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Usage: /csudo <player> <message>")));
            return;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        // Check if the target player is online
        if (target == null || !target.isOnline()) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Player '" + targetName + "' is not online.")));
            return;
        }

        User user = luckPerms.getUserManager().getUser(target.getUniqueId());

        String prefix = user.getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getPrefix();
        String suffix = user.getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getSuffix();

        if (prefix == null) prefix = "";
        if (suffix == null) suffix = "";

        // Construct the message to send
        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            messageBuilder.append(args[i]).append(" ");
        }
        String message = messageBuilder.toString().trim();

        // Format the fake message with sender's name
        Component formattedMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + target.getName() + suffix + "&f: " + message);

        // Broadcast the message to the entire server
        Bukkit.broadcast(formattedMessage);

        // Notify the sender
        player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Message sent as " + target.getName() + ": " + message)));

        return;
    }

    @Override
    public java.util.Collection<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        return args.length <= 1 ? org.bcnlab.beaconlabscore.commands.CommandCompletion.players(
                org.bcnlab.beaconlabscore.commands.CommandCompletion.argument(args, 0)) : java.util.List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlab.core.csudo");
    }
}
