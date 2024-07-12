package org.bcnlab.beaconlabscore.commands;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public HealCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0) {
            if (!sender.hasPermission("beaconlabs.core.heal.others")) {
                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "You do not have permission to heal others.");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Player not found: " + args[0]);
                return true;
            }

            boolean notify = true;
            if (args.length > 1 && args[args.length - 1].equalsIgnoreCase("nonotify")) {
                notify = false;
            }

            healPlayer(target, notify);

            if(notify) {
                sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "You healed " + target.getName() + ".");
            } else {
                sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "You healed " + target.getName() + " without notifying them.");
            }

            if (notify) {
                target.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "You have been healed by " + player.getName() + ".");
            }
        } else {
            if (!sender.hasPermission("beaconlabs.core.heal.self")) {
                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "You do not have permission to heal yourself.");
                return true;
            }

            healPlayer(player, true);
            player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "You healed yourself.");
        }

        return true;
    }

    private void healPlayer(Player player, boolean notify) {
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setFireTicks(0);

        if (notify) {
            player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "You have been healed.");
        }
    }
}
