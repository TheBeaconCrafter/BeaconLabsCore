package org.bcnlab.beaconlabscore.commands;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;

public class GlobalMuteCommand implements CommandExecutor
{
    public static boolean globalmute;
    private final BeaconLabsCore plugin;

    public GlobalMuteCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender s, final Command cmd, final String l, final String[] args) {
        if (s instanceof Player) {
            final Player p = (Player)s;
            if (p.hasPermission("beaconlabs.core.globalmute.set")) {
                if (GlobalMuteCommand.globalmute) {
                    Bukkit.broadcastMessage(plugin.getPrefix() + "§aChat was §creactivated!");
                    GlobalMuteCommand.globalmute = false;
                }
                else {
                    Bukkit.broadcastMessage(plugin.getPrefix() + "§aChat was §cdeactivated!");
                    GlobalMuteCommand.globalmute = true;
                }
            }
            else {
                p.sendMessage(plugin.getPrefix() + ChatColor.translateAlternateColorCodes('&', plugin.getMoPermsMessage()));
            }
        }
        return false;
    }

    static {
        GlobalMuteCommand.globalmute = false;
    }
}