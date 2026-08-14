package org.bcnlab.beaconlabscore.commands.player;

import org.bukkit.Bukkit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements io.papermc.paper.command.brigadier.BasicCommand {

    private final Component pluginPrefix;

    public FlyCommand(String pluginPrefix) {
        this.pluginPrefix = LegacyComponentSerializer.legacyAmpersand().deserialize(pluginPrefix);
    }

    @Override
    public void execute(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (!(sender instanceof Player)) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>This command can only be used by players.")));
            return;
        }

        Player player = (Player) sender;

        if (args.length > 0) {
            if (!sender.hasPermission("beaconlabs.core.fly.others")) {
                sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>You do not have permission to fly others.")));
                return;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>Player not found: " + args[0])));
                return;
            }

            boolean notify = true;
            if (args.length > 1 && (args[args.length - 1].equalsIgnoreCase("nonotify") || args[args.length - 1].equalsIgnoreCase("n"))) {
                notify = false;
            }

            toggleFlight(target, notify);

            if (notify) {
                sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>Toggled fly mode for " + target.getName() + ".")));
                target.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>Your fly mode was toggled by " + player.getName() + ".")));
            } else {
                sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>Toggled fly mode for " + target.getName() + " without notifying.")));
            }
        } else {
            if (!sender.hasPermission("beaconlabs.core.fly.self")) {
                sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>You do not have permission to use this command.")));
                return;
            }

            toggleFlight(player, true);
        }
        return;
    }

    private void toggleFlight(Player player, boolean notify) {
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            if (notify) {
                player.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>Fly mode disabled.")));
            }
        } else {
            player.setAllowFlight(true);
            player.setFlying(true);
            if (notify) {
                player.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>Fly mode enabled.")));
            }
        }
    }

    @Override
    public java.util.Collection<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        return args.length <= 1 ? org.bcnlab.beaconlabscore.commands.CommandCompletion.players(
                org.bcnlab.beaconlabscore.commands.CommandCompletion.argument(args, 0)) : java.util.List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.fly.self")
                || sender.hasPermission("beaconlabs.core.fly.others");
    }
}
