package org.bcnlab.beaconlabscore.commands.weather;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.World;

public class WeatherCommand implements io.papermc.paper.command.brigadier.BasicCommand {

    private final BeaconLabsCore plugin;

    public WeatherCommand(BeaconLabsCore plugin) {
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

        if (!player.hasPermission("beaconlabs.core.weather")) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>You do not have permission to change the weather.")));
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Usage: /weather <clear|rain|storm>")));
            return;
        }

        World world = player.getWorld();
        String weatherType = args[0].toLowerCase();

        switch (weatherType) {
            case "clear":
                world.setStorm(false);
                world.setThundering(false);
                player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Set weather to clear.")));
                break;
            case "rain":
                world.setStorm(true);
                world.setThundering(false);
                player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Set weather to rain.")));
                break;
            case "storm":
                world.setStorm(true);
                world.setThundering(true);
                player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Set weather to thunderstorm.")));
                break;
            default:
                player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Invalid weather type. Use: clear, rain, storm")));
                break;
        }

        return;
    }

    @Override
    public java.util.Collection<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        return args.length <= 1 ? org.bcnlab.beaconlabscore.commands.CommandCompletion.filter(
                java.util.List.of("clear", "rain", "storm"),
                org.bcnlab.beaconlabscore.commands.CommandCompletion.argument(args, 0)) : java.util.List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.weather");
    }
}
