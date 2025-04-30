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
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.TabCompleter;

public final class BeaconLabsCore extends JavaPlugin implements Listener {

    private String pluginPrefix;
    private String pluginVersion = "1.2";
    private ChatFormatter chatFormatter;
    private WarpManager warpManager;

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
    public void onEnable() {
        // Save the default config if it doesn't exist
        createDefaultConfig();

        // Load the configuration
        loadConfig();

        LuckPerms luckPerms = LuckPermsProvider.get();
        chatFormatter = new ChatFormatter(this, luckPerms);
        
        // Initialize the WarpManager
        warpManager = new WarpManager(this);

        // Register JoinLeaveMessages listener
        new JoinLeaveMessages(this);

        // Register other commands and listeners
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new JoinLeaveMessages(this), this);
        getServer().getPluginManager().registerEvents(new DeathMessages(this), this);
        getServer().getPluginManager().registerEvents(new UnknownCommandListener(this), this);
        getServer().getPluginManager().registerEvents(new NametagGenerator(), this);
        getCommand("fly").setExecutor(new FlyCommand(pluginPrefix));
        getCommand("core").setExecutor(new CoreCommand(this));
        getCommand("heal").setExecutor(new HealCommand(this));
        getCommand("gamemode").setExecutor(new GamemodeCommand(this));
        getCommand("invsee").setExecutor(new InvseeCommand(this));
        getCommand("workbench").setExecutor(new WorkbenchCommand(this));
        getCommand("enderchest").setExecutor(new EnderchestCommand(this));
        getCommand("vanish").setExecutor(new VanishCommand(this));
        getCommand("tp").setExecutor(new TpCommand(this));
        getCommand("tphere").setExecutor(new TpHereCommand(this));
        getCommand("spectate").setExecutor(new SpectateCommand(this));
        getCommand("endspectate").setExecutor(new EndSpectateCommand(this));
        getCommand("chatsudo").setExecutor(new ChatSudoCommand(this, luckPerms));
        getCommand("sudo").setExecutor(new SudoCommand(this));
        getCommand("serverbroadcast").setExecutor(new ServerBroadcastCommand(this));
        getCommand("weather").setExecutor(new WeatherCommand(this));
        getCommand("clear").setExecutor(new ClearCommand(this));
        getCommand("rain").setExecutor(new RainCommand(this));
        getCommand("storm").setExecutor(new StormCommand(this));
        getCommand("day").setExecutor(new DayCommand(this));
        getCommand("midnight").setExecutor(new MidnightCommand(this));
        getCommand("night").setExecutor(new NightCommand(this));
        getCommand("noon").setExecutor(new NoonCommand(this));
        getCommand("time").setExecutor(new TimeCommand(this));
        getCommand("globalmute").setExecutor(new GlobalMuteCommand(this));
        getCommand("btps").setExecutor(new TpsCommand(this));
        getCommand("clearinventory").setExecutor(new ClearInventoryCommand(this));
        getCommand("clearlag").setExecutor(new ClearLagCommand(this));
        getCommand("tpa").setExecutor(new TpaCommand(this.pluginPrefix));
        getCommand("tpaccept").setExecutor(new TpAcceptCommand(this.pluginPrefix));
        getCommand("tpdeny").setExecutor(new TpDenyCommand(this.pluginPrefix));
        getCommand("randomteleport").setExecutor(new RandomTeleportCommand(this.pluginPrefix, 5000));
        getCommand("teleportcoordinates").setExecutor(new TpcCommand(this));
        
        // Register warp commands
        getCommand("warp").setExecutor(new WarpCommand(this, warpManager));
        getCommand("setwarp").setExecutor(new SetWarpCommand(this, warpManager));
        getCommand("delwarp").setExecutor(new DelWarpCommand(this, warpManager));
        getCommand("warps").setExecutor(new WarpsCommand(this, warpManager));
        
        // Register tab completers for warp commands
        getCommand("warp").setTabCompleter((TabCompleter) getCommand("warp").getExecutor());
        getCommand("delwarp").setTabCompleter((TabCompleter) getCommand("delwarp").getExecutor());
        getCommand("warps").setTabCompleter((TabCompleter) getCommand("warps").getExecutor());
        
        // Register gamemode shortcut commands
        getCommand("gmc").setExecutor(new GamemodeShortcutCommand(this, GameMode.CREATIVE));
        getCommand("gms").setExecutor(new GamemodeShortcutCommand(this, GameMode.SURVIVAL));
        getCommand("gma").setExecutor(new GamemodeShortcutCommand(this, GameMode.ADVENTURE));
        getCommand("gmsp").setExecutor(new GamemodeShortcutCommand(this, GameMode.SPECTATOR));

        // Plugin startup logic
        getLogger().info(pluginPrefix + "BeaconLabsCore was enabled!");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true); // Prevent default chat behavior
        final Player p = event.getPlayer();
        if (GlobalMuteCommand.globalmute) {
            if (!p.hasPermission("beaconlabs.core.globalmute.ignore")) {
                p.sendMessage(getPrefix() + "§aChat is §cdeactivated!");
                event.setCancelled(true);
            } else {
                chatFormatter.onPlayerChat(event.getPlayer(), event.getMessage());
            }
        }
        else {
            chatFormatter.onPlayerChat(event.getPlayer(), event.getMessage());
        }
    }

    @Override
    public void onDisable() {
        // Close the warp database connection
        if (warpManager != null) {
            warpManager.closeConnection();
        }
        
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
        
        // Load warp delay
        warpDelay = config.getInt("warp-delay", 3);

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
        config.addDefault("warp-delay", 3);
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

    public String getNoPermsMessage() {
        return noPermsMessage;
    }
    
    public int getWarpDelay() {
        return warpDelay;
    }
}
