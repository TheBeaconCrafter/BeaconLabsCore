package org.bcnlab.beaconlabscore.commands.player;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand implements io.papermc.paper.command.brigadier.BasicCommand {

    private final BeaconLabsCore plugin;

    public GamemodeCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>This command can only be used by players.")));
            return;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Usage: /gamemode <mode> [player] [nonotify]")));
            return;
        }

        GameMode gameMode;
        try {
            gameMode = parseGameMode(args[0]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Invalid game mode: " + args[0])));
            return;
        }

        if (args.length == 1) {
            if (!sender.hasPermission("beaconlabs.core.gamemode.self")) {
                sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>You do not have permission to change your game mode.")));
                return;
            }

            setGameMode(player, gameMode);
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize(
                    "<gray>Your game mode has been changed to <gold>" + gameMode.toString().toLowerCase()
                            + "<gray>.")));
        } else if (args.length >= 2) {
            boolean notify = true;
            String playerName = args[1];

            if (args.length > 3) {
                sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage()
                        .deserialize("<gray>Usage: /gamemode <mode> [player] [nonotify]")));
                return;
            }
            if (args.length > 2 && (args[2].equalsIgnoreCase("nonotify")
                    || args[2].equalsIgnoreCase("n"))) {
                notify = false;
            }

            if (!sender.hasPermission("beaconlabs.core.gamemode.others")) {
                sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>You do not have permission to change other players' game modes.")));
                return;
            }

            Player target = Bukkit.getPlayerExact(playerName);
            if (target == null) target = Bukkit.getPlayer(playerName);
            if (target == null) {
                sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Player not found: " + playerName + ".")));
                return;
            }

            setGameMode(target, gameMode);

            if (notify) {
                sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize(
                        "<gray>Changed game mode of " + target.getName() + " to <gold>"
                                + gameMode.toString().toLowerCase() + "<gray>.")));
                target.sendMessage(plugin.getPrefix(target).append(MiniMessage.miniMessage().deserialize(
                        "<gray>Your game mode has been changed to <gold>" + gameMode.toString().toLowerCase()
                                + "<gray> by " + player.getName() + ".")));
            } else {
                sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize(
                        "<gray>Changed game mode of " + target.getName() + " to <gold>"
                                + gameMode.toString().toLowerCase() + "<gray> without notification.")));
            }
        }

        return;
    }

    private GameMode parseGameMode(String mode) {
        switch (mode.toLowerCase()) {
            case "0":
            case "survival":
            case "s":
                return GameMode.SURVIVAL;
            case "1":
            case "creative":
            case "c":
                return GameMode.CREATIVE;
            case "2":
            case "adventure":
            case "a":
                return GameMode.ADVENTURE;
            case "3":
            case "spectator":
            case "spec":
                return GameMode.SPECTATOR;
            default:
                throw new IllegalArgumentException("Invalid game mode: " + mode);
        }
    }

    private void setGameMode(Player player, GameMode gameMode) {
        player.setGameMode(gameMode);
    }

    @Override
    public java.util.Collection<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        if (args.length <= 1) {
            return org.bcnlab.beaconlabscore.commands.CommandCompletion.filter(
                    java.util.List.of("survival", "creative", "adventure", "spectator", "0", "1", "2", "3"),
                    org.bcnlab.beaconlabscore.commands.CommandCompletion.argument(args, 0));
        }
        if (args.length == 2) {
            return org.bcnlab.beaconlabscore.commands.CommandCompletion.players(
                    org.bcnlab.beaconlabscore.commands.CommandCompletion.argument(args, 1));
        }
        if (args.length == 3) {
            return org.bcnlab.beaconlabscore.commands.CommandCompletion.filter(
                    java.util.List.of("notify", "nonotify", "n"),
                    org.bcnlab.beaconlabscore.commands.CommandCompletion.argument(args, 2));
        }
        return java.util.List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.gamemode.self")
                || sender.hasPermission("beaconlabs.core.gamemode.others");
    }
}
