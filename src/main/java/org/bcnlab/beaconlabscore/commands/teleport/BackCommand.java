package org.bcnlab.beaconlabscore.commands.teleport;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public final class BackCommand implements BasicCommand {

    private final BeaconLabsCore plugin;

    public BackCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage()
                    .deserialize("<gray>This command can only be used by players.")));
            return;
        }

        if (args.length > 0) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage()
                    .deserialize("<gray>Usage: /back")));
            return;
        }

        Location deathLocation = plugin.getLastDeathLocation(player);
        if (deathLocation == null) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage()
                    .deserialize("<gray>You do not have a recorded death location.")));
            return;
        }

        player.teleport(deathLocation);
        player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage()
                .deserialize("<gray>Teleported you to your last death location.")));
    }

    @Override
    public Collection<String> suggest(CommandSourceStack stack, String[] args) {
        return List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.back");
    }
}
