package org.bcnlab.beaconlabscore.commands.utils;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorkbenchCommand implements io.papermc.paper.command.brigadier.BasicCommand {

    private final BeaconLabsCore plugin;

    public WorkbenchCommand(BeaconLabsCore plugin) {
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

        if (!sender.hasPermission("beaconlabs.core.workbench")) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>You do not have permission to use this command.")));
            return;
        }

        player.openWorkbench(null, true);
        return;
    }

    @Override
    public java.util.Collection<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        return java.util.List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.workbench");
    }
}
