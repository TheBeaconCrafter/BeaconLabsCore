package org.bcnlab.beaconlabscore.utils;

import org.bcnlab.beaconlabscore.BeaconLabsCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class WarpManager {
    private final BeaconLabsCore plugin;
    private final File databaseFile;
    private final Map<String, Location> warpLocations;
    private Connection connection;

    public WarpManager(BeaconLabsCore plugin) {
        this.plugin = plugin;
        this.databaseFile = new File(plugin.getDataFolder(), "warps.db");
        this.warpLocations = new HashMap<>();
        
        // Create plugin directory if it doesn't exist
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            plugin.getLogger().warning("Could not create the plugin data directory for warp storage.");
        }
        
        // Initialize the database
        initializeDatabase();
        
        // Load warps from the database
        loadWarps();
    }

    private void initializeDatabase() {
        try {
            // Register the JDBC driver for SQLite
            Class.forName("org.sqlite.JDBC");
            
            // Create a connection to the database
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());
            
            // Create the warps table if it doesn't exist
            try (Statement statement = connection.createStatement()) {
                statement.execute(
                    "CREATE TABLE IF NOT EXISTS warps (" +
                    "name TEXT PRIMARY KEY, " +
                    "world TEXT NOT NULL, " +
                    "x DOUBLE NOT NULL, " +
                    "y DOUBLE NOT NULL, " +
                    "z DOUBLE NOT NULL, " +
                    "yaw FLOAT NOT NULL, " +
                    "pitch FLOAT NOT NULL)"
                );
            }
        } catch (ClassNotFoundException | SQLException e) {
            plugin.getLogger().log(java.util.logging.Level.SEVERE,
                    "Failed to initialize SQLite database for warps!", e);
        }
    }

    private void loadWarps() {
        warpLocations.clear();
        if (connection == null) return;
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM warps")) {
            
            while (resultSet.next()) {
                String warpName = resultSet.getString("name");
                String worldName = resultSet.getString("world");
                World world = Bukkit.getWorld(worldName);
                
                if (world == null) {
                    plugin.getLogger().warning("World '" + worldName + "' not found for warp '" + warpName + "'!");
                    continue;
                }
                
                double x = resultSet.getDouble("x");
                double y = resultSet.getDouble("y");
                double z = resultSet.getDouble("z");
                float yaw = resultSet.getFloat("yaw");
                float pitch = resultSet.getFloat("pitch");
                
                Location location = new Location(world, x, y, z, yaw, pitch);
                warpLocations.put(warpName.toLowerCase(Locale.ROOT), location);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(java.util.logging.Level.SEVERE, "Failed to load warps from database!", e);
        }
    }

    public boolean createWarp(String name, Location location) {
        if (connection == null || name == null || name.isBlank() || location == null || location.getWorld() == null) {
            return false;
        }
        String lowerName = name.toLowerCase(Locale.ROOT);
        if (warpExists(lowerName)) {
            return false;
        }
        
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO warps (name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            statement.setString(1, lowerName);
            statement.setString(2, location.getWorld().getName());
            statement.setDouble(3, location.getX());
            statement.setDouble(4, location.getY());
            statement.setDouble(5, location.getZ());
            statement.setFloat(6, location.getYaw());
            statement.setFloat(7, location.getPitch());
            
            statement.executeUpdate();
            warpLocations.put(lowerName, location.clone());
            return true;
        } catch (SQLException e) {
            plugin.getLogger().log(java.util.logging.Level.SEVERE,
                    "Failed to create warp '" + name + "' in database!", e);
            return false;
        }
    }

    public boolean deleteWarp(String name) {
        if (connection == null || name == null || name.isBlank()) return false;
        String lowerName = name.toLowerCase(Locale.ROOT);
        if (!warpExists(lowerName)) {
            return false;
        }
        
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM warps WHERE name = ?")) {
            statement.setString(1, lowerName);
            statement.executeUpdate();
            warpLocations.remove(lowerName);
            return true;
        } catch (SQLException e) {
            plugin.getLogger().log(java.util.logging.Level.SEVERE,
                    "Failed to delete warp '" + name + "' from database!", e);
            return false;
        }
    }

    public Location getWarp(String name) {
        if (name == null) return null;
        Location location = warpLocations.get(name.toLowerCase(Locale.ROOT));
        return location == null ? null : location.clone();
    }

    public boolean warpExists(String name) {
        return name != null && warpLocations.containsKey(name.toLowerCase(Locale.ROOT));
    }

    public Set<String> getWarpNames() {
        return Collections.unmodifiableSet(Set.copyOf(warpLocations.keySet()));
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(java.util.logging.Level.WARNING,
                    "Failed to close SQLite database connection!", e);
        }
    }
}
