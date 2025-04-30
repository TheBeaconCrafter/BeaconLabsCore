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
import java.util.HashMap;
import java.util.HashSet;
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
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
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
            plugin.getLogger().severe("Failed to initialize SQLite database for warps!");
            e.printStackTrace();
        }
    }

    private void loadWarps() {
        warpLocations.clear();
        
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
                warpLocations.put(warpName.toLowerCase(), location);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to load warps from database!");
            e.printStackTrace();
        }
    }

    public boolean createWarp(String name, Location location) {
        String lowerName = name.toLowerCase();
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
            warpLocations.put(lowerName, location);
            return true;
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to create warp '" + name + "' in database!");
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteWarp(String name) {
        String lowerName = name.toLowerCase();
        if (!warpExists(lowerName)) {
            return false;
        }
        
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM warps WHERE name = ?")) {
            statement.setString(1, lowerName);
            statement.executeUpdate();
            warpLocations.remove(lowerName);
            return true;
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to delete warp '" + name + "' from database!");
            e.printStackTrace();
            return false;
        }
    }

    public Location getWarp(String name) {
        return warpLocations.get(name.toLowerCase());
    }

    public boolean warpExists(String name) {
        return warpLocations.containsKey(name.toLowerCase());
    }

    public Set<String> getWarpNames() {
        return warpLocations.keySet();
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to close SQLite database connection!");
            e.printStackTrace();
        }
    }
}
