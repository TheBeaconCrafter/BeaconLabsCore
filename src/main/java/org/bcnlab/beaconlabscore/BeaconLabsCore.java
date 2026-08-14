package org.bcnlab.beaconlabscore;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bcnlab.beaconlabscore.commands.*;
import org.bcnlab.beaconlabscore.commands.chat.ChatSudoCommand;
import org.bcnlab.beaconlabscore.commands.chat.GlobalMuteCommand;
import org.bcnlab.beaconlabscore.commands.chat.ServerBroadcastCommand;
import org.bcnlab.beaconlabscore.commands.player.*;
import org.bcnlab.beaconlabscore.commands.teleport.*;
import org.bcnlab.beaconlabscore.commands.time.*;
import org.bcnlab.beaconlabscore.commands.utils.*;
import org.bcnlab.beaconlabscore.commands.weather.ClearCommand;
import org.bcnlab.beaconlabscore.commands.weather.RainCommand;
import org.bcnlab.beaconlabscore.commands.weather.StormCommand;
import org.bcnlab.beaconlabscore.commands.weather.WeatherCommand;
import org.bcnlab.beaconlabscore.listeners.*;
import org.bcnlab.beaconlabscore.utils.WarpManager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class BeaconLabsCore extends JavaPlugin implements Listener {

    private String pluginPrefix;
    private String legacyPrefixString;
    private Component legacyPrefix;
    private String pluginVersion = "1.4.0";
    private ChatFormatter chatFormatter;
    private org.bcnlab.beaconlabscore.listeners.NametagGenerator nametagGenerator;
    private WarpManager warpManager;
    private final Map<UUID, Location> lastDeathLocations = new HashMap<>();
    public WarpManager getWarpManager() { return warpManager; }

    public void recordDeathLocation(Player player) {
        lastDeathLocations.put(player.getUniqueId(), player.getLocation().clone());
    }

    public Location getLastDeathLocation(Player player) {
        Location location = lastDeathLocations.get(player.getUniqueId());
        return location == null ? null : location.clone();
    }

    //CONFIG
    private boolean joinMessagesEnabled;
    private boolean leaveMessagesEnabled;
    private boolean deathMessagesEnabled;
    private String customJoinMessage;
    private String customLeaveMessage;
    private String customDeathMessage;
    private String noPermsMessage = "&cYou do not have permission to use this command.";
    private int warpDelay; // Delay in seconds for warp teleportation

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void onEnable() {
        // Save the default config if it doesn't exist
        createDefaultConfig();

        // Load the configuration
        loadConfig();

        LuckPerms luckPerms = LuckPermsProvider.get();
        chatFormatter = new ChatFormatter(this, luckPerms);
        
        // Initialize the WarpManager
        warpManager = new WarpManager(this);

        // Register other commands and listeners
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new JoinLeaveMessages(this), this);
        getServer().getPluginManager().registerEvents(new DeathMessages(this), this);
        getServer().getPluginManager().registerEvents(new UnknownCommandListener(this), this);
        nametagGenerator = new org.bcnlab.beaconlabscore.listeners.NametagGenerator();
        getServer().getPluginManager().registerEvents(nametagGenerator, this);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            org.bcnlab.beaconlabscore.utils.CommandRegistry.registerAll(this, event.registrar());
        });


        // Plugin startup logic
        org.bukkit.Bukkit.getConsoleSender().sendMessage(getPrefix().append(Component.text("BeaconLabsCore was enabled!", net.kyori.adventure.text.format.NamedTextColor.GREEN)));
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true); // Prevent default chat behavior
        final Player player = event.getPlayer();
        final String message = event.getMessage();

        // AsyncPlayerChatEvent is commonly fired off the main thread. Defer all
        // Bukkit player iteration and message delivery to the server thread.
        getServer().getScheduler().runTask(this, () -> {
            if (!player.isOnline()) return;
            if (GlobalMuteCommand.globalmute && !player.hasPermission("beaconlabs.core.globalmute.ignore")) {
                player.sendMessage(getPrefix(player).append(
                        net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                                .deserialize("<gray>Chat is <gold>deactivated!")));
                return;
            }
            chatFormatter.onPlayerChat(player, message);
        });
    }

    @Override
    public void onDisable() {
        // Close the warp database connection
        if (warpManager != null) {
            warpManager.closeConnection();
        }

        // Plugin shutdown logic
        org.bukkit.Bukkit.getConsoleSender().sendMessage(getPrefix().append(Component.text("BeaconLabsCore was disabled!", net.kyori.adventure.text.format.NamedTextColor.RED)));
    }

    public org.bcnlab.beaconlabscore.listeners.NametagGenerator getNametagGenerator() {
        return nametagGenerator;
    }

    // Method to load the configuration
    private void loadConfig() {
        FileConfiguration config = getConfig();

        // Load plugin prefix from config
        pluginPrefix = config.getString("plugin-prefix", "<gradient:gold:yellow>BeaconLabs</gradient> <dark_gray>»</dark_gray> ");

        // Load join and leave messages enabled status
        joinMessagesEnabled = config.getBoolean("join-messages.enabled", true);
        leaveMessagesEnabled = config.getBoolean("leave-messages.enabled", true);
        deathMessagesEnabled = config.getBoolean("death-messages.enabled", true);
        customJoinMessage = config.getString("join-messages.custom", "&8[&a+&8] &7{player}");
        leaveMessagesEnabled = config.getBoolean("leave-messages.enabled", true);
        customLeaveMessage = config.getString("leave-messages.custom", "&8[&c-&8] &7{player}");
        customDeathMessage = config.getString("death-messages.custom", "&4&lERROR");
        
        // Load warp delay
        warpDelay = config.getInt("warp-delay", 3);

        // Prepend prefix to custom messages (legacy section codes from config)
        customDeathMessage = pluginPrefix + customDeathMessage;
        
        legacyPrefixString = config.getString("legacy-prefix", "&6BeaconLabs &8» &7");
        legacyPrefix = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize(legacyPrefixString);
    }

    // Method to create the default configuration if it doesn't exist
    private void createDefaultConfig() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        config.addDefault("plugin-prefix", "<gradient:gold:yellow>BeaconLabs</gradient> <dark_gray>»</dark_gray> ");
        config.addDefault("join-messages.enabled", true);
        config.addDefault("join-messages.custom", "&7[&a+&7] &9{player}");
        config.addDefault("leave-messages.enabled", true);
        config.addDefault("leave-messages.custom", "&7[&a-&7] &9{player}");
        config.addDefault("death-messages.enabled", true);
        config.addDefault("death-messages.custom", "&c{player} was killed by {other}");
        config.addDefault("warp-delay", 3);
        saveConfig();
    }

    public Component getPrefix() {
        return MiniMessage.miniMessage().deserialize(pluginPrefix);
    }

    public Component getPrefix(Player player) {
        try {
            if (player.hasMetadata("protocol_version")) {
                int protocol = player.getMetadata("protocol_version").get(0).asInt();
                if (protocol <= 47) {
                    return legacyPrefix;
                }
            }
        } catch (Exception e) {}
        return getPrefix();
    }
    
    public Component getPrefix(CommandSourceStack source) {
        if (source.getSender() instanceof Player p) {
            return getPrefix(p);
        }
        return getPrefix();
    }
    
    public Component getPrefix(org.bukkit.command.CommandSender sender) {
        if (sender instanceof Player p) {
            return getPrefix(p);
        }
        return getPrefix();
    }

    public String getVersion() {
        return pluginVersion;
    }

    public boolean areJoinMessagesEnabled() {
        return joinMessagesEnabled;
    }

    public boolean areLeaveMessagesEnabled() {
        return leaveMessagesEnabled;
    }

    public boolean areDeathMessagesEnabled() {
        return deathMessagesEnabled;
    }

    public String getCustomJoinMessage() {
        return customJoinMessage;
    }

    public String getCustomLeaveMessage() {
        return customLeaveMessage;
    }

    public String getCustomDeathMessage() {
        return customDeathMessage;
    }

    public String getNoPermsMessage() {
        return noPermsMessage;
    }
    
    public int getWarpDelay() {
        return warpDelay;
    }
}
