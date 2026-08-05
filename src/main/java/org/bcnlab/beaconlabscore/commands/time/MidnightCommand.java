package org.bcnlab.beaconlabscore.commands.time;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MidnightCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public MidnightCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Only players can use this command."));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("beaconlabs.core.time")) {
            player.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<red>You do not have permission to change the time.")));
            return true;
        }

        if (args.length > 0) {
            sender.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<red>Usage: /midnight")));
            return true;
        }

        World world = player.getWorld();

        world.setTime(18000);
        player.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<green>Set time to midnight.")));

        return true;
    }
}
