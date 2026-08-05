package org.bcnlab.beaconlabscore.listeners;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveMessages implements Listener {

    private final BeaconLabsCore plugin;

    public JoinLeaveMessages(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.areJoinMessagesEnabled()) {
            event.joinMessage(null); // Suppress join message if disabled
        } else {
            String message = formatMessage(plugin.getCustomJoinMessage(), event.getPlayer().getName());
            event.joinMessage(LegacyComponentSerializer.legacySection().deserialize(
                    LegacyComponentSerializer.legacySection().serialize(
                            LegacyComponentSerializer.legacyAmpersand().deserialize(message))));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!plugin.areLeaveMessagesEnabled()) {
            event.quitMessage(null); // Suppress leave message if disabled
        } else {
            String message = formatMessage(plugin.getCustomLeaveMessage(), event.getPlayer().getName());
            event.quitMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
        }
    }

    private String formatMessage(String message, String playerName) {
        // Replace {player} placeholder with the actual player name
        return message.replace("{player}", playerName);
    }
}
