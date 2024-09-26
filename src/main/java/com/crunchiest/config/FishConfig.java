package com.crunchiest.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.crunchiest.CrunchiestFishingPlugin;
import com.crunchiest.data.Fish;
import com.crunchiest.data.FishManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * FishConfigManager:
 * Handles loading of fish.yml config data.
 */
public class FishConfig {

    private final CrunchiestFishingPlugin plugin;
    private final File fishConfigFile;
    private FileConfiguration fishConfig;

    /**
     * FishConfigManager:
     * Handles loading of fish.yml config data.
     *
     * @param plugin main plugin instance.
     */
    public FishConfig(CrunchiestFishingPlugin plugin) {
        this.plugin = plugin;
        this.fishConfigFile = new File(plugin.getDataFolder(), "fish.yml");
        saveDefaultConfig();
    }

    /**
     * reloadConfig: 
     * Reloads the fish configuration file.
     */
    public void reloadConfig(FishManager fishManager) {
        plugin.getLogger().info("Loading fish config...");
        this.fishConfig = YamlConfiguration.loadConfiguration(fishConfigFile);
        loadDefaultConfig("fish.yml", fishConfig);
        plugin.getLogger().info("Fish config loaded.");
        fishManager.refreshFishData(this);
    }

    /**
     * getFishConfig:
     * Retrieves the fish configuration.
     *
     * @return Fish configuration
     */
    public FileConfiguration getFishConfig() {
        return this.fishConfig;
    }

    /**
     * saveDefaultConfig: 
     * Saves the default fish configuration from resources if it doesn't exist.
     */
    private void saveDefaultConfig() {
        saveResource("fish.yml", fishConfigFile);
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
     * saveFishConfig:
     * Saves the fish configuration file.
     *
     * @param errorMessage Error message to log in case of failure
     */
    public void saveFishConfig(String errorMessage) {
        if (fishConfig != null && fishConfigFile != null) {
            try {
                fishConfig.save(fishConfigFile);
            } catch (IOException e) {
                plugin.getLogger().severe(errorMessage);
                e.printStackTrace();
            }
        }
    }

    /**
     * loadFishData:
     * Loads fish data from the fish.yml configuration file.
     *
     * @return List of Fish objects
     */
    public List<Fish> loadFishData() {
        List<Fish> fishList = new ArrayList<>();
        
        if (fishConfig.isConfigurationSection("fish")) {
            for (String fishName : fishConfig.getConfigurationSection("fish").getKeys(false)) {
                String path = "fish." + fishName;

                // Read fish attributes
                double minLength = fishConfig.getDouble(path + ".minLength");
                double maxLength = fishConfig.getDouble(path + ".maxLength");
                double minWeight = fishConfig.getDouble(path + ".minWeight");
                double maxWeight = fishConfig.getDouble(path + ".maxWeight");
                int rarity = fishConfig.getInt(path + ".rarity");
                List<String> description = fishConfig.getStringList(path + ".description");

                // Create new Fish object and add to the list
                Fish customFish = new Fish(fishName, minLength, maxLength, minWeight, maxWeight, rarity, description);
                fishList.add(customFish);
            }
            plugin.getLogger().info("Loaded " + fishList.size() + " fish from configuration.");
        } else {
            plugin.getLogger().warning("No fish section found in configuration.");
        }

        return fishList;
    }
}