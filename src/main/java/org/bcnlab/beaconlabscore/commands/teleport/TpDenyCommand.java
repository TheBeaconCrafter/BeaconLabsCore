package org.bcnlab.beaconlabscore.commands.teleport;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bcnlab.beaconlabscore.commands.teleport.TpaCommand.tpaRequests;

public class TpDenyCommand implements CommandExecutor {

    private final Component pluginPrefix;

    public TpDenyCommand(String pluginPrefix) {
        this.pluginPrefix = LegacyComponentSerializer.legacyAmpersand().deserialize(pluginPrefix);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>This command can only be used by players.")));
            return true;
        }

        Player target = (Player) sender;

        if (!sender.hasPermission("beaconlabs.core.tpdeny")) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>You do not have permission to use this command.")));
            return true;
        }

        String requesterName = tpaRequests.get(target.getName());
        if (requesterName == null) {
            sender.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>You have no pending teleport requests.")));
            return true;
        }

        Player requester = target.getServer().getPlayer(requesterName);
        if (requester != null) {
            requester.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>Your teleport request to " + target.getName() + " was denied.")));
        }

        target.sendMessage(pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>You have denied the teleport request from " + requesterName + ".")));
        tpaRequests.remove(target.getName());
        return true;
    }
}
