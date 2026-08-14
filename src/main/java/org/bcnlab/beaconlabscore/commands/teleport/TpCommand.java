package org.bcnlab.beaconlabscore.commands.teleport;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Teleports entities to other entities or to absolute/relative coordinates.
 *
 * <ul>
 *     <li>{@code /tp <player>}</li>
 *     <li>{@code /tp <target> <destination>}</li>
 *     <li>{@code /tp <x> <y> <z>}</li>
 *     <li>{@code /tp <target> <x> <y> <z>}</li>
 * </ul>
 */
public class TpCommand implements io.papermc.paper.command.brigadier.BasicCommand {

    private final BeaconLabsCore plugin;

    public TpCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage()
                    .deserialize("<gray>Only players can use this command!")));
            return;
        }

        if (!player.hasPermission("beaconlabs.core.tp")) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage()
                    .deserialize("<gray>You do not have permission to use this command.")));
            return;
        }

        if (args.length == 1) {
            List<Entity> destinations = EntityTargetResolver.resolve(args[0], player);
            if (destinations.size() != 1) {
                sendUsage(player, destinations.isEmpty()
                    ? "No entity matched '" + args[0] + "'."
                    : "The destination selector must match exactly one entity.");
                return;
            }
            Entity destination = destinations.get(0);
            player.teleport(destination.getLocation());
            send(player, "<gray>Teleported to " + destination.getName() + ".");
            return;
        }

        if (args.length == 2) {
            List<Entity> targets = EntityTargetResolver.resolve(args[0], player);
            List<Entity> destinations = EntityTargetResolver.resolve(args[1], player);
            if (targets.isEmpty()) {
                sendUsage(player, "No entities matched '" + args[0] + "'.");
                return;
            }
            if (destinations.size() != 1) {
                sendUsage(player, destinations.isEmpty()
                        ? "No destination entity matched '" + args[1] + "'."
                        : "The destination selector must match exactly one entity.");
                return;
            }

            Location destination = destinations.get(0).getLocation();
            for (Entity target : targets) {
                target.teleport(destination);
            }
            send(player, "<gray>Teleported " + targets.size() + " entit"
                    + (targets.size() == 1 ? "y" : "ies") + " to " + destinations.get(0).getName() + ".");
            return;
        }

        if (args.length == 3) {
            Location destination = coordinates(player.getLocation(), args, 0);
            if (destination == null) {
                sendUsage(player, "Coordinates must be numbers or relative values such as ~ and ~5.");
                return;
            }
            player.teleport(destination);
            send(player, "<gray>Teleported you to (" + format(destination.getX()) + ", "
                    + format(destination.getY()) + ", " + format(destination.getZ()) + ").");
            return;
        }

        if (args.length == 4) {
            List<Entity> targets = EntityTargetResolver.resolve(args[0], player);
            if (targets.isEmpty()) {
                sendUsage(player, "No entities matched '" + args[0] + "'.");
                return;
            }

            for (Entity target : targets) {
                Location destination = coordinates(target.getLocation(), args, 1);
                if (destination == null) {
                    sendUsage(player, "Coordinates must be numbers or relative values such as ~ and ~5.");
                    return;
                }
                target.teleport(destination);
            }
            send(player, "<gray>Teleported " + targets.size() + " entit"
                    + (targets.size() == 1 ? "y" : "ies") + " to the specified coordinates.");
            return;
        }

        sendUsage(player, "Usage: /tp <destination> | /tp <targets> <destination> | /tp <x> <y> <z> | /tp <targets> <x> <y> <z>");
    }

    private Location coordinates(Location base, String[] args, int offset) {
        Double x = parseCoordinate(args[offset], base.getX());
        Double y = parseCoordinate(args[offset + 1], base.getY());
        Double z = parseCoordinate(args[offset + 2], base.getZ());
        if (x == null || y == null || z == null) return null;

        Location result = base.clone();
        result.setX(x);
        result.setY(y);
        result.setZ(z);
        return result;
    }

    private Double parseCoordinate(String value, double current) {
        try {
            if (value.startsWith("~")) {
                String offset = value.substring(1);
                return current + (offset.isEmpty() ? 0.0 : Double.parseDouble(offset));
            }
            return Double.parseDouble(value);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private void sendUsage(Player player, String message) {
        send(player, "<gray>" + message);
    }

    private void send(Player player, String message) {
        player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize(message)));
    }

    private String format(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    @Override
    public List<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (!(sender instanceof Player player) || !sender.hasPermission("beaconlabs.core.tp")) {
            return List.of();
        }

        String partial = org.bcnlab.beaconlabscore.commands.CommandCompletion
                .argument(args, args.length - 1).toLowerCase(Locale.ROOT);
        List<String> suggestions = new ArrayList<>();
        if (args.length <= 1) {
            suggestions.addAll(onlinePlayerNames());
            suggestions.addAll(EntityTargetResolver.selectorSuggestions(partial, player));
            suggestions.addAll(coordinateValues(player.getLocation(), 0));
        } else if (args.length == 2) {
            List<Entity> targets = EntityTargetResolver.resolve(args[0], player);
            if (!targets.isEmpty()) {
                // The second argument may be a destination player or the target's X coordinate.
                suggestions.addAll(onlinePlayerNames());
                suggestions.addAll(EntityTargetResolver.selectorSuggestions(partial, player));
                suggestions.addAll(coordinateValues(targets.get(0).getLocation(), 0));
            } else {
                suggestions.addAll(coordinateValues(player.getLocation(), 1));
            }
        } else if (args.length == 3) {
            List<Entity> targets = EntityTargetResolver.resolve(args[0], player);
            Location base = targets.isEmpty() ? player.getLocation() : targets.get(0).getLocation();
            suggestions.addAll(coordinateValues(base, targets.isEmpty() ? 2 : 1));
        } else if (args.length == 4) {
            List<Entity> targets = EntityTargetResolver.resolve(args[0], player);
            if (!targets.isEmpty()) {
                suggestions.addAll(coordinateValues(targets.get(0).getLocation(), 2));
            }
        }

        return suggestions.stream()
                .distinct()
                .filter(value -> value.toLowerCase(Locale.ROOT).startsWith(partial))
                .toList();
    }

    private List<String> onlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }

    private List<String> coordinateValues(Location location, int axis) {
        double coordinate = axis == 0 ? location.getX() : axis == 1 ? location.getY() : location.getZ();
        return List.of("~", "~0", format(coordinate), String.valueOf((int) Math.floor(coordinate)));
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.tp");
    }
}
