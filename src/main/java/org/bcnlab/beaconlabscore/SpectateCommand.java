package org.bcnlab.beaconlabscore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpectateCommand implements CommandExecutor {

    private final BeaconLabsCore plugin;

    public SpectateCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the command sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        // Check permission
        if (!player.hasPermission("beaconlabscore.spectate")) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        // Validate command usage
        if (args.length != 1) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage: /spectate <player>");
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        // Check if the target player is online
        if (target == null || !target.isOnline()) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Player '" + targetName + "' is not online.");
            return true;
        }

        // Execute /vanish command for the player
        Bukkit.dispatchCommand(player, "vanish");

        // Execute spectate actions
        executeSpectate(player, target);

        return true;
    }

    private void executeSpectate(Player spectator, Player target) {
        // Set gamemode to adventure mode
        spectator.setGameMode(GameMode.ADVENTURE);

        // Enable flight mode
        spectator.setAllowFlight(true);
        spectator.setFlying(true);

        // Disable health and hunger loss
        spectator.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 255, false, false));
        spectator.setHealth(spectator.getMaxHealth()); // Set health to max
        spectator.setFoodLevel(20); // Set food level to max

        // Teleport to the target player
        spectator.teleport(target.getLocation());

        // Display spectating message in red
        spectator.sendMessage(ChatColor.RED + "Spectating " + target.getName());
    }

}
