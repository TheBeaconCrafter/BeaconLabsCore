package org.bcnlab.beaconlabscore.commands;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ClearInventoryCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public ClearInventoryCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (sender.hasPermission("beaconlabs.core.clearinventory.self")) {
                    clearInventory(player);
                    player.sendMessage(plugin.getPrefix() + "§cYour inventory has been cleared.");
                } else {
                    player.sendMessage(plugin.getPrefix() + "§cYou are not allowed to clear your inventory.");
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + "§cOnly players can clear their own inventory.");
            }
            return true;
        } else if (args.length == 1) {
            if (sender.hasPermission("beaconlabs.core.clearinventory.others")) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    clearInventory(target);
                    sender.sendMessage(plugin.getPrefix() + "§cCleared the inventory of " + target.getName() + ".");
                    target.sendMessage(plugin.getPrefix() + "§cYour inventory has been cleared by an admin.");
                } else {
                    sender.sendMessage(plugin.getPrefix() + "§cPlayer not found.");
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + "§cYou do not have permission to clear other players' inventories.");
            }
            return true;
        } else {
            sender.sendMessage(plugin.getPrefix() + "§cUsage: /clearinv [player]");
            return false;
        }
    }

    private void clearInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }
}
