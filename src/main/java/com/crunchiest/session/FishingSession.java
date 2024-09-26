package com.crunchiest.session;

import com.crunchiest.CrunchiestFishingPlugin;
import com.crunchiest.data.FishingData;
import com.crunchiest.data.Fish;
import com.crunchiest.util.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BossBar; 
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import net.md_5.bungee.api.ChatColor;

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
 * Represents a fishing session for a player, managing the reeling process,
 * including the display of boss bars for time remaining and click percentage.
 */
public class FishingSession {

    private final Player player;
    private final FishingData fishingData;
    private final CrunchiestFishingPlugin plugin;
    private static final long TIRED_FISH_TIME_LIMIT = 40L; // 2 seconds in ticks after the fish gets tired
    private static final int MAX_CLICKS_PER_SECOND = 8;
    private long reelStartTime; // Track the start time of the reel

    // Initialize boss bars
    private final BossBar timeBossBar;
    private final BossBar clickBossBar;

    /**
     * Constructs a FishingSession for a player.
     *
     * @param player the player participating in the fishing session
     * @param fishingData the fishing data associated with the player
     * @param plugin the plugin instance for task scheduling
     */
    public FishingSession(Player player, FishingData fishingData, CrunchiestFishingPlugin plugin) {
        this.player = player;
        this.fishingData = fishingData;
        this.plugin = plugin;

        // Set this FishingSession in FishingData
        fishingData.setFishingSession(this);

        // Initialize the boss bars using the updated BarColor and BarStyle enums
        timeBossBar = Bukkit.createBossBar("Time Remaining", BarColor.BLUE, BarStyle.SOLID);
        clickBossBar = Bukkit.createBossBar("Click Percentage", BarColor.GREEN, BarStyle.SOLID);
        timeBossBar.setTitle("- Fish Struggle -");
        clickBossBar.setTitle("- Reeling Progress -");
    }

    /**
     * Starts the reeling session for the player.
     */
    public void startReelSession() {
        player.sendTitle(ChatColor.GOLD + "FISH ON!", ChatColor.DARK_GREEN + "better get reeling", 5, 10, 5);
        fishingData.resetClickCount();
        int targetClicks = calculateTargetClicks(fishingData.getCaughtFish());
        fishingData.setTargetClicks(targetClicks);

        // Store the current time as the start time for reeling
        reelStartTime = System.currentTimeMillis();
        long totalReelTime = fishingData.getReelTime();

        // Add the player to both boss bars
        timeBossBar.addPlayer(player);
        clickBossBar.addPlayer(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - reelStartTime;

                // Check if the player has clicked enough times
                if (fishingData.getClickCount() < targetClicks) {
                    player.sendMessage("You were too slow! The fish got away.");
                    player.sendMessage("Total Reel Time: " + totalReelTime + " ms, Target Clicks: " + targetClicks);
                    SoundUtil.playFailureSound(player);
                    stopFishing();
                } else {
                    // If the player reached the target clicks in less than half the time, they lose the fish
                    long timeOfFinalClick = fishingData.getLastReelClickTime() - reelStartTime;
                    if (timeOfFinalClick < totalReelTime * 0.4) {
                        player.sendMessage("You reeled too fast! The fish escaped.");
                        player.sendMessage("Elapsed Time: " + timeOfFinalClick + " ms, Total Reel Time: " + totalReelTime + " ms, Target Clicks: " + targetClicks);
                        SoundUtil.playFailureSound(player);
                        stopFishing();
                    } else {
                        // Success, player caught the fish within the allowed time
                        player.sendMessage("QUICKLY PULL THE FISH IN!");
                        player.sendMessage("Elapsed Time: " + timeOfFinalClick + " ms, Total Reel Time: " + totalReelTime + " ms, Target Clicks: " + targetClicks);
                        SoundUtil.playSuccessSound(player);
                        fishingData.resetClickCount();  // Reset the clicks for the next phase
                        startTiredFishTimer();
                    }
                }

                // Update the boss bars every tick
                updateFishingBars(elapsedTime, totalReelTime, targetClicks);
            }
        }.runTaskLater(plugin, totalReelTime / 50); // Schedule task to run after the reel time
    }

    /**
     * Calculates the target number of clicks required to reel in the caught fish.
     *
     * @param caughtFish the fish that has been caught
     * @return the calculated target number of clicks
     */
    private int calculateTargetClicks(Fish caughtFish) {
        // Get fish attributes
        double weight = caughtFish.getWeight();  // Fish weight
        double length = caughtFish.getLength();  // Fish length

        // Get the total reel time in seconds (from milliseconds to seconds)
        long totalReelTimeMillis = fishingData.getReelTime();
        double totalReelTimeSeconds = totalReelTimeMillis / 1000.0;

        // Max possible clicks based on time (8 clicks per second)
        int maxPossibleClicks = (int) Math.floor(MAX_CLICKS_PER_SECOND * totalReelTimeSeconds);

        // Base click count calculation using weight and length
        int baseClicks = (int) Math.ceil((weight * 2.0) + (length * 1.5));

        // Ensure the required clicks do not exceed the max possible clicks per second
        return Math.min(baseClicks * 2, maxPossibleClicks);
    }

    /**
     * Starts a timer for when the fish gets tired.
     */
    private void startTiredFishTimer() {
        fishingData.setFishEscaped(true);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (fishingData.isFishEscaped()) {
                    player.sendMessage("You were too slow! The fish got away.");
                    SoundUtil.playFailureSound(player);
                    stopFishing();
                } else {
                    String fishInfo = fishingData.getCaughtFish().getFormattedInfo();
                    player.sendMessage(fishInfo);
                    player.sendTitle(ChatColor.GOLD + fishingData.getCaughtFish().getType(), fishInfo, 5, 60, 10);
                    SoundUtil.playSuccessSounds(player);
                    stopFishing();
                }
            }
        }.runTaskLater(plugin, TIRED_FISH_TIME_LIMIT); // time for landing time, fish gets off if too slow.
    }

    /**
     * Updates the boss bars with the current time remaining and click percentage.
     *
     * @param elapsedTime The time that has elapsed since the start of the reel.
     * @param totalReelTime The total time allowed for the reel.
     * @param targetClicks The total clicks required.
     */
    public void updateFishingBars(long elapsedTime, long totalReelTime, int targetClicks) {
        // Update time boss bar (inverted to fill up)
        double timeProgress = Math.min(1.0, elapsedTime / (double) totalReelTime); // Ensure progress does not exceed 1
        timeBossBar.setProgress(timeProgress); // Show remaining time

        // Update click percentage boss bar
        double percentageOfClicks = (fishingData.getClickCount() / (double) targetClicks) * 100;
        clickBossBar.setProgress(percentageOfClicks / 100);
    }

    /**
     * Stops the fishing session and removes the player from the boss bars.
     */
    private void stopFishing() {
        fishingData.stopFishing();
        // Remove player from boss bars when fishing stops
        timeBossBar.removePlayer(player);
        clickBossBar.removePlayer(player);
    }
}