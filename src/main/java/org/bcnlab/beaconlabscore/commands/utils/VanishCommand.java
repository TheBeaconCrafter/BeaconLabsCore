package org.bcnlab.beaconlabscore.commands.utils;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;

public class VanishCommand implements io.papermc.paper.command.brigadier.BasicCommand {

    private final BeaconLabsCore plugin;
    private final Set<Player> vanishedPlayers;

    public VanishCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
        this.vanishedPlayers = new HashSet<>();
    }

    @Override
    public void execute(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>This command can only be used by players.")));
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("beaconlabs.core.vanish.self")) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>You do not have permission to use this command.")));
            return;
        }

        if (vanishedPlayers.contains(player)) {
            unVanish(player);
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>You are no longer vanished.")));
        } else {
            vanish(player);
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>You are now vanished.")));
        }

        return;
    }

    @Override
    public java.util.Collection<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        return java.util.List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.vanish.self");
    }

    private void vanish(Player player) {
        vanishedPlayers.add(player);

        // Hide player from all online players who do not have beaconlabs.core.vanish.see permission
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.hasPermission("beaconlabs.core.vanish.see")) {
                onlinePlayer.hidePlayer(player);
            }
        }
    }

    private void unVanish(Player player) {
        vanishedPlayers.remove(player);

        // Show player to all online players
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showPlayer(player);
        }
    }
}
