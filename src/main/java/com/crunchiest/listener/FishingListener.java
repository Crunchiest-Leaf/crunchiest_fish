package com.crunchiest.listener;

import com.crunchiest.data.FishingData;
import com.crunchiest.CrunchiestFishingPlugin;
import com.crunchiest.data.Fish;
import com.crunchiest.util.FishingConstants;
import com.crunchiest.session.FishingSession;
import com.crunchiest.util.SoundUtil;

import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
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
* desc: For Fun Fishing overhaul Plugin!
*       work in progress!
* 
* link: https://github.com/Crunchiest-Leaf/crunchiest_fish
* 
*/

/**
 * Listens for player fishing events and manages the associated fishing data and sessions.
 */
public class FishingListener implements Listener {

    private final CrunchiestFishingPlugin plugin;
    private final ConcurrentHashMap<UUID, FishingData> fishingDataMap = new ConcurrentHashMap<>();

    /**
     * Constructs a FishingListener for managing fishing events.
     *
     * @param plugin the plugin instance for handling events and scheduling
     */
    public FishingListener(CrunchiestFishingPlugin plugin) {
        this.plugin = plugin;
    }

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
                Fish caughtFish = Fish.createRandomFish(); // Encapsulated fish creation
                long reelTime = ThreadLocalRandom.current().nextLong(FishingConstants.MIN_REEL_TIME_MS, FishingConstants.MAX_REEL_TIME_MS);
                data.startFishing(caughtFish, reelTime, getPlayerHook(player));
                new FishingSession(player, data, plugin).startReelSession(); // Delegate the reel session
                break;

            case FISHING:
                sendMessage(player, "You cast your fishing rod. Wait for a bite!");
                break;

            case REEL_IN:
                if (data.isFishEscaped()) {
                    data.setFishEscaped(false);
                }
                break;

            default:
                break;
        }
    }

    /**
     * Handles the player clicking action during the fishing session.
     *
     * @param playerUUID the unique ID of the player
     * @param player the player object associated with the UUID
     */
    public void handlePlayerClick(UUID playerUUID, Player player) {
        FishingData data = fishingDataMap.get(playerUUID);
        if (data != null) {
            data.incrementClickCount();
            SoundUtil.playClickSound(player); // Play click sound

            // Move the hook closer to the player
            FishHook hook = data.getFishingHook(); // Ensure you're getting the right hook from FishingData
            if (hook != null) {
                moveFishingHookCloser(hook, player);
            }

            // If the click count matches the target clicks, update the last click time
            if (data.getClickCount() == data.getTargetClicks()) {
                data.updateLastReelClickTime();
            }

            // Update the boss bars only if the click count is less than the target clicks
            if (data.getClickCount() <= data.getTargetClicks()) {
                // Calculate elapsed time using the correct reel start time
                long elapsedTime = System.currentTimeMillis() - data.getReelStartTime();

                // Get the total reel time
                long totalReelTime = data.getReelTime();

                // Call the updateFishingBars method from the FishingSession
                FishingSession fishingSession = data.getFishingSession();
                if (fishingSession != null) {
                    fishingSession.updateFishingBars(elapsedTime, totalReelTime, data.getTargetClicks());
                }
            }
        }
    }

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

    /**
     * Sends messages to the specified player.
     *
     * @param player the player to send messages to
     * @param messages the messages to send to the player
     */
    private void sendMessage(Player player, String... messages) {
        for (String message : messages) {
            player.sendMessage(message);
        }
    }

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

  // Calculate the direction from the hook to the player
  Vector direction = playerLocation.toVector().subtract(hookLocation.toVector()).normalize();

  // Determine the velocity to apply (adjust the multiplier as needed)
  Vector velocity = direction.multiply(0.2); // Adjust this multiplier to control speed

  // Set the hook's velocity to move it towards the player
  hook.setVelocity(velocity);
}

    /**
     * Checks if the given location is on solid ground (not water).
     *
     * @param location The location to check.
     * @return true if the location is on land, false if it's water or air.
     */
    private boolean isLocationOnLand(Location location) {
        // Check the block at the new location
        Material blockType = location.getBlock().getType();
        // Return true if the block is a solid block (not water)
        return blockType.isSolid() && blockType != Material.WATER;
    }

    private FishHook getPlayerHook(Player player) {
        return player.getWorld().getEntitiesByClass(FishHook.class).stream()
                .filter(hook -> hook.getShooter().equals(player)).findFirst().orElse(null);
    }
}