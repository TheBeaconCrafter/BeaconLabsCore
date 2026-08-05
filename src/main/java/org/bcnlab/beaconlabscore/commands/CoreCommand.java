package org.bcnlab.beaconlabscore.commands;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoreCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public CoreCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<red>This command can only be used by players.")));
            return true;
        }

        if (!sender.hasPermission("beaconlabs.core.info")) {
            sender.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<red>You do not have permission to use this command.")));
            return true;
        }

        Player player = (Player) sender;
        player.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<red>BeaconLabsCore Version <gold>" + plugin.getVersion() + "<red> by ItsBeacon")));
        return true;
    }
}
