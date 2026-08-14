package org.bcnlab.beaconlabscore.commands.teleport;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpHereCommand implements io.papermc.paper.command.brigadier.BasicCommand {

    private final BeaconLabsCore plugin;

    public TpHereCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        // Check if the command sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Only players can use this command!")));
            return;
        }

        Player player = (Player) sender;

        // Check permission
        if (!player.hasPermission("beaconlabs.core.tp")) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>You do not have permission to use this command.")));
            return;
        }

        // Validate command usage
        if (args.length != 1) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Usage: /tphere <player|selector>")));
            return;
        }

        java.util.List<org.bukkit.entity.Entity> targets = EntityTargetResolver.resolve(args[0], player);
        if (targets.isEmpty()) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage()
                    .deserialize("<gray>No entities matched '" + args[0] + "'.")));
            return;
        }

        // Teleport every selected target to the sender's location.
        for (org.bukkit.entity.Entity target : targets) {
            target.teleport(player.getLocation());
        }
        player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage()
                .deserialize("<gray>Teleported " + targets.size() + " entit"
                        + (targets.size() == 1 ? "y" : "ies") + " to your location.")));

        return;
    }

    @Override
    public java.util.Collection<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        return args.length <= 1
                ? java.util.stream.Stream.concat(
                        org.bcnlab.beaconlabscore.commands.CommandCompletion.players(
                                org.bcnlab.beaconlabscore.commands.CommandCompletion.argument(args, 0)).stream(),
                        EntityTargetResolver.selectorSuggestions(
                                org.bcnlab.beaconlabscore.commands.CommandCompletion.argument(args, 0)).stream())
                .filter(value -> value.toLowerCase(java.util.Locale.ROOT).startsWith(
                        org.bcnlab.beaconlabscore.commands.CommandCompletion.argument(args, 0)
                                .toLowerCase(java.util.Locale.ROOT)))
                .distinct()
                .toList()
                : java.util.List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.tp");
    }
}
