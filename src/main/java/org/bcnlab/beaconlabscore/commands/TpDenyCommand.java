package org.bcnlab.beaconlabscore.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bcnlab.beaconlabscore.commands.TpaCommand.tpaRequests;

public class TpDenyCommand implements CommandExecutor {

    private final String pluginPrefix;

    public TpDenyCommand(String pluginPrefix) {
        this.pluginPrefix = ChatColor.translateAlternateColorCodes('&', pluginPrefix);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(pluginPrefix + ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player target = (Player) sender;

        String requesterName = tpaRequests.get(target.getName());
        if (requesterName == null) {
            sender.sendMessage(pluginPrefix + ChatColor.RED + "You have no pending teleport requests.");
            return true;
        }

        Player requester = target.getServer().getPlayer(requesterName);
        if (requester != null) {
            requester.sendMessage(pluginPrefix + ChatColor.RED + "Your teleport request to " + target.getName() + " was denied.");
        }

        target.sendMessage(pluginPrefix + ChatColor.GREEN + "You have denied the teleport request from " + requesterName + ".");
        tpaRequests.remove(target.getName());
        return true;
    }
}
