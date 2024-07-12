package org.bcnlab.beaconlabscore.listeners;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathMessages implements Listener {

    private final BeaconLabsCore plugin;

    public DeathMessages(BeaconLabsCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!plugin.areDeathMessagesEnabled()) {
            event.setDeathMessage(null);
            return;
        }

        Player player = event.getEntity();
        String deathMessage = null;

        // Check if the death was caused by another entity
        if (player.getKiller() instanceof Player) {
            Player killer = player.getKiller();
            String message = plugin.getCustomDeathMessage();
            deathMessage = formatMessage(message, player.getName(), killer.getName());
        } else {
            // Check if the death was caused by an entity
            if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent lastDamageEvent = (EntityDamageByEntityEvent) player.getLastDamageCause();
                String message = plugin.getCustomDeathMessage();
                deathMessage = formatMessage(message, player.getName(), lastDamageEvent.getDamager().getName());
            } else {
                // Otherwise, use the cause of death
                String message = plugin.getCustomDeathMessage();
                deathMessage = formatMessage(message, player.getName(), player.getLastDamageCause().getCause().name());
            }
        }

        event.setDeathMessage(deathMessage);
    }

    private String formatMessage(String message, String playerName, String other) {
        // Replace {player} and {other} placeholders with actual names
        message = message.replace("{player}", playerName);
        message = message.replace("{other}", other);
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
