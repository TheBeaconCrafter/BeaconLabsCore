package org.bcnlab.beaconlabscore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InvseeCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public InvseeCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (!sender.hasPermission("beaconlabs.core.invsee")) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /invsee <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Player not found: " + args[0]);
            return true;
        }

        Inventory targetInventory = target.getInventory();
        player.openInventory(targetInventory);

        sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "You are now viewing the inventory of " + target.getName() + ".");
        return true;
    }
}
