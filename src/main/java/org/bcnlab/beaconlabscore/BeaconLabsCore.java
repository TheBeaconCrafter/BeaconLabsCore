package org.bcnlab.beaconlabscore;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class BeaconLabsCore extends JavaPlugin implements Listener {

    private String pluginPrefix;
    private String pluginVersion = "1.0";
    private ChatFormatter chatFormatter;

    //CONFIG
    private boolean joinMessagesEnabled;
    private boolean leaveMessagesEnabled;
    private boolean deathMessagesEnabled;
    private String customJoinMessage;
    private String customLeaveMessage;
    private String customDeathMessage;

    @Override
    public void onEnable() {
        // Save the default config if it doesn't exist
        createDefaultConfig();

        // Load the configuration
        loadConfig();

        LuckPerms luckPerms = LuckPermsProvider.get();
        chatFormatter = new ChatFormatter(this, luckPerms);

        // Register JoinLeaveMessages listener
        new JoinLeaveMessages(this);

        // Register other commands and listeners
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new JoinLeaveMessages(this), this);
        getServer().getPluginManager().registerEvents(new DeathMessages(this), this);
        getCommand("fly").setExecutor(new FlyCommand(pluginPrefix));
        getCommand("core").setExecutor(new CoreCommand(this));
        getCommand("heal").setExecutor(new HealCommand(this));
        getCommand("gamemode").setExecutor(new GamemodeCommand(this));
        getCommand("invsee").setExecutor(new InvseeCommand(this));
        getCommand("workbench").setExecutor(new WorkbenchCommand(this));
        getCommand("enderchest").setExecutor(new EnderchestCommand(this));
        getCommand("vanish").setExecutor(new VanishCommand(this));

        // Plugin startup logic
        getLogger().info(pluginPrefix + "BeaconLabsCore was enabled!");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true); // Prevent default chat behavior
        chatFormatter.onPlayerChat(event.getPlayer(), event.getMessage());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info(pluginPrefix + "BeaconLabsCore was disabled!");
    }

    // Method to load the configuration
    private void loadConfig() {
        FileConfiguration config = getConfig();

        // Load plugin prefix from config
        pluginPrefix = config.getString("plugin-prefix", "&6BeaconLabs &8» ");

        // Load join and leave messages enabled status
        joinMessagesEnabled = config.getBoolean("join-messages.enabled", true);
        leaveMessagesEnabled = config.getBoolean("leave-messages.enabled", true);
        deathMessagesEnabled = config.getBoolean("death-messages.enabled", true);
        customJoinMessage = config.getString("join-messages.custom", "&6[+] PLEASE RESTART FOR THIS TO WORK");
        customLeaveMessage = config.getString("leave-messages.custom", "&6[-] PLEASE RESTART FOR THIS TO WORK");
        customDeathMessage = config.getString("death-messages.custom", "&4&lERROR");

        // Translate color codes in custom messages
        customJoinMessage = ChatColor.translateAlternateColorCodes('&', customJoinMessage);
        customLeaveMessage = ChatColor.translateAlternateColorCodes('&', customLeaveMessage);
        customDeathMessage = ChatColor.translateAlternateColorCodes('&', customDeathMessage);
    }

    // Method to create the default configuration if it doesn't exist
    private void createDefaultConfig() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        config.addDefault("plugin-prefix", "&6BeaconLabs &8» ");
        config.addDefault("join-messages.enabled", true);
        config.addDefault("join-messages.custom", "&7[&a+&7] &9{player}");
        config.addDefault("leave-messages.enabled", true);
        config.addDefault("leave-messages.custom", "&7[&a-&7] &9{player}");
        config.addDefault("death-messages.enabled", true);
        config.addDefault("death-messages.custom", "&c{player} was killed by {other}");
        saveConfig();
    }

    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', pluginPrefix);
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
}
