package org.bcnlab.beaconlabscore.commands.player;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearInventoryCommand implements io.papermc.paper.command.brigadier.BasicCommand {

    private final BeaconLabsCore plugin;

    public ClearInventoryCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (sender.hasPermission("beaconlabs.core.clearinventory.self")) {
                    clearInventory(player);
                    player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>Your inventory has been cleared.")));
                } else {
                    player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>You are not allowed to clear your inventory.")));
                }
            } else {
                sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Only players can clear their own inventory.")));
            }
            return;
        } else if (args.length == 1) {
            if (sender.hasPermission("beaconlabs.core.clearinventory.others")) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    clearInventory(target);
                    sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Cleared the inventory of " + target.getName() + ".")));
                    target.sendMessage(plugin.getPrefix(target).append(MiniMessage.miniMessage().deserialize("<gray>Your inventory has been cleared by an admin.")));
                } else {
                    sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Player not found.")));
                }
            } else {
                sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>You do not have permission to clear other players' inventories.")));
            }
            return;
        } else {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Usage: /clearinv [player]")));
            return;
        }
    }

    private void clearInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }

    @Override
    public java.util.Collection<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        return args.length <= 1 ? org.bcnlab.beaconlabscore.commands.CommandCompletion.players(
                org.bcnlab.beaconlabscore.commands.CommandCompletion.argument(args, 0)) : java.util.List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.clearinventory.self")
                || sender.hasPermission("beaconlabs.core.clearinventory.others");
    }
}
