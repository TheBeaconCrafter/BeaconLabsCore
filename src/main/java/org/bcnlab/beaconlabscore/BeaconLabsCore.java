package org.bcnlab.beaconlabscore;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class BeaconLabsCore extends JavaPlugin {

    private String pluginPrefix;

    @Override
    public void onEnable() {
        // Save the default config if it doesn't exist
        createDefaultConfig();

        // Load the configuration
        loadConfig();

        getCommand("fly").setExecutor(new FlyCommand(pluginPrefix));

        // Plugin startup logic
        getLogger().info(pluginPrefix + "BeaconLabsCore was enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info(pluginPrefix + "BeaconLabsCore was disabled!");
    }

    // Method to create the default configuration if it doesn't exist
    private void createDefaultConfig() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        config.addDefault("plugin-prefix", "&6BeaconLabs &8» ");
        saveConfig();
    }

    // Method to load the configuration
    private void loadConfig() {
        FileConfiguration config = getConfig();
        pluginPrefix = config.getString("plugin-prefix", "&6BeaconLabs &8» "); // Default prefix if not set
    }
}
