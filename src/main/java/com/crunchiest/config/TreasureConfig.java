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

/*
* CRUNCHIEST FISHING
*   ____ ____  _   _ _   _  ____ _   _ ___ _____ ____ _____   _____ ___ ____  _   _ ___ _   _  ____ 
*  / ___|  _ \| | | | \ | |/ ___| | | |_ _| ____/ ___|_   _| |  ___|_ _/ ___|| | | |_ _| \ | |/ ___|
* | |   | |_) | | | |  \| | |   | |_| || ||  _| \___ \ | |   | |_   | |\___ \| |_| || ||  \| | |  _ 
* | |___|  _ <| |_| | |\  | |___|  _  || || |___ ___) || |   |  _|  | | ___) |  _  || || |\  | |_| |
*  \____|_| \_\\___/|_| \_|\____|_| |_|___|_____|____/ |_|   |_|   |___|____/|_| |_|___|_| \_|\____|
*
* Author: Crunchiest_Leaf
* 
* Description: A fun fishing overhaul plugin. Still a work in progress!
* GitHub: https://github.com/Crunchiest-Leaf/crunchiest_fish
*/

/**
 * Manages the treasure configuration file (treasures.yml) for the plugin.
 * Responsible for loading, saving, and updating treasure data from the configuration.
 */
public class TreasureConfig {

    private final CrunchiestFishingPlugin plugin;
    private final File treasureConfigFile;
    private FileConfiguration treasureConfig;

    /**
     * Constructs a TreasureConfig object responsible for handling the treasure configuration.
     *
     * @param plugin The main plugin instance
     */
    public TreasureConfig(CrunchiestFishingPlugin plugin) {
        this.plugin = plugin;
        this.treasureConfigFile = new File(plugin.getDataFolder(), "treasures.yml");
        saveDefaultConfig();
    }

    /**
     * Reloads the treasure configuration file and refreshes the TreasureManager's treasure data.
     *
     * @param treasureManager The TreasureManager instance to refresh after loading the config
     */
    public void reloadConfig(TreasureManager treasureManager) {
        plugin.getLogger().info("Loading treasure config...");
        this.treasureConfig = YamlConfiguration.loadConfiguration(treasureConfigFile);
        loadDefaultConfig("treasures.yml", treasureConfig);
        plugin.getLogger().info("Treasure config loaded.");
        treasureManager.refreshTreasureData(this);
    }

    /**
     * Retrieves the current treasure configuration.
     *
     * @return The FileConfiguration representing the treasure config
     */
    public FileConfiguration getTreasureConfig() {
        return this.treasureConfig;
    }

    /**
     * Saves the default treasure configuration from the resources folder if it doesn't already exist.
     */
    private void saveDefaultConfig() {
        saveResource("treasures.yml", treasureConfigFile);
    }

    /**
     * Saves a configuration file from the plugin's resources folder to the specified file.
     *
     * @param resourcePath The path to the resource file in the plugin's JAR
     * @param file The file to save the resource to
     */
    private void saveResource(String resourcePath, File file) {
        if (!file.exists()) {
            plugin.saveResource(resourcePath, false);
        }
    }

    /**
     * Loads the default configuration from the plugin's resources into the specified configuration.
     *
     * @param fileName The name of the resource file to load
     * @param config The configuration object to load defaults into
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
     * Saves the treasure configuration to the treasures.yml file, logging an error message if it fails.
     *
     * @param errorMessage The error message to log if saving fails
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
     * Loads treasure data from the treasures.yml configuration file and returns a list of CustomTreasure objects.
     *
     * @return A list of CustomTreasure objects representing the treasure data in the config
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

                    // Create new CustomTreasure object and add it to the list
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