package org.bcnlab.beaconlabscore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class WorkbenchCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public WorkbenchCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (!sender.hasPermission("beaconlabs.core.workbench")) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        player.openWorkbench(null, true);
        return true;
    }
}
