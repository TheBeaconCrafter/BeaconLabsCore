package org.bcnlab.beaconlabscore.commands.utils;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class TpsCommand implements io.papermc.paper.command.brigadier.BasicCommand {

    private final BeaconLabsCore plugin;

    public TpsCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("beaconlabs.core.stats")) {
                player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>You do not have permission to use this command.")));
                return;
            }
        }

        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();

        int totalEntities = 0;
        int livingEntities = 0;

        for (org.bukkit.World world : Bukkit.getWorlds()) { // Iterate over all worlds
            for (Entity entity : world.getEntities()) {
                totalEntities++;
                if (entity.getType().isAlive()) {
                    livingEntities++;
                }
            }
        }

        String message = String.format(
                "<gray>Memory Usage:\n<gold>Used: <gold>%.2f MB\n<gold>Free: <gold>%.2f MB\n<gold>Total: <gold>%.2f MB\n<gold>Max: <gold>%.2f MB\n" +
                        "<gray>Entities:\n<gold>Total Entities: <gold>%d\n<gold>Living Entities: <gold>%d",
                usedMemory / 1048576.0,
                freeMemory / 1048576.0,
                totalMemory / 1048576.0,
                maxMemory / 1048576.0,
                totalEntities,
                livingEntities
        );

        sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize(message)));
        return;
    }

    @Override
    public java.util.Collection<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        return java.util.List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.stats");
    }
}
