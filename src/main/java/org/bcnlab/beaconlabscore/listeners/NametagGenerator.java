package org.bcnlab.beaconlabscore.listeners;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

        String prefix = metaData.getPrefix() != null ? ChatColor.translateAlternateColorCodes('&', metaData.getPrefix()) : "";
        String suffix = metaData.getSuffix() != null ? ChatColor.translateAlternateColorCodes('&', metaData.getSuffix()) : "";
        String colorName = ChatColor.translateAlternateColorCodes('&', prefix + player.getName() + suffix);

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

        team.setPrefix(prefix);
        team.setSuffix(suffix);
        team.addEntry(player.getName());
        player.setDisplayName(colorName);
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
