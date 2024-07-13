package org.bcnlab.beaconlabscore.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bcnlab.beaconlabscore.BeaconLabsCore;

public class BlockListener implements Listener {

    private final BeaconLabsCore plugin;

    public BlockListener(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getPlayer().hasPermission("beaconlabs.core.block.break")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.getPrefix() + ChatColor.RED + "You do not have permission to break blocks.");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getPlayer().hasPermission("beaconlabs.core.block.place")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.getPrefix() + ChatColor.RED + "You do not have permission to place blocks.");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block != null && block.getType() != Material.AIR) {
            if (!event.getPlayer().hasPermission("beaconlabs.core.block.interact")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(plugin.getPrefix() + ChatColor.RED + "You do not have permission to interact with blocks.");
            }
        }
    }
}
