package org.bcnlab.beaconlabscore.commands.weather;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.World;

public class WeatherCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public WeatherCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>Only players can use this command."));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("beaconlabs.core.weather")) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>You do not have permission to change the weather.")));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Usage: /weather <clear|rain|storm>")));
            return true;
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

        return true;
    }
}
