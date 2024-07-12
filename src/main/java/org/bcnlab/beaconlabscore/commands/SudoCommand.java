package org.bcnlab.beaconlabscore.commands;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SudoCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public SudoCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the command sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        // Check permission
        if (!player.hasPermission("beaconlabs.core.sudo")) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        // Validate command usage
        if (args.length < 2) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /sudo <player> <command> [args...]");
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        // Check if the target player is online
        if (target == null || !target.isOnline()) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Player '" + targetName + "' is not online.");
            return true;
        }

        // Build the command to execute
        StringBuilder commandBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            commandBuilder.append(args[i]).append(" ");
        }
        String commandToExecute = commandBuilder.toString().trim();

        // Execute the command as the target player
        boolean commandExecuted = Bukkit.dispatchCommand(target, commandToExecute);

        if (commandExecuted) {
            player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Successfully executed command as " + target.getName() + ": /" + commandToExecute);
        } else {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Failed to execute command as " + target.getName() + ": /" + commandToExecute);
        }

        return true;
    }
}
