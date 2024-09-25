package com.crunchiest;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class for the Crunchiest Fishing plugin, extending the JavaPlugin.
 * This class handles the plugin lifecycle and registers event listeners.
 */
public class CrunchiestFishingPlugin extends JavaPlugin {

    private FishingListener fishingListener;
    private InteractionListener interactionListener; // Declare the InteractionListener

    /**
     * Called when the plugin is enabled. Initializes listeners and registers them.
     */
    @Override
    public void onEnable() {
        // Initialize the FishingListener and register it
        fishingListener = new FishingListener(this);
        getServer().getPluginManager().registerEvents(fishingListener, this);

        // Initialize the InteractionListener and register it
        interactionListener = new InteractionListener(fishingListener);
        getServer().getPluginManager().registerEvents(interactionListener, this);
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
    }
}