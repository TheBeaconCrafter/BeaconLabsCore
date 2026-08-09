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
                            prefix = group.getNodes(net.luckperms.api.node.NodeType.PREFIX).stream().map(net.luckperms.api.node.types.PrefixNode::getMetaValue).findFirst().orElse(null);
                            suffix = group.getNodes(net.luckperms.api.node.NodeType.SUFFIX).stream().map(net.luckperms.api.node.types.SuffixNode::getMetaValue).findFirst().orElse(null);
                            Bukkit.getLogger().info("ChatFormatter Debug: player=" + player.getName() + " fakeRank=" + fakeRank + " groupName=" + group.getName() + " prefix=" + prefix);
                        } else {
                            Bukkit.getLogger().info("ChatFormatter Debug: player=" + player.getName() + " fakeRank=" + fakeRank + " GROUP IS NULL");
                        }
                    } else {
                        try {
                            String originalName = (String) vs.getClass().getMethod("getOriginalName", Player.class).invoke(vs, player);
                            if (originalName != null) {
                                displayName = originalName;
                            }
                        } catch (Exception e) {}
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

        String nameStr = prefix + displayName + suffix;
        Component nameComponent;
        if (nameStr.contains("&") || nameStr.contains("§")) {
            nameComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(nameStr.replace("§", "&"));
        } else {
            nameComponent = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(nameStr);
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
        
        // Use a reset color for the colon and message so the name color doesn't bleed if it were serialized differently, 
        // but since we are appending them as separate components, they will be default color anyway.
        return Component.empty().append(nameComponent).append(Component.text(": ")).append(msgComponent);
    }

    public void onPlayerChat(Player player, String message) {
        Component formattedMessage = formatChat(player, message);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(formattedMessage);
        }
        Bukkit.getConsoleSender().sendMessage(formattedMessage);
    }
}
