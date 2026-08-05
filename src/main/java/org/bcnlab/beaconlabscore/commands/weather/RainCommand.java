package org.bcnlab.beaconlabscore.commands.weather;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.World;

public class RainCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public RainCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Only players can use this command."));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("beaconlabs.core.weather")) {
            player.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<red>You do not have permission to change the weather.")));
            return true;
        }

        if (args.length > 0) {
            sender.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<red>Usage: /rain")));
            return true;
        }

        World world = player.getWorld();

        world.setStorm(true);
        world.setThundering(false);
        player.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<green>Set weather to rain.")));

        return true;
    }
}
