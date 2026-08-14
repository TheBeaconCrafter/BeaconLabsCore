package org.bcnlab.beaconlabscore.commands.player;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bcnlab.beaconlabscore.commands.CommandCompletion;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.List;

@NullMarked
public class HealCommand implements BasicCommand {

    private final BeaconLabsCore plugin;

    public HealCommand(BeaconLabsCore plugin) {
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
            if (!sender.hasPermission("beaconlabs.core.heal.others")) {
                sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage()
                        .deserialize("<gray>You do not have permission to heal others.")));
                return;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage()
                        .deserialize("<gray>Player not found: " + args[0])));
                return;
            }

            boolean notify = args.length <= 1 || !(args[args.length - 1].equalsIgnoreCase("nonotify")
                    || args[args.length - 1].equalsIgnoreCase("n"));
            healPlayer(target);
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize(
                    "<gray>You healed " + target.getName() + (notify ? "." : " without notifying them."))));
            if (notify) {
                target.sendMessage(plugin.getPrefix(target).append(MiniMessage.miniMessage()
                        .deserialize("<gray>You have been healed by " + player.getName() + ".")));
            }
            return;
        }

        if (!sender.hasPermission("beaconlabs.core.heal.self")) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage()
                    .deserialize("<gray>You do not have permission to heal yourself.")));
            return;
        }

        healPlayer(player);
        player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage()
                .deserialize("<gray>You healed yourself.")));
    }

    @Override
    public Collection<String> suggest(CommandSourceStack stack, String[] args) {
        if (args.length <= 1 && stack.getSender().hasPermission("beaconlabs.core.heal.others")) {
            return CommandCompletion.players(CommandCompletion.argument(args, 0));
        }
        return List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.heal.self")
                || sender.hasPermission("beaconlabs.core.heal.others");
    }

    private void healPlayer(Player player) {
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setFireTicks(0);
    }
}
