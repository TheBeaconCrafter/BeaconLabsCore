package org.bcnlab.beaconlabscore.commands.teleport;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bcnlab.beaconlabscore.commands.teleport.TpaCommand.tpaRequests;

public class TpDenyCommand implements io.papermc.paper.command.brigadier.BasicCommand {

    private final Component pluginPrefix;

    public TpDenyCommand(String pluginPrefix) {
        this.pluginPrefix = LegacyComponentSerializer.legacyAmpersand().deserialize(pluginPrefix);
    }

    @Override
    public void execute(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (!(sender instanceof Player)) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>This command can only be used by players.")));
            return;
        }

        Player target = (Player) sender;

        if (!sender.hasPermission("beaconlabs.core.tpdeny")) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>You do not have permission to use this command.")));
            return;
        }

        String requesterName = tpaRequests.get(target.getName());
        if (requesterName == null) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>You have no pending teleport requests.")));
            return;
        }

        Player requester = target.getServer().getPlayer(requesterName);
        if (requester != null) {
            requester.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>Your teleport request to " + target.getName() + " was denied.")));
        }

        target.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>You have denied the teleport request from " + requesterName + ".")));
        tpaRequests.remove(target.getName());
        return;
    }

    @Override
    public java.util.Collection<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        return args.length <= 1 ? org.bcnlab.beaconlabscore.commands.CommandCompletion.players(
                org.bcnlab.beaconlabscore.commands.CommandCompletion.argument(args, 0)) : java.util.List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.tpdeny");
    }
}
