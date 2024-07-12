package org.bcnlab.beaconlabscore.listeners;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class UnknownCommandListener implements Listener {

    private final BeaconLabsCore plugin;

    public UnknownCommandListener(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String msg = e.getMessage();
        String[] args = msg.split(" ");
        Player p = e.getPlayer();

        if (Bukkit.getServer().getHelpMap().getHelpTopic(args[0]) == null) {
            e.setCancelled(true);

            p.sendMessage(plugin.getPrefix() + "§cThe command §8[§a" + msg + "§8]§c doesn't exist!");
        }
    }

}