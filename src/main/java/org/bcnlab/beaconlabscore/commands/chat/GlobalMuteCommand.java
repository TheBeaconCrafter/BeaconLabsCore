package org.bcnlab.beaconlabscore.commands.chat;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlobalMuteCommand implements CommandExecutor {
    private BeaconLabsCore plugin;
    public static boolean globalmute = false;

    public GlobalMuteCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<red>This command can only be used by players.")));
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("beaconlabs.core.globalmute")) {
            player.sendMessage(plugin.getPrefix().append(LegacyComponentSerializer.legacyAmpersand().deserialize(plugin.getNoPermsMessage())));
            return true;
        }

        globalmute = !globalmute;
        String status = globalmute ? "deactivated" : "reactivated";
        player.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<green>Chat was " + status + "!")));

        return true;
    }
}
