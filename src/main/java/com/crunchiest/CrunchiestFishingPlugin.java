package com.crunchiest;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;
import com.crunchiest.listener.InteractionListener;
import com.crunchiest.config.FishConfig;
import com.crunchiest.data.FishManager;
import com.crunchiest.listener.FishingListener;

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
* desc: For Fun Fishing overhaul Plugin!
*       work in progress!
* 
* link: https://github.com/Crunchiest-Leaf/crunchiest_fish
* 
*/

/**
 * The main class for the Crunchiest Fishing plugin, extending the JavaPlugin.
 * This class handles the plugin lifecycle and registers event listeners.
 */
public class CrunchiestFishingPlugin extends JavaPlugin {

    private FishingListener fishingListener;
    private InteractionListener interactionListener; // Declare the InteractionListener
    private FishConfig fishConfig;
    private FishManager fishManager;

    /**
     * Called when the plugin is enabled. Initializes listeners and registers them.
     */
    @Override
    public void onEnable() {
        this.fishConfig = new FishConfig(this);
        this.fishManager = new FishManager();
        fishConfig.reloadConfig(fishManager);

        fishingListener = new FishingListener(fishManager, this);
        // Initialize the InteractionListener and register it
        interactionListener = new InteractionListener(fishingListener);
        getServer().getPluginManager().registerEvents(fishingListener, this);
        getServer().getPluginManager().registerEvents(interactionListener, this);
        getLogger().info("Crunchiest Fishing enabled!");
    }

    /**
     * Called when the plugin is disabled. Performs cleanup operations.
     */
    @Override
    public void onDisable() {
        // Any necessary cleanup can be done here
        if (fishingListener != null) {
            fishingListener.clearData(); // Clear any cached data
        }
        getLogger().info("Crunchiest Fishing Disabled!");
    }

  /**
   * Get the FishConfig instance for accessing custom fish data.
   * 
   * @return the FishConfig instance.
   */
  public FishConfig getFishConfig() {
      return fishConfig;
  }

      // Method to reload the fish configuration and update the fish manager
      public void reloadFishConfig() {
        fishConfig.reloadConfig(fishManager);
      }
}