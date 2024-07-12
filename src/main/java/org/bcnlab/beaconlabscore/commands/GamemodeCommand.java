package org.bcnlab.beaconlabscore.commands;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public GamemodeCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /gamemode <mode> [player] [nonotify]");
            return true;
        }

        GameMode gameMode;
        try {
            gameMode = parseGameMode(args[0]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Invalid game mode: " + args[0]);
            return true;
        }

        if (args.length == 1) {
            if (!sender.hasPermission("beaconlabs.core.gamemode.self")) {
                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "You do not have permission to change your game mode.");
                return true;
            }

            setGameMode(player, gameMode);
            sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Your game mode has been changed to " + gameMode.toString().toLowerCase() + ".");
        } else if (args.length >= 2) {
            boolean notify = true;
            String playerName = args[1];

            if (args.length > 2 && args[2].equalsIgnoreCase("nonotify")) {
                notify = false;
            }

            if (!sender.hasPermission("beaconlabs.core.gamemode.others")) {
                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "You do not have permission to change other players' game modes.");
                return true;
            }

            Player target = Bukkit.getPlayer(playerName);
            if (target == null) {
                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Player not found: " + playerName + ".");
                return true;
            }

            setGameMode(target, gameMode);

            if (notify) {
                sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Changed game mode of " + target.getName() + " to " + gameMode.toString().toLowerCase() + ".");
                target.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Your game mode has been changed to " + gameMode.toString().toLowerCase() + " by " + player.getName() + ".");
            } else {
                sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Changed game mode of " + target.getName() + " to " + gameMode.toString().toLowerCase() + " without notification.");
            }
        }

        return true;
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
}
