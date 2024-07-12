package org.bcnlab.beaconlabscore;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;

public class VanishCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;
    private final Set<Player> vanishedPlayers;

    public VanishCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
        this.vanishedPlayers = new HashSet<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("beaconlabs.core.vanish.self")) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (vanishedPlayers.contains(player)) {
            unVanish(player);
            player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "You are no longer vanished.");
        } else {
            vanish(player);
            player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "You are now vanished.");
        }

        return true;
    }

    private void vanish(Player player) {
        vanishedPlayers.add(player);

        // Hide player from all online players who do not have beaconlabs.core.vanish.see permission
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.hasPermission("beaconlabs.core.vanish.see")) {
                onlinePlayer.hidePlayer(player);
            }
        }
    }

    private void unVanish(Player player) {
        vanishedPlayers.remove(player);

        // Show player to all online players
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showPlayer(player);
        }
    }
}
