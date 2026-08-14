package org.bcnlab.beaconlabscore.commands.player;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the gamemode shortcut commands: /gms, /gmc, /gma, /gmsp
 */
public class GamemodeShortcutCommand implements io.papermc.paper.command.brigadier.BasicCommand {
    
    private final BeaconLabsCore plugin;
    private final GameMode gameMode;
    private final String gameModeName;
    
    public GamemodeShortcutCommand(BeaconLabsCore plugin, GameMode gameMode) {
        this.plugin = plugin;
        this.gameMode = gameMode;
        
        // Set the friendly name for the gamemode
        switch (gameMode) {
            case CREATIVE:
                gameModeName = "Creative";
                break;
            case SURVIVAL:
                gameModeName = "Survival";
                break;
            case ADVENTURE:
                gameModeName = "Adventure";
                break;
            case SPECTATOR:
                gameModeName = "Spectator";
                break;
            default:
                gameModeName = gameMode.name();
        }
    }
    
    @Override
    public void execute(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        // Check for self permission or others permission
        if (args.length == 0) {
            // Setting own gamemode
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Console cannot change its own gamemode!")));
                return;
            }
            
            Player player = (Player) sender;
            if (!player.hasPermission("beaconlabs.core.gamemode.self")) {
                player.sendMessage(plugin.getPrefix(player).append(LegacyComponentSerializer.legacyAmpersand().deserialize(plugin.getNoPermsMessage())));
                return;
            }
            
            changeGameMode(player, player, false);
        } else {
            // Setting another player's gamemode
            if (!sender.hasPermission("beaconlabs.core.gamemode.others")) {
                sender.sendMessage(plugin.getPrefix(sender).append(LegacyComponentSerializer.legacyAmpersand().deserialize(plugin.getNoPermsMessage())));
                return;
            }
            
            String targetName = args[0];
            Player target = Bukkit.getPlayer(targetName);
            
            if (target == null) {
                sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Player '" + targetName + "' not found or is not online.")));
                return;
            }
            
            boolean silent = args.length > 1 && args[1].equalsIgnoreCase("nonotify");
            changeGameMode(sender, target, silent);
        }
        
        return;
    }
    
    private void changeGameMode(CommandSender sender, Player target, boolean silent) {
        target.setGameMode(gameMode);
        
        if (!silent) {
            target.sendMessage(plugin.getPrefix(target).append(MiniMessage.miniMessage().deserialize("<gray>Your gamemode has been changed to <gold>" + gameModeName + "<gray>.")));
        }
        
        if (sender != target && !silent) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Changed <gold>" + target.getName() + "<gray>'s gamemode to <gold>" + gameModeName + "<gray>.")));
        }
    }

    @Override
    public java.util.Collection<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        return args.length <= 1 ? org.bcnlab.beaconlabscore.commands.CommandCompletion.players(
                org.bcnlab.beaconlabscore.commands.CommandCompletion.argument(args, 0)) : java.util.List.of();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.gamemode.self")
                || sender.hasPermission("beaconlabs.core.gamemode.others");
    }
}
