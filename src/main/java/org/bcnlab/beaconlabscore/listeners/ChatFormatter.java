package org.bcnlab.beaconlabscore.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChatFormatter {

    private final BeaconLabsCore plugin;
    private final LuckPerms luckPerms;

    public ChatFormatter(BeaconLabsCore plugin, LuckPerms luckPerms) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
    }

    public Component formatChat(Player player, String message) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());

        if (user == null) {
            return Component.text(player.getName() + ": " + message);
        }

        String prefix = null;
        String suffix = null;
        String displayName = player.getName(); // Use real name to avoid Bukkit prefix bug

        org.bukkit.plugin.Plugin linkPlugin = Bukkit.getPluginManager().getPlugin("BeaconLabsVelocityLink");
        if (linkPlugin != null) {
            try {
                Object vs = linkPlugin.getClass().getMethod("getVisualStateService").invoke(linkPlugin);
                if (vs != null) {
                    Boolean isNicked = (Boolean) vs.getClass().getMethod("isNicked", Player.class).invoke(vs, player);
                    if (isNicked != null && isNicked) {
                        displayName = (String) vs.getClass().getMethod("getNickname", Player.class).invoke(vs, player);
                        String fakeRank = (String) vs.getClass().getMethod("getFakeRank", Player.class).invoke(vs, player);
                        if (fakeRank == null || fakeRank.isEmpty()) {
                            fakeRank = "default";
                        }
                        net.luckperms.api.model.group.Group group = luckPerms.getGroupManager().getGroup(fakeRank.toLowerCase());
                        if (group != null) {
                            prefix = group.getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getPrefix();
                            suffix = group.getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getSuffix();
                        }
                    }
                }
            } catch (Exception e) {
                // Ignore
            }
        }

        if (prefix == null && suffix == null) {
            prefix = user.getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getPrefix();
            suffix = user.getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getSuffix();
        }

        if (prefix == null) prefix = "";
        if (suffix == null) suffix = "";

        Component prefixComponent = Component.empty();
        if (prefix != null && !prefix.isEmpty()) {
            if (prefix.contains("&") || prefix.contains("§")) {
                prefixComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(prefix.replace("§", "&"));
            } else {
                prefixComponent = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(prefix);
            }
        }
        
        Component suffixComponent = Component.empty();
        if (suffix != null && !suffix.isEmpty()) {
            if (suffix.contains("&") || suffix.contains("§")) {
                suffixComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(suffix.replace("§", "&"));
            } else {
                suffixComponent = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(suffix);
            }
        }

        Component msgComponent;
        if (!player.hasPermission("beaconlabs.core.coloredchat")) {
            msgComponent = Component.text(message);
        } else {
            if (message.contains("&") || message.contains("§")) {
                msgComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(message.replace("§", "&"));
            } else {
                msgComponent = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(message);
            }
        }
        
        return Component.empty().append(prefixComponent).append(Component.text(displayName)).append(suffixComponent).append(Component.text(": ")).append(msgComponent);
    }

    public void onPlayerChat(Player player, String message) {
        Component formattedMessage = formatChat(player, message);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(formattedMessage);
        }
        Bukkit.getConsoleSender().sendMessage(formattedMessage);
    }
}
