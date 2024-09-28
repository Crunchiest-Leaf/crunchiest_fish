package com.crunchiest.config;

import com.crunchiest.CrunchiestFishingPlugin;
import com.crunchiest.data.CustomTreasure;
import com.crunchiest.data.TreasureManager;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * TreasureConfig:
 * Handles loading of treasures.yml config data.
 */
public class TreasureConfig {

    private final CrunchiestFishingPlugin plugin;
    private final File treasureConfigFile;
    private FileConfiguration treasureConfig;

    /**
     * TreasureConfig:
     * Handles loading of treasures.yml config data.
     *
     * @param plugin main plugin instance.
     */
    public TreasureConfig(CrunchiestFishingPlugin plugin) {
        this.plugin = plugin;
        this.treasureConfigFile = new File(plugin.getDataFolder(), "treasures.yml");
        saveDefaultConfig();
    }

    /**
     * reloadConfig:
     * Reloads the treasure configuration file.
     */
    public void reloadConfig(TreasureManager treasureManager) {
        plugin.getLogger().info("Loading treasure config...");
        this.treasureConfig = YamlConfiguration.loadConfiguration(treasureConfigFile);
        loadDefaultConfig("treasures.yml", treasureConfig);
        plugin.getLogger().info("Treasure config loaded.");
        treasureManager.refreshTreasureData(this);
    }

    /**
     * getTreasureConfig:
     * Retrieves the treasure configuration.
     *
     * @return Treasure configuration
     */
    public FileConfiguration getTreasureConfig() {
        return this.treasureConfig;
    }

    /**
     * saveDefaultConfig:
     * Saves the default treasure configuration from resources if it doesn't exist.
     */
    private void saveDefaultConfig() {
        saveResource("treasures.yml", treasureConfigFile);
    }

    /**
     * saveResource:
     * Saves a default configuration file from the plugin resources.
     *
     * @param resourcePath Path to the resource in the plugin JAR
     * @param file         File to save the resource to
     */
    private void saveResource(String resourcePath, File file) {
        if (!file.exists()) {
            plugin.saveResource(resourcePath, false);
        }
    }

    /**
     * loadDefaultConfig:
     * Loads default configuration from resources if not already existing.
     *
     * @param fileName   Name of the file to load
     * @param config     Configuration to load into
     */
    private void loadDefaultConfig(String fileName, FileConfiguration config) {
        InputStream inputStream = plugin.getResource(fileName);
        if (inputStream != null) {
            YamlConfiguration defaultConfig =
                YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
            config.setDefaults(defaultConfig);
        }
    }

    /**
     * saveTreasureConfig:
     * Saves the treasure configuration file.
     *
     * @param errorMessage Error message to log in case of failure
     */
    public void saveTreasureConfig(String errorMessage) {
        if (treasureConfig != null && treasureConfigFile != null) {
            try {
                treasureConfig.save(treasureConfigFile);
            } catch (IOException e) {
                plugin.getLogger().severe(errorMessage);
                e.printStackTrace();
            }
        }
    }

    /**
     * loadTreasureData:
     * Loads treasure data from the treasures.yml configuration file.
     *
     * @return List of CustomTreasure objects
     */
    public List<CustomTreasure> loadTreasureData() {
        List<CustomTreasure> treasureList = new ArrayList<>();

        if (treasureConfig.isConfigurationSection("treasures")) {
            for (String treasureName : treasureConfig.getConfigurationSection("treasures").getKeys(false)) {
                String path = "treasures." + treasureName;

                try {
                    // Read treasure attributes
                    List<String> description = treasureConfig.getStringList(path + ".description");
                    String materialName = treasureConfig.getString(path + ".material", "GOLD_NUGGET"); // Default to GOLD_NUGGET if not specified
                    Material material = Material.valueOf(materialName.toUpperCase());
                    int rarity = treasureConfig.getInt(path + ".rarity");

                    // Create new Treasure object and add to the list
                    CustomTreasure customTreasure = new CustomTreasure(treasureName, description, material, rarity);
                    treasureList.add(customTreasure);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().severe("Invalid material for treasure '" + treasureName + "': " + e.getMessage());
                } catch (ClassCastException e) {
                    plugin.getLogger().severe("Type mismatch for treasure '" + treasureName + "': " + e.getMessage());
                } catch (Exception e) {
                    plugin.getLogger().severe("Error loading treasure '" + treasureName + "': " + e.getMessage());
                }
            }
            plugin.getLogger().info("Loaded " + treasureList.size() + " treasures from configuration.");
        } else {
            plugin.getLogger().warning("No treasures section found in configuration.");
        }

        return treasureList;
    }
}