package org.bcnlab.beaconlabscore.commands.utils;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class EndSpectateCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public EndSpectateCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the command sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Only players can use this command!")));
            return true;
        }

        Player player = (Player) sender;

        // Check permission
        if (!player.hasPermission("beaconlabs.core.endspectator")) {
            player.sendMessage(plugin.getPrefix(player).append(MiniMessage.miniMessage().deserialize("<gray>You do not have permission to use this command.")));
            return true;
        }

        // Execute end spectator actions
        endSpectator(player);

        return true;
    }

    private void endSpectator(Player spectator) {
        // Execute /vanish command for the player
        Bukkit.dispatchCommand(spectator, "vanish");

        // Disable flight mode
        spectator.setAllowFlight(false);
        spectator.setFlying(false);

        // Set gamemode to survival mode
        spectator.setGameMode(GameMode.SURVIVAL);

        // Remove all potion effects
        for (PotionEffect effect : spectator.getActivePotionEffects()) {
            spectator.removePotionEffect(effect.getType());
        }

        // Send confirmation message
        spectator.sendMessage(plugin.getPrefix(spectator).append(MiniMessage.miniMessage().deserialize("<gray>You are no longer spectating.")));

        // Clear title (if any)
        spectator.resetTitle();
    }
}
