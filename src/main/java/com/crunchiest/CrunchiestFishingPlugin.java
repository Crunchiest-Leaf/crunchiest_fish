package com.crunchiest;

import com.crunchiest.config.FishConfig;
import com.crunchiest.config.TreasureConfig;
import com.crunchiest.data.FishManager;
import com.crunchiest.data.TreasureManager;
import com.crunchiest.listener.InteractionListener;
import com.crunchiest.listener.FishingListener;
import org.bukkit.plugin.java.JavaPlugin;

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
 */

/**
 * The main class for the Crunchiest Fishing plugin, extending the JavaPlugin.
 * This class handles the plugin lifecycle and registers event listeners.
 */
public class CrunchiestFishingPlugin extends JavaPlugin {

    private FishingListener fishingListener;
    private InteractionListener interactionListener; // Listener for player interactions
    private FishConfig fishConfig;
    private FishManager fishManager;
    private TreasureManager treasureManager;
    private TreasureConfig treasureConfig;

    /**
     * Called when the plugin is enabled. Initializes listeners and registers them.
     */
    @Override
    public void onEnable() {
        // Initialize configurations
        this.fishConfig = new FishConfig(this);
        this.fishManager = new FishManager();
        fishConfig.reloadConfig(fishManager);

        this.treasureConfig = new TreasureConfig(this);
        this.treasureManager = new TreasureManager();
        treasureConfig.reloadConfig(treasureManager);

        // Initialize and register listeners
        this.fishingListener = new FishingListener(fishManager, this);
        this.interactionListener = new InteractionListener(fishingListener);
        getServer().getPluginManager().registerEvents(fishingListener, this);
        getServer().getPluginManager().registerEvents(interactionListener, this);
        getLogger().info("Crunchiest Fishing enabled!");
    }

    /**
     * Called when the plugin is disabled. Performs cleanup operations.
     */
    @Override
    public void onDisable() {
        // Clear any cached data
        if (fishingListener != null) {
            fishingListener.clearData();
        }
        getLogger().info("Crunchiest Fishing disabled!");
    }

    /**
     * Gets the FishConfig instance for accessing custom fish data.
     *
     * @return the FishConfig instance.
     */
    public FishConfig getFishConfig() {
        return fishConfig;
    }

    /**
     * Reloads the fish configuration and updates the fish manager.
     */
    public void reloadFishConfig() {
        fishConfig.reloadConfig(fishManager);
    }

    /**
     * Gets the TreasureManager instance for managing treasure-related operations.
     *
     * @return the TreasureManager instance.
     */
    public TreasureManager getTreasureManager() {
        return treasureManager;
    }
}