package org.bcnlab.beaconlabscore.commands.time;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimeCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public TimeCommand(BeaconLabsCore plugin) {
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

        if (args.length == 0) {
            sender.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<red>Usage: /time <day|night|noon|midnight|number>")));
            return true;
        }

        World world = player.getWorld();
        String timeArg = args[0].toLowerCase();

        switch (timeArg) {
            case "day":
                world.setTime(0);
                player.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<green>Set time to day.")));
                break;
            case "night":
                world.setTime(13000);
                player.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<green>Set time to night.")));
                break;
            case "noon":
                world.setTime(6000);
                player.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<green>Set time to noon.")));
                break;
            case "midnight":
                world.setTime(18000);
                player.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<green>Set time to midnight.")));
                break;
            default:
                try {
                    long timeValue = Long.parseLong(timeArg);
                    world.setTime(timeValue);
                    player.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<green>Set time to " + timeValue + ".")));
                } catch (NumberFormatException e) {
                    player.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<red>Invalid time value or keyword. Use: day, night, noon, midnight, or a number.")));
                }
                break;
        }

        return true;
    }
}
