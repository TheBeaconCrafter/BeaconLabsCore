package org.bcnlab.beaconlabscore.commands.player;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InvseeCommand implements io.papermc.paper.command.brigadier.BasicCommand {

    private final BeaconLabsCore plugin;

    public InvseeCommand(BeaconLabsCore plugin) {
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

        if (!sender.hasPermission("beaconlabs.core.invsee")) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>You do not have permission to use this command.")));
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Usage: /invsee <player>")));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Player not found: " + args[0])));
            return;
        }

        Inventory targetInventory = target.getInventory();
        player.openInventory(targetInventory);

        sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>You are now viewing the inventory of " + target.getName() + ".")));
        return;
    }

    @Override
    public java.util.Collection<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        return args.length <= 1 ? org.bcnlab.beaconlabscore.commands.CommandCompletion.players(
                org.bcnlab.beaconlabscore.commands.CommandCompletion.argument(args, 0)) : java.util.List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.invsee");
    }
}
