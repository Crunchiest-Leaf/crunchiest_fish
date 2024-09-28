package com.crunchiest.data;

import com.crunchiest.session.FishingSession;
import java.util.UUID;
import org.bukkit.entity.FishHook;

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
 * Represents the fishing data for a player, including their caught fish,
 * reel time, click count, and session details.
 */
public class FishingData {
    private final UUID playerUUID;
    private CustomFish caughtFish;
    private long reelTime; // Time allocated to reel the fish
    private long reelStartTime;
    private boolean fishEscaped;
    private int clickCount;
    private int targetClicks;
    private boolean canReel; // Flag to track whether the player can reel
    private long lastReelClickTime;
    private FishingSession fishingSession; // Reference to the FishingSession
    private FishHook hook;

    /**
     * Constructs a FishingData object for a player with a given UUID.
     *
     * @param playerUUID the UUID of the player
     */
    public FishingData(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.clickCount = 0;
        this.fishEscaped = false;
        this.canReel = false;
    }
    

    /**
     * Returns the UUID of the player.
     *
     * @return the player's UUID
     */
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    /**
     * Returns the fish that has been caught.
     *
     * @return the caught fish
     */
    public CustomFish getCaughtFish() {
        return caughtFish;
    }

    /**
     * Sets the caught fish.
     *
     * @param fish the fish to set as caught
     */
    public void setCaughtFish(CustomFish fish) {
        this.caughtFish = fish;
    }

    /**
     * Returns the total reel time allocated.
     *
     * @return the reel time in milliseconds
     */
    public long getReelTime() {
        return reelTime;
    }

    /**
     * Sets the total reel time allocated.
     *
     * @param reelTime the reel time to set in milliseconds
     */
    public void setReelTime(long reelTime) {
        this.reelTime = reelTime;
    }

    /**
     * Returns the start time of the reeling process.
     *
     * @return the reel start time in milliseconds
     */
    public long getReelStartTime() {
        return reelStartTime; // Return reelStartTime instead of reelTime
    }

    /**
     * Sets the start time of the reeling process.
     *
     * @param reelStartTime the start time to set in milliseconds
     */
    public void setReelStartTime(long reelStartTime) {
        this.reelStartTime = reelStartTime;
    }

    /**
     * Checks if the fish has escaped.
     *
     * @return true if the fish has escaped; false otherwise
     */
    public boolean isFishEscaped() {
        return fishEscaped;
    }

    /**
     * Sets the escape status of the fish.
     *
     * @param fishEscaped true if the fish has escaped; false otherwise
     */
    public void setFishEscaped(boolean fishEscaped) {
        this.fishEscaped = fishEscaped;
    }

    /**
     * Resets the click count to zero.
     */
    public void resetClickCount() {
        this.clickCount = 0;
    }

    /**
     * Increments the click count by one.
     */
    public void incrementClickCount() {
        this.clickCount++;
    }

    /**
     * Returns the current click count.
     *
     * @return the number of clicks
     */
    public int getClickCount() {
        return clickCount;
    }

    /**
     * Sets the target number of clicks required to reel in the fish.
     *
     * @param targetClicks the target clicks to set
     */
    public void setTargetClicks(int targetClicks) {
        this.targetClicks = targetClicks;
    }

    /**
     * Returns the target number of clicks required to reel in the fish.
     *
     * @return the target clicks
     */
    public int getTargetClicks() {
        return targetClicks;
    }

    /**
     * Checks if the player can reel in the fish.
     *
     * @return true if the player can reel; false otherwise
     */
    public boolean canReel() {
        return canReel;
    }

    /**
     * Sets the reeling capability for the player.
     *
     * @param canReel true if the player can reel; false otherwise
     */
    public void setCanReel(boolean canReel) {
        this.canReel = canReel;
    }

    /**
     * Updates the last reel click time to the current system time.
     */
    public void updateLastReelClickTime() {
        this.lastReelClickTime = System.currentTimeMillis();
    }

    /**
     * Returns the last time the reel was clicked.
     *
     * @return the last reel click time in milliseconds
     */
    public long getLastReelClickTime() {
        return this.lastReelClickTime;
    }

    /**
     * Returns the fishing session associated with this fishing data.
     *
     * @return the fishing session
     */
    public FishingSession getFishingSession() {
        return fishingSession; // Getter for FishingSession
    }

    /**
     * Sets the fishing session associated with this fishing data.
     *
     * @param fishingSession the fishing session to set
     */
    public void setFishingSession(FishingSession fishingSession) {
        this.fishingSession = fishingSession; // Setter for FishingSession
    }

    /**
     * Starts a fishing session with a specified fish and reel time.
     *
     * @param caughtFish the fish that is caught
     * @param reelTime   the time allocated for reeling in the fish
     */
    public void startFishing(CustomFish caughtFish, long reelTime, FishHook hook) {
        this.caughtFish = caughtFish;
        this.reelTime = reelTime;
        this.resetClickCount();
        this.fishEscaped = false;
        this.canReel = true;
        this.reelStartTime = System.currentTimeMillis();
        this.hook = hook;
    }

    public FishHook getFishingHook(){
      return this.hook;
    }


    /**
     * Stops the fishing session, resetting relevant attributes.
     */
    public void stopFishing() {
        this.caughtFish = null;
        this.reelTime = 0;
        this.fishEscaped = false;
        this.resetClickCount();
        this.canReel = false;
        this.lastReelClickTime = 0;
        this.hook = null;
    }
}