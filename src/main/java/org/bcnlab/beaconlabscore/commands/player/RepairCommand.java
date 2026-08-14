package org.bcnlab.beaconlabscore.commands.player;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Command to repair the currently held item
 */
public class RepairCommand implements io.papermc.paper.command.brigadier.BasicCommand {

    private final BeaconLabsCore plugin;

    public RepairCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (!sender.hasPermission("beaconlabs.core.repair")) {
            sender.sendMessage(plugin.getPrefix(sender).append(LegacyComponentSerializer.legacyAmpersand().deserialize(plugin.getNoPermsMessage())));
            return;
        }

        Player target;

        if (args.length >= 1) {
            // Command used as: /repair <player>
            if (!sender.hasPermission("beaconlabs.core.repair.others")) {
                sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>You don't have permission to repair other players' items.")));
                return;
            }

            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Player '" + args[0] + "' not found or is not online.")));
                return;
            }
        } else if (sender instanceof Player) {
            // Command used as: /repair
            target = (Player) sender;
        } else {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>Console must specify a player: /repair <player>")));
            return;
        }

        ItemStack item = target.getInventory().getItemInMainHand();
        
        if (item == null || item.getType() == Material.AIR) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>" + (sender == target ? "You are" : target.getName() + " is") + " not holding any item.")));
            return;
        }
        
        // Check if the item can be damaged
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable)) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>This item cannot be repaired.")));
            return;
        }
        
        Damageable damageable = (Damageable) meta;
        
        // Check if the item is already at full durability
        if (!damageable.hasDamage()) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>This item is already at full durability.")));
            return;
        }
        
        damageable.setDamage(0);
        item.setItemMeta(meta);
        
        String itemName = formatItemName(item.getType().name());
        
        if (sender != target) {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>You repaired " + target.getName() + "'s " + itemName + ".")));
            target.sendMessage(plugin.getPrefix(target).append(MiniMessage.miniMessage().deserialize("<gray>Your " + itemName + " was repaired by " + sender.getName() + ".")));
        } else {
            sender.sendMessage(plugin.getPrefix(sender).append(MiniMessage.miniMessage().deserialize("<gray>You repaired your " + itemName + ".")));
        }
        
        return;
    }

    @Override
    public List<String> suggest(io.papermc.paper.command.brigadier.CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        List<String> completions = new ArrayList<>();
        
        if (!sender.hasPermission("beaconlabs.core.repair")) {
            return completions;
        }
        
        if (args.length <= 1 && sender.hasPermission("beaconlabs.core.repair.others")) {
            String partialName = org.bcnlab.beaconlabscore.commands.CommandCompletion
                    .argument(args, 0).toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(partialName)) {
                    completions.add(player.getName());
                }
            }
        }
        
        return completions;
    }
      /**
     * Format item name from UPPER_SNAKE_CASE to Title Case
     */
    private String formatItemName(String itemName) {
        // Convert UPPER_SNAKE_CASE to Title Case with spaces
        String[] words = itemName.split("_");
        StringBuilder titleCase = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 0) {
                // Capitalize first letter, lowercase the rest
                titleCase.append(words[i].substring(0, 1).toUpperCase());
                if (words[i].length() > 1) {
                    titleCase.append(words[i].substring(1).toLowerCase());
                }
                // Add space if not the last word
                if (i < words.length - 1) {
                    titleCase.append(" ");
                }
            }
        }
        
        return titleCase.toString();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("beaconlabs.core.repair");
    }
}
