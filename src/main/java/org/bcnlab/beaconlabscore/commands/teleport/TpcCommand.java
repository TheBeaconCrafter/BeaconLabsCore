package org.bcnlab.beaconlabscore.commands.teleport;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TpcCommand implements io.papermc.paper.command.brigadier.BasicCommand {

    private final BeaconLabsCore plugin;

    public TpcCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
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

        if (args.length < 3) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Usage: /tpc <player> <x> <y> <z> | /tpc <x> <y> <z>")));
            return;
        }

        if (args.length == 4) {
            // Teleport selected entities to coordinates
            List<Entity> targets = EntityTargetResolver.resolve(args[0], player);
            if (targets.isEmpty()) {
                sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage()
                        .deserialize("<gray>No entities matched '" + args[0] + "'.")));
                return;
            }

            double x, y, z;
            try {
                x = Double.parseDouble(args[1]);
                y = Double.parseDouble(args[2]);
                z = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Coordinates must be numbers.")));
                return;
            }

            for (Entity target : targets) {
                target.teleport(new org.bukkit.Location(target.getWorld(), x, y, z));
            }
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage()
                    .deserialize("<gold>Teleported " + targets.size() + " entit"
                            + (targets.size() == 1 ? "y" : "ies") + " to (" + x + ", " + y + ", " + z + ").")));

        } else if (args.length == 3) {
            // Teleport the sender to coordinates
            double x, y, z;
            try {
                x = Double.parseDouble(args[0]);
                y = Double.parseDouble(args[1]);
                z = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Coordinates must be numbers.")));
                return;
            }

            player.teleport(new org.bukkit.Location(player.getWorld(), x, y, z));
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gold>Teleported you to (" + x + ", " + y + ", " + z + ").")));

        } else {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Usage: /tpc <player> <x> <y> <z> | /tpc <x> <y> <z>")));
            return;
        }

        return;
    }

    @Override
    public List<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (!(sender instanceof Player player) || !player.hasPermission("beaconlabs.core.tp")) {
            return List.of();
        }

        List<String> suggestions = new ArrayList<>();
        if (args.length <= 1) {
            suggestions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
            suggestions.addAll(EntityTargetResolver.selectorSuggestions(
                    org.bcnlab.beaconlabscore.commands.CommandCompletion.argument(args, 0), player));
            suggestions.addAll(coordinateValues(player.getLocation().getX()));
        } else if (args.length == 2) {
            List<Entity> targets = EntityTargetResolver.resolve(args[0], player);
            Entity target = targets.isEmpty() ? null : targets.get(0);
            suggestions.addAll(coordinateValues(target == null ? player.getLocation().getY() : target.getLocation().getX()));
        } else if (args.length == 3) {
            List<Entity> targets = EntityTargetResolver.resolve(args[0], player);
            Entity target = targets.isEmpty() ? null : targets.get(0);
            suggestions.addAll(coordinateValues(target == null ? player.getLocation().getZ() : target.getLocation().getY()));
        } else if (args.length == 4) {
            List<Entity> targets = EntityTargetResolver.resolve(args[0], player);
            if (!targets.isEmpty()) {
                suggestions.addAll(coordinateValues(targets.get(0).getLocation().getZ()));
            }
        }

        String partial = org.bcnlab.beaconlabscore.commands.CommandCompletion
                .argument(args, args.length - 1).toLowerCase(Locale.ROOT);
        return suggestions.stream()
                .distinct()
                .filter(value -> value.toLowerCase(Locale.ROOT).startsWith(partial))
                .toList();
    }

    private List<String> coordinateValues(double coordinate) {
        return List.of(String.format(Locale.ROOT, "%.2f", coordinate),
                String.valueOf((int) Math.floor(coordinate)));
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.tp");
    }
}
