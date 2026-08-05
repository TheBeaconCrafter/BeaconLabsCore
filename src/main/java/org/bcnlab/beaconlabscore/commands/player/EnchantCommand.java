package org.bcnlab.beaconlabscore.commands.player;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command to enchant items with any level up to 255
 */
public class EnchantCommand implements CommandExecutor, TabCompleter {

    private final BeaconLabsCore plugin;

    public EnchantCommand(BeaconLabsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("beaconlabs.core.enchant")) {
            sender.sendMessage(plugin.getPrefix().append(LegacyComponentSerializer.legacyAmpersand().deserialize(plugin.getNoPermsMessage())));
            return true;
        }

        // Check if args length is correct
        if (args.length < 2) {
            sender.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<red>Usage: /ench [player] <enchantment> <level>")));
            return true;
        }

        Player target;
        String enchantmentName;
        int level;

        // Parse arguments based on if player is specified
        if (args.length >= 3 && Bukkit.getPlayer(args[0]) != null) {
            // Format: /ench <player> <enchantment> <level>
            target = Bukkit.getPlayer(args[0]);
            enchantmentName = args[1];
            try {
                level = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<red>Level must be a number between 1 and 255.")));
                return true;
            }
        } else if (sender instanceof Player) {
            // Format: /ench <enchantment> <level>
            target = (Player) sender;
            enchantmentName = args[0];
            try {
                level = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<red>Level must be a number between 1 and 255.")));
                return true;
            }
        } else {
            sender.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<red>Console must specify a player: /ench <player> <enchantment> <level>")));
            return true;
        }

        // Validate level
        if (level < 1 || level > 255) {
            sender.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<red>Level must be between 1 and 255.")));
            return true;
        }

        // Get the enchantment
        Enchantment enchantment = getEnchantmentByName(enchantmentName);
        if (enchantment == null) {
            sender.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<red>Unknown enchantment: " + enchantmentName)));
            return true;
        }

        // Get the item in hand
        ItemStack item = target.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            sender.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<red>The player must be holding an item.")));
            return true;
        }

        // Handle enchanted books differently
        if (item.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            meta.addStoredEnchant(enchantment, level, true);
            item.setItemMeta(meta);
        } else {
            // Check if the enchantment can be applied to the item
            if (!enchantment.canEnchantItem(item) && !sender.hasPermission("beaconlabs.core.enchant.bypass")) {
                sender.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<red>This enchantment cannot be applied to this item.")));
                return true;
            }

            // Add the enchantment
            item.addUnsafeEnchantment(enchantment, level);
        }

        // Send success messages
        if (sender != target) {
            sender.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<green>Applied " + 
                formatEnchantmentName(enchantment.getKey().getKey()) + " " + level + " to " + target.getName() + "'s " + formatItemName(item.getType().name()) + ".")));
            target.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<green>Your " + formatItemName(item.getType().name()) + 
                " was enchanted with " + formatEnchantmentName(enchantment.getKey().getKey()) + " " + level + ".")));
        } else {
            sender.sendMessage(plugin.getPrefix().append(MiniMessage.miniMessage().deserialize("<green>Applied " + 
                formatEnchantmentName(enchantment.getKey().getKey()) + " " + level + " to your " + formatItemName(item.getType().name()) + ".")));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("beaconlabs.core.enchant")) {
            return completions;
        }

        if (args.length == 1) {
            // First argument could be player name or enchantment
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(player.getName());
                }
            }            // Also add enchantment names
            for (Enchantment enchantment : Enchantment.values()) {
                String enchName = enchantment.getKey().getKey();
                if (enchName.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(enchName);
                }
            }
        } else if (args.length == 2) {
            // Second argument could be enchantment or level
            if (Bukkit.getPlayer(args[0]) != null) {
                // If first arg was a player, second is enchantment
                for (Enchantment enchantment : Enchantment.values()) {
                    String enchName = enchantment.getKey().getKey();
                    if (enchName.toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(enchName);
                    }
                }
            } else {
                // If first arg was an enchantment, second is level
                completions.addAll(Arrays.asList("1", "5", "10", "25", "50", "100", "255"));
            }
        } else if (args.length == 3) {
            // Third argument must be level
            completions.addAll(Arrays.asList("1", "5", "10", "25", "50", "100", "255"));
        }

        return completions;
    }

    /**
     * Get an enchantment by name, with support for partial matching
     */
    private Enchantment getEnchantmentByName(String name) {
        name = name.toLowerCase();
        
        // Try direct match first
        for (Enchantment enchantment : Enchantment.values()) {
            String key = enchantment.getKey().getKey();
            if (key.equals(name)) {
                return enchantment;
            }
        }
        
        // Try partial match
        for (Enchantment enchantment : Enchantment.values()) {
            String key = enchantment.getKey().getKey();
            if (key.contains(name)) {
                return enchantment;
            }
        }
        
        return null;
    }

    /**
     * Format enchantment name from snake_case to Title Case
     */
    private String formatEnchantmentName(String enchName) {
        // Convert snake_case to Title Case with spaces
        return Arrays.stream(enchName.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    /**
     * Format item name from UPPER_SNAKE_CASE to Title Case
     */
    private String formatItemName(String itemName) {
        // Convert UPPER_SNAKE_CASE to Title Case with spaces
        return Arrays.stream(itemName.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
