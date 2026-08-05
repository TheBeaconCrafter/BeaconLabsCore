package org.bcnlab.beaconlabscore.commands.utils;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorkbenchCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public WorkbenchCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<red>This command can only be used by players.")));
            return true;
        }

        Player player = (Player) sender;

        if (!sender.hasPermission("beaconlabs.core.workbench")) {
            sender.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<red>You do not have permission to use this command.")));
            return true;
        }

        player.openWorkbench(null, true);
        return true;
    }
}
