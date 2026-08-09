package org.bcnlab.beaconlabscore.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NametagGenerator implements Listener {

    private final LuckPerms luckPerms;
    private final Scoreboard scoreboard;

    public NametagGenerator() {
        this.luckPerms = LuckPermsProvider.get();
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updatePlayerNametag(player);
    }

    public void updatePlayerNametag(Player player) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            return;
        }

        ContextManager contextManager = luckPerms.getContextManager();
        QueryOptions queryOptions = contextManager.getQueryOptions(user).orElse(QueryOptions.defaultContextualOptions());
        CachedMetaData metaData = user.getCachedData().getMetaData(queryOptions);

        String prefix = metaData.getPrefix() != null ? metaData.getPrefix() : "";
        String suffix = metaData.getSuffix() != null ? metaData.getSuffix() : "";
        String displayName = player.getName();
        
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
                            prefix = group.getCachedData().getMetaData(queryOptions).getPrefix();
                            suffix = group.getCachedData().getMetaData(queryOptions).getSuffix();
                        }
                    }
                }
            } catch (Exception e) {
                // Ignore
            }
        }
        
        if (prefix == null) prefix = "";
        if (suffix == null) suffix = "";

        // Remove old team if it exists
        Team oldTeam = scoreboard.getEntryTeam(player.getName());
        if (oldTeam != null) {
            oldTeam.removeEntry(player.getName());
        }

        // Generate a valid team name
        String teamName = generateTeamName(player.getUniqueId().toString());

        // Create a new team or get an existing one
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        // Use Adventure API for prefix/suffix (Component-based)
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
        
        boolean isLegacy = false;
        if (player.hasMetadata("protocol_version") && player.getMetadata("protocol_version").get(0).asInt() <= 47) {
            isLegacy = true;
        }
        
        if (isLegacy) {
            String legacyPrefixStr = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.builder().character('§').build().serialize(prefixComponent);
            if (legacyPrefixStr.length() > 16) legacyPrefixStr = legacyPrefixStr.substring(0, 16);
            prefixComponent = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.builder().character('§').build().deserialize(legacyPrefixStr);
            
            String legacySuffixStr = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.builder().character('§').build().serialize(suffixComponent);
            if (legacySuffixStr.length() > 16) legacySuffixStr = legacySuffixStr.substring(0, 16);
            suffixComponent = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.builder().character('§').build().deserialize(legacySuffixStr);
        }

        team.prefix(prefixComponent);
        team.suffix(suffixComponent);
        team.addEntry(player.getName());

        // Set display name with colors using Adventure
        player.displayName(prefixComponent.append(Component.text(displayName)).append(suffixComponent));
    }

    private String generateTeamName(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < 8; i++) { // Only take the first 8 bytes for a 16-character hex string
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
