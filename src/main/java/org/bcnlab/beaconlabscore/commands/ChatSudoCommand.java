package org.bcnlab.beaconlabscore.commands;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatSudoCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;
    private final LuckPerms luckPerms;

    public ChatSudoCommand(BeaconLabsCore plugin, LuckPerms luckPerms) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the command sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        // Check permission
        if (!player.hasPermission("beaconlab.core.csudo")) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        // Validate command usage
        if (args.length < 2) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /csudo <player> <message>");
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        // Check if the target player is online
        if (target == null || !target.isOnline()) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Player '" + targetName + "' is not online.");
            return true;
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
        String formattedMessage = ChatColor.translateAlternateColorCodes('&', prefix + target.getName() + suffix + ChatColor.WHITE + ": " + message);

        // Broadcast the message to the entire server
        Bukkit.broadcastMessage(formattedMessage);

        // Notify the sender
        player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Message sent as " + target.getName() + ": " + message);

        return true;
    }
}
