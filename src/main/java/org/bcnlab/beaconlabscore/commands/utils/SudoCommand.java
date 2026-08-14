package org.bcnlab.beaconlabscore.commands.utils;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SudoCommand implements io.papermc.paper.command.brigadier.BasicCommand {

    private final BeaconLabsCore plugin;

    public SudoCommand(BeaconLabsCore plugin) {
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
        if (!player.hasPermission("beaconlabs.core.sudo")) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>You do not have permission to use this command.")));
            return;
        }

        // Validate command usage
        if (args.length < 2) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Usage: /sudo <player> <command> [args...]")));
            return;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        // Check if the target player is online
        if (target == null || !target.isOnline()) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Player '" + targetName + "' is not online.")));
            return;
        }

        // Build the command to execute
        StringBuilder commandBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            commandBuilder.append(args[i]).append(" ");
        }
        String commandToExecute = commandBuilder.toString().trim();

        // Execute the command as the target player
        boolean commandExecuted = Bukkit.dispatchCommand(target, commandToExecute);

        if (commandExecuted) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Successfully executed command as " + target.getName() + ": /" + commandToExecute)));
        } else {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Failed to execute command as " + target.getName() + ": /" + commandToExecute)));
        }

        return;
    }

    @Override
    public java.util.Collection<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        return args.length <= 1 ? org.bcnlab.beaconlabscore.commands.CommandCompletion.players(
                org.bcnlab.beaconlabscore.commands.CommandCompletion.argument(args, 0)) : java.util.List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.sudo");
    }
}
