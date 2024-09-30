package com.crunchiest.listener;

import com.crunchiest.CrunchiestFishingPlugin;
import com.crunchiest.data.CustomFish;
import com.crunchiest.data.FishManager;
import com.crunchiest.data.FishingData;
import com.crunchiest.session.FishingSession;
import com.crunchiest.util.FishingConstants;
import com.crunchiest.util.SoundUtil;
import java.util.Optional;

import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

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
 * Listens for player fishing events and manages the associated fishing data and sessions.
 */
public class FishingListener implements Listener {

    private final CrunchiestFishingPlugin plugin;
    private final FishManager fishManager;

    // ConcurrentHashMap to store FishingData for each player by their UUID
    private final ConcurrentHashMap<UUID, FishingData> fishingDataMap = new ConcurrentHashMap<>();

    /**
     * Constructs a FishingListener for managing fishing events.
     *
     * @param fishManager the FishManager instance for managing fish data
     * @param plugin      the plugin instance for handling events and scheduling
     */
    public FishingListener(FishManager fishManager, CrunchiestFishingPlugin plugin) {
        this.fishManager = fishManager;
        this.plugin = plugin;
    }

    // ------------------------------------------------------------------------
    // EVENT HANDLERS
    // ------------------------------------------------------------------------

    /**
     * Handles the PlayerFishEvent, managing the fishing state for the player.
     *
     * @param event the PlayerFishEvent triggered by a player
     */
    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        FishingData data = fishingDataMap.computeIfAbsent(playerUUID, k -> new FishingData(playerUUID));

        switch (event.getState()) {
            case CAUGHT_FISH:
                event.setCancelled(true); // Cancel automatic fish catch
                break;

            case BITE:
                handleFishBite(event, player, data);
                break;

            case FISHING:
                sendMessage(player, "You cast your fishing rod. Wait for a bite!");
                break;

            case REEL_IN:
                handleReelIn(data);
                break;

            default:
                break;
        }
    }
private void handleFishBite(PlayerFishEvent event, Player player, FishingData data) {
    // Handle the Optional<CustomFish> returned by createRandomFish()
    Optional<CustomFish> optionalCaughtFish = fishManager.createRandomFish();
    
    if (optionalCaughtFish.isPresent()) {
        CustomFish caughtFish = optionalCaughtFish.get(); // Get the fish if present
        long reelTime = ThreadLocalRandom.current()
                .nextLong(FishingConstants.MIN_REEL_TIME_MS, FishingConstants.MAX_REEL_TIME_MS);
        
        // Start fishing with the caught fish
        data.startFishing(caughtFish, reelTime, event.getHook());
        new FishingSession(player, data, plugin).startReelSession(); // Start reel session
    } else {
        // Optional feedback if no fish is available
        sendMessage(player, "No fish available right now!"); // You may need to define sendMessage if not already defined
    }
}

    private void handleReelIn(FishingData data) {
        if (data.isFishEscaped()) {
            data.setFishEscaped(false); // Reset escape status
        }
        data.setCanReel(false);
    }

    /**
     * Handles the PlayerQuitEvent to remove player data from fishingDataMap.
     *
     * @param event the PlayerQuitEvent triggered when a player leaves the server
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removePlayerData(event.getPlayer());
    }

    /**
     * Handles the PlayerKickEvent to remove player data from fishingDataMap.
     *
     * @param event the PlayerKickEvent triggered when a player is kicked from the server
     */
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        removePlayerData(event.getPlayer());
    }

    private void removePlayerData(Player player) {
        UUID playerUUID = player.getUniqueId();
        fishingDataMap.remove(playerUUID);
        plugin.getLogger().info("Removed fishing data for player: " + player.getName());
    }

    // ------------------------------------------------------------------------
    // PLAYER FISHING HANDLING
    // ------------------------------------------------------------------------

    /**
     * Handles the player clicking action during the fishing session.
     *
     * @param playerUUID the unique ID of the player
     * @param player     the player object associated with the UUID
     */
    public void handlePlayerClick(UUID playerUUID, Player player) {
        FishingData data = fishingDataMap.get(playerUUID);
        if (data != null) {
            // Update data if the click count reaches the target
            if (data.getClickCount() == data.getTargetClicks() - 1) {
                data.updateLastReelClickTime();
            }

            // Update boss bars during reeling
            if (data.getClickCount() <= data.getTargetClicks()) {
                data.incrementClickCount(); // Increment click count
                SoundUtil.playClickSound(player); // Play click sound

                // Move the hook closer to the player
                FishHook hook = data.getFishingHook();
                if (hook != null) {
                    moveFishingHookCloser(hook, player);
                }
            } else {
                sendMessage(player, "You missed the reel timing!"); // Optional user feedback
            }
        }
    }

    // ------------------------------------------------------------------------
    // UTILITIES
    // ------------------------------------------------------------------------

    /**
     * Moves the fishing hook closer to the player using velocity.
     *
     * @param hook   The FishingHook entity to move.
     * @param player The player whose fishing hook is being moved.
     */
    private void moveFishingHookCloser(FishHook hook, Player player) {
        // Get the current location of the hook
        Location hookLocation = hook.getLocation();
        Location playerLocation = player.getLocation();

        // If hook is already too close, do nothing
        if (playerLocation.distance(hookLocation) < 0.5) return;

        // Calculate the direction from the hook to the player
        Vector direction = playerLocation.toVector().subtract(hookLocation.toVector()).normalize();

        // Apply velocity to the hook towards the player
        Vector velocity = direction.multiply(0.2); // Adjust multiplier to control speed
        hook.setVelocity(velocity);
    }

    /**
     * Sends messages to the specified player.
     *
     * @param player   the player to send messages to
     * @param messages the messages to send to the player
     */
    private void sendMessage(Player player, String... messages) {
        for (String message : messages) {
            player.sendMessage(message);
        }
    }

    // ------------------------------------------------------------------------
    // DATA MANAGEMENT
    // ------------------------------------------------------------------------

    /**
     * Clears all fishing data for players.
     */
    public void clearData() {
        fishingDataMap.clear();
    }

    /**
     * Retrieves the FishingData for a specified player.
     *
     * @param playerUUID the unique ID of the player
     * @return the FishingData associated with the player, or null if not found
     */
    public FishingData getFishingData(UUID playerUUID) {
        return fishingDataMap.get(playerUUID);
    }
}