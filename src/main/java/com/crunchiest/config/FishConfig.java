package com.crunchiest.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.crunchiest.CrunchiestFishingPlugin;
import com.crunchiest.data.CustomFish;
import com.crunchiest.data.FishManager;

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
 * Manages the fish configuration file (fish.yml) for the plugin.
 * Responsible for loading, saving, and updating fish data from the configuration.
 */
public class FishConfig {

  private final CrunchiestFishingPlugin plugin;
  private final File fishConfigFile;
  private FileConfiguration fishConfig;

  /**
   * Constructs a FishConfig object responsible for handling the fish configuration.
   *
   * @param plugin The main plugin instance
   */
  public FishConfig(CrunchiestFishingPlugin plugin) {
      this.plugin = plugin;
      this.fishConfigFile = new File(plugin.getDataFolder(), "fish.yml");
      saveDefaultConfig();
  }

  /**
   * Reloads the fish configuration file and refreshes the FishManager's fish data.
   *
   * @param fishManager The FishManager instance to refresh after loading the config
   */
  public void reloadConfig(FishManager fishManager) {
      plugin.getLogger().info("Loading fish config...");
      this.fishConfig = YamlConfiguration.loadConfiguration(fishConfigFile);
      loadDefaultConfig("fish.yml", fishConfig);
      plugin.getLogger().info("Fish config loaded.");
      fishManager.refreshFishData(this);
  }

  /**
   * Retrieves the current fish configuration.
   *
   * @return The FileConfiguration representing the fish config
   */
  public FileConfiguration getFishConfig() {
      return this.fishConfig;
  }

  /**
   * Saves the default fish configuration from the resources folder if it doesn't already exist.
   */
  private void saveDefaultConfig() {
      saveResource("fish.yml", fishConfigFile);
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
   * Saves the fish configuration to the fish.yml file, logging an error message if it fails.
   *
   * @param errorMessage The error message to log if saving fails
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
   * Loads fish data from the fish.yml configuration file and returns a list of CustomFish objects.
   *
   * @return A list of CustomFish objects representing the fish data in the config
   */
  public List<CustomFish> loadFishData() {
      List<CustomFish> fishList = new ArrayList<>();

      if (fishConfig.isConfigurationSection("fish")) {
          for (String fishName : fishConfig.getConfigurationSection("fish").getKeys(false)) {
              String path = "fish." + fishName;

              try {
                  // Read fish attributes
                  double minLength = fishConfig.getDouble(path + ".minLength");
                  double maxLength = fishConfig.getDouble(path + ".maxLength");
                  double minWeight = fishConfig.getDouble(path + ".minWeight");
                  double maxWeight = fishConfig.getDouble(path + ".maxWeight");
                  int rarity = fishConfig.getInt(path + ".rarity");
                  List<String> description = fishConfig.getStringList(path + ".description");

                  // Read the entity type for the fish
                  String mobTypeName = fishConfig.getString(path + ".entityType", "COD"); // Default to COD if not specified
                  String dropMaterial = fishConfig.getString(path + ".dropMaterial", "COD");
                
                  // Create new CustomFish object and add it to the list
                  CustomFish customFish = new CustomFish(fishName, minLength, maxLength, minWeight, maxWeight, rarity, description, mobTypeName, dropMaterial);
                  plugin.getLogger().info(customFish.getMaterialType().toString());
                  fishList.add(customFish);
              } catch (IllegalArgumentException e) {
                  plugin.getLogger().severe("Invalid entity type for fish '" + fishName + "': " + e.getMessage());
              } catch (ClassCastException e) {
                  plugin.getLogger().severe("Type mismatch for fish '" + fishName + "': " + e.getMessage());
              } catch (Exception e) {
                  plugin.getLogger().severe("Error loading fish '" + fishName + "': " + e.getMessage());
              }
          }
          plugin.getLogger().info("Loaded " + fishList.size() + " fish from configuration.");
      } else {
          plugin.getLogger().warning("No fish section found in configuration.");
      }

      return fishList;
  }
}