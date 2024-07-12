package org.bcnlab.beaconlabscore;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import net.luckperms.api.query.QueryOptions;
import net.luckperms.api.query.dataorder.DataQueryOrder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatFormatter {

    private final BeaconLabsCore plugin;
    private final LuckPerms luckPerms;

    public ChatFormatter(BeaconLabsCore plugin, LuckPerms luckPerms) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
    }

    public String formatChat(Player player, String message) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());

        if (user == null) {
            return message;
        }

        String prefix = user.getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getPrefix();
        String suffix = user.getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getSuffix();

        if (prefix == null) prefix = "";
        if (suffix == null) suffix = "";

        if(!player.hasPermission("beaconlabs.core.coloredchat")) {
            if (prefix == null) prefix = "";
            if (suffix == null) suffix = "";

            // Translate colors for prefix and suffix only
            prefix = ChatColor.translateAlternateColorCodes('&', prefix);
            suffix = ChatColor.translateAlternateColorCodes('&', suffix);

            return prefix + player.getName() + suffix + ChatColor.WHITE + ": " + message;
        } else {
            return ChatColor.translateAlternateColorCodes('&', prefix + player.getName() + suffix + ChatColor.WHITE + ": " + message);
        }
    }

    public void onPlayerChat(Player player, String message) {
        String formattedMessage = formatChat(player, message);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(formattedMessage);
        }
        Bukkit.getConsoleSender().sendMessage(formattedMessage);
    }
}
