package org.bcnlab.beaconlabscore.commands.time;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public class TimeCommand implements io.papermc.paper.command.brigadier.BasicCommand {

    private final BeaconLabsCore plugin;

    public TimeCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (!(sender instanceof Player)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>Only players can use this command."));
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("beaconlabs.core.time")) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>You do not have permission to change the time.")));
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Usage: /time <day|night|noon|midnight|number>")));
            return;
        }

        World world = player.getWorld();
        String timeArg = args[0].toLowerCase(Locale.ROOT);

        switch (timeArg) {
            case "day":
                world.setTime(0);
                player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Set time to day.")));
                break;
            case "night":
                world.setTime(13000);
                player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Set time to night.")));
                break;
            case "noon":
                world.setTime(6000);
                player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Set time to noon.")));
                break;
            case "midnight":
                world.setTime(18000);
                player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Set time to midnight.")));
                break;
            default:
                try {
                    long timeValue = Math.floorMod(Long.parseLong(timeArg), 24000L);
                    world.setTime(timeValue);
                    player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Set time to " + timeValue + ".")));
                } catch (NumberFormatException e) {
                    player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Invalid time value or keyword. Use: day, night, noon, midnight, or a number.")));
                }
                break;
        }

        return;
    }

    @Override
    public List<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (!(sender instanceof Player player) || !player.hasPermission("beaconlabs.core.time")) {
            return List.of();
        }
        if (args.length > 1) return List.of();

        String partial = org.bcnlab.beaconlabscore.commands.CommandCompletion
                .argument(args, 0).toLowerCase(Locale.ROOT);
        return List.of("day", "night", "noon", "midnight", "0", "6000", "12000", "13000", "18000")
                .stream()
                .filter(value -> value.startsWith(partial))
                .toList();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.time");
    }
}
