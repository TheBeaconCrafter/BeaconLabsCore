package org.bcnlab.beaconlabscore.listeners;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/** Provides BeaconLabsCore's custom response for unknown player commands. */
public class UnknownCommandListener implements Listener {

    private final BeaconLabsCore plugin;

    public UnknownCommandListener(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        if (message == null || message.length() < 2 || message.charAt(0) != '/') return;

        String commandLabel = message.substring(1).trim().split("\\s+", 2)[0];
        if (commandLabel.isEmpty()) return;

        String lookupLabel = "/" + commandLabel.toLowerCase(java.util.Locale.ROOT);
        if (Bukkit.getServer().getHelpMap().getHelpTopic(lookupLabel) != null) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize(
                "<gray>The command <dark_gray>[<gold>" + message
                        + "<dark_gray>]<gray> doesn't exist!")));
    }
}
