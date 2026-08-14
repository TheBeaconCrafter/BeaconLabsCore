package org.bcnlab.beaconlabscore.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathMessages implements Listener {

    private final BeaconLabsCore plugin;

    public DeathMessages(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        plugin.recordDeathLocation(player);

        if (!plugin.areDeathMessagesEnabled()) {
            event.deathMessage(null);
            return;
        }

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
                // Otherwise, use the cause of death. A death event may not always
                // have a recorded damage cause (for example, plugin-triggered deaths).
                String message = plugin.getCustomDeathMessage();
                EntityDamageEvent cause = player.getLastDamageCause();
                String causeName = cause == null ? "unknown" : cause.getCause().name();
                deathMessage = formatMessage(message, player.getName(), causeName);
            }
        }

        event.deathMessage(LegacyComponentSerializer.legacySection().deserialize(deathMessage));
    }

    private String formatMessage(String message, String playerName, String other) {
        // Replace {player} and {other} placeholders with actual names
        message = message.replace("{player}", playerName);
        message = message.replace("{other}", other);
        return LegacyComponentSerializer.legacySection().serialize(
                LegacyComponentSerializer.legacyAmpersand().deserialize(message));
    }
}
