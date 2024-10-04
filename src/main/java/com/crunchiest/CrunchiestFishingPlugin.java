package com.crunchiest;

import com.crunchiest.config.FishConfig;
import com.crunchiest.config.TreasureConfig;
import com.crunchiest.data.FishManager;
import com.crunchiest.data.TreasureManager;
import com.crunchiest.listener.InteractionListener;

import net.md_5.bungee.api.ChatColor;

import com.crunchiest.listener.FishingListener;
import com.crunchiest.command.ReloadCommand;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

/*
 * CRUNCHIEST FISHING
 *   ____ ____  _   _ _   _  ____ _   _ ___ _____ ____ _____   _____ ___ ____  _   _ ___ _   _  ____ 
 *  / ___|  _ \| | | | \ | |/ ___| | | |_ _| ____/ ___|_   _| |  ___|_ _/ ___|| | | |_ _| \ | |/ ___|
 * | |   | |_) | | | |  \| | |   | |_| || ||  _| \___ \ | |   | |_   | |\___ \| |_| || ||  \| | |  _ 
 * | |___|  _ <| |_| | |\  | |___|  _  || || |___ ___) || |   |  _|  | | ___) |  _  || || |\  | |_| |
 *  \____|_| \_\\___/|_| \_|\____|_| |_|___|_____|____/ |_|   |_|   |___|____/|_| |_|___|_| \_|\____|
 * 
 *     ⠀⠀⠀⠀⠀⠀⠀
 *                       ⠀⠀⠀⠀⠀
 *          ⣶⣄⠀⠀⢀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀*⠀⠀⠀   ⠀⠀⢸⣿⣿⣷⣴⣿⡄⠀⠀⠀⠀⠀⢀⡀⠀
⠀*⠀⠀⠀   ⠰⣶⣾⣿⣿⣿⣿⣿⡇⠀⢠⣷⣤⣶⣿⡇⠀
⠀*⠀⠀⠀   ⠀⠙⣿⣿⣿⣿⣿⣿⣿⣀⣿⣿⣿⣿⣿⣧⣀
⠀*⠀   ⣷⣦⣀⠘⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠃
 * ⢲⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠁⠀
⠀*  ⠙⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡟⠁⠀⠀⠀                  ⣀⣤⣴⣶⣾⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀*  ⠚⠻⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠿⠿⠂⠀⠀⠀            ⣀⣤⣶⣿⣿⣿⣿⣿⣿⣿⣿⡄⠀⠀⠀⠀⠀⠀⠀
⠀*⠀⠀⠀⠀⠉⠙⢻⣿⣿⡿⠛⠉⡇⠀⠀⠀⠀⠀⠀⠀           ⢀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⠿⠿⠿⠛⠓⣶⣶⢶⣶⣶⣶⣶⠶⣤⣄⡀⠀⠀⠀⠀⠀
⠀*⠀⠀⠀⠀⠀⠀⠘⠋⠁⠀⠀⠀⠸⡄⠀⠀⠀⠀⠀⠀        ⢀⣴⣿⣿⣿⣿⡿⠟⠋⢉⣁⣠⣤⣴⡶⠀⡴⣿⣿⣿⣿⣿⣿⡏⣙⣿⡷⠋⢉⠓⢦⣄⠀⠀
⠀*⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢳⡀           ⣴⣿⣿⣿⠿⠋⣁⣤⣶⣿⣿⣿⣿⣿⠏⠀⣼⣽⣿⣿⣿⡿⠿⠿⣿⣿⠏⡠⠚⣡⣶⣶⣾⣿⠀
 *                       ⠀⠀⢀⣾⣿⣿⠟⣡⣶⣿⠿⠛⠁⠀⠘⣿⣿⡟⠀⢸⣧⣿⡟⠉⠀⠀⠀⠀⠀⠀⡜⢀⣾⣿⣿⣿⣿⠟⠀
 *                       ⠀⢠⣿⣿⡟⣡⣾⣿⣟⣥⡶⠒⠊⢉⣡⢿⣿⣧⠀⢸⣿⠏⠀⠀⠀⠀⠀⠀⠀⠈⢀⣾⡿⠿⠿⠿⠯⢤⡄
 *                       ⢀⣿⣿⡟⣼⣿⠟⠹⣿⣏⠀⠠⠒⣉⠔⣫⠽⡿⣆⠈⣯⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⣀⣤⠖⠒⠒⡂⢠⡇
 *                       ⢸⣿⡟⣸⣿⠏⠀⠀⠘⢿⣷⡀⠈⠤⠊⢁⡼⠁⣈⣷⣾⣷⣄⠀⠀⠐⠢⢴⣶⡶⠯⢷⡫⢔⣥⣮⠾⠋⠀
 *                       ⣿⣿⡇⣿⡟⠀⠀⠀⠀⠀⠙⠻⢷⣦⡶⣿⣷⣿⣿⣿⣿⣿⣿⠿⠿⠖⠒⠒⠒⠒⠚⠛⠋⠉⠉⠀⠀⠀⠀
 *                       ⢻⣿⠸⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⢀⣾⣿⣿⣿⡿⠛⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
 *                       ⠘⣿⠀⣿⣇⠀⠀⠀⠀⠀⠀⠀⠀⣾⣿⣿⡟⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
 *                       ⠀⠹⣇⠸⣿⡀⠀⠀⠀⠀⠀⠀⠘⣿⣿⢿⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
 *                       ⠀⠀⠻⣆⢻⣧⠀⠀⠀⠀⠀⠀⠀⢻⣿⠘⢷⣄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣠⣤⡀⠀⠀⠀⠀
 *                       ⠀⠀⠀⠙⣦⡹⣷⡀⠀⠀⠀⠀⠀⠀⠹⣆⠀⠙⠷⣤⣀⠀⠀⠀⠀⠀⠀⠀⠀⣠⡶⠋⡀⠀⢿⠀⠀⠀⠀
 *                       ⠀⠀⠀⠀⠈⠛⢮⣷⣤⠀⠀⠀⠀⠀⠀⠈⠀⠀⠀⠀⠉⠛⠶⢤⣤⣤⡴⠖⠋⣁⣤⠞⢀⠀⡿⠀⠀⠀⠀
 *                       ⠀⠀⠀⠀⠀⠀⠀⠈⠙⠳⢦⣤⣀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠉⠻⣟⣛⣩⣥⠖⠁⢰⠇⠀⠀⠀⠀
 *                       ⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡔⢫⣿⡟⡟⠛⠶⠶⠦⠤⠤⣤⣤⣤⣤⣤⣼⠿⠯⠭⠤⠔⠊⣿⠀⠀⠀⠀⠀
 *                       ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠛⠷⣬⣿⡾⠃⠀⠀⠀⠀⠀⠀⠀⠻⣦⡀⠹⣯⡻⠶⠦⠤⠤⠄⢿⡀⠀⠀⠀⠀
 *                       ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠻⢦⣘⠻⣷⣤⣄⣀⠀⠈⢷⡀⠀⠀⠀
 *                       ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠓⠶⣬⣉⣉⠉⠀⢀⣿⠀⠀⠀
 *                       ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠉⠉⠉⠁⠀⠀⠀
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
    public Logger logger = getLogger();

    /**
     * Called when the plugin is enabled. Initializes listeners and registers them.
     */
    @Override
    public void onEnable() {
        // Initialize configurations
        logger.info("Initialising Crunchiest fishing...");
        this.fishConfig = new FishConfig(this);
        this.fishManager = new FishManager(logger);
        fishConfig.reloadConfig(fishManager);

        this.treasureConfig = new TreasureConfig(this);
        this.treasureManager = new TreasureManager(logger);
        treasureConfig.reloadConfig(treasureManager);

        // Initialize and register listeners
        this.fishingListener = new FishingListener(fishManager, this);
        this.interactionListener = new InteractionListener(fishingListener);
        getServer().getPluginManager().registerEvents(fishingListener, this);
        getServer().getPluginManager().registerEvents(interactionListener, this);
        logger.info("Crunchiest Fishing enabled!");

        // Initialise and register commands
        this.getCommand("fishingreload").setExecutor(new ReloadCommand(this));
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
        logger.info("Crunchiest Fishing disabled!");
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
        logger.info(ChatColor.DARK_GREEN + "Reloading Fish Config!");
        fishConfig.reloadConfig(fishManager);
        logger.info(ChatColor.DARK_GREEN + "Reloaded Fish Config!");
    }

    
    /**
     * Reloads the fish configuration and updates the fish manager.
     */
    public void reloadTreasureConfig() {
      logger.info(ChatColor.DARK_GREEN + "Reloading Treasure Config!");
      treasureConfig.reloadConfig(treasureManager);
      logger.info(ChatColor.DARK_GREEN + "Reloaded Treasure Config!");
    }

    public void reloadAllConfigs() {
      logger.info(ChatColor.BLUE + "--");
      reloadFishConfig();
      reloadTreasureConfig();
      logger.info("--");
      logger.info(ChatColor.BLUE + "--");
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