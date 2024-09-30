package com.crunchiest.session;

import com.crunchiest.CrunchiestFishingPlugin;
import com.crunchiest.data.CustomFish;
import com.crunchiest.data.FishingData;
import com.crunchiest.data.TreasureManager;
import com.crunchiest.util.SoundUtil;
import com.crunchiest.util.TreasureUtil;
import com.crunchiest.util.FishUtil;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.function.Consumer;

import java.util.Random;
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
 * Manages the fishing session for a player, including the process of reeling
 * in a fish, displaying progress bars, and handling fish catch or escape.
 */
public class FishingSession {

    private static final long TIRED_FISH_TIME_LIMIT = 40L; // 2 seconds in ticks after the fish gets tired
    private static final int MAX_CLICKS_PER_SECOND = 8;

    private final Player player;
    private final FishingData fishingData;
    private final CrunchiestFishingPlugin plugin;
    private final TreasureManager treasureManager;

    private final BossBar timeBossBar;
    private final BossBar clickBossBar;
    private long reelStartTime;

    private static final String FISH_ON_TITLE = ChatColor.GOLD + "FISH ON!";
    private static final String REELING_MESSAGE = ChatColor.DARK_GREEN + "better get reeling";
    private static final String FISH_STRUGGLE_TITLE = "- Fish Struggle -";
    private static final String REELING_PROGRESS_TITLE = "- Reeling Progress -";
    private static final String STOPPED_MESSAGE = "You stopped fishing, and let the fish go.";
    private static final String SLOW_MESSAGE = "You were too slow! The fish got away.";
    private static final String ESCAPED_MESSAGE = "You reeled too fast! The fish escaped.";
    private static final String QUICK_PULL_MESSAGE = "QUICKLY PULL THE FISH IN!";

    /**
     * Constructor for the FishingSession class.
     *
     * @param player       the player involved in the session
     * @param fishingData  the fishing data associated with the session
     * @param plugin       the plugin instance for task scheduling
     */
    public FishingSession(Player player, FishingData fishingData, CrunchiestFishingPlugin plugin) {
        this.player = player;
        this.fishingData = fishingData;
        this.plugin = plugin;
        this.treasureManager = plugin.getTreasureManager();

        fishingData.setFishingSession(this);

        timeBossBar = Bukkit.createBossBar(FISH_STRUGGLE_TITLE, BarColor.BLUE, BarStyle.SOLID);
        clickBossBar = Bukkit.createBossBar(REELING_PROGRESS_TITLE, BarColor.GREEN, BarStyle.SOLID);
    }

    /**
     * Starts the reel session for the player, initiating boss bars and progress tracking.
     */
    public void startReelSession() {
        player.sendTitle(FISH_ON_TITLE, REELING_MESSAGE, 5, 10, 5);
        fishingData.resetClickCount();
        int targetClicks = calculateTargetClicks(fishingData.getCaughtFish());
        fishingData.setTargetClicks(targetClicks);
        reelStartTime = System.currentTimeMillis();
        long totalReelTime = fishingData.getReelTime();

        timeBossBar.addPlayer(player);
        clickBossBar.addPlayer(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - reelStartTime;

                if (!hasFishingRodInHand(player) || !fishingData.canReel()) {
                    sendMessageAndSound(STOPPED_MESSAGE, SoundUtil::playFailureSound);
                    clearProgressBars();
                    stopFishing();
                    this.cancel();
                    return;
                }

                if (fishingData.getClickCount() >= targetClicks) {
                    handleSuccessfulReel(totalReelTime);
                    this.cancel();
                } else if (elapsedTime >= totalReelTime) {
                    sendMessageAndSound(SLOW_MESSAGE, SoundUtil::playFailureSound);
                    clearProgressBars();
                    stopFishing();
                    this.cancel();
                } else {
                    updateFishingBars(elapsedTime, totalReelTime, targetClicks);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    /**
     * Sends a message to the player and plays the corresponding sound.
     *
     * @param message the message to send to the player
     * @param soundAction the sound to play
     */
    private void sendMessageAndSound(String message, Consumer<Player> soundAction) {
        player.sendMessage(message);
        soundAction.accept(player);
    }

    /**
     * Handles the actions when the player has clicked enough times to successfully reel in the fish.
     *
     * @param totalReelTime the total allowed reeling time for the fish
     */
    private void handleSuccessfulReel(long totalReelTime) {
        long timeOfFinalClick = fishingData.getLastReelClickTime() - reelStartTime;
        clearProgressBars();

        if (timeOfFinalClick < totalReelTime * 0.4) {
            sendMessageAndSound(ESCAPED_MESSAGE, SoundUtil::playFailureSound);
            stopFishing();
        } else {
            sendMessageAndSound(QUICK_PULL_MESSAGE, SoundUtil::playSuccessSound);
            fishingData.resetClickCount();
            fishingData.setFishEscaped(true);
            startTiredFishTimer();
        }
    }

    /**
     * Starts a timer to simulate the fish getting tired after a certain period.
     */
    private void startTiredFishTimer() {
        new BukkitRunnable() {
            int ticksElapsed = 0;

            @Override
            public void run() {
                if (ticksElapsed >= TIRED_FISH_TIME_LIMIT) {
                    sendMessageAndSound(SLOW_MESSAGE, SoundUtil::playFailureSound);
                    stopFishing();
                    this.cancel();
                    return;
                }

                if (!fishingData.isFishEscaped()) {
                    int randomValue = new Random().nextInt(100) + 1; // 1 to 100

                    if (randomValue <= 70) {
                        FishUtil.displayCaughtFish(player, fishingData, plugin); // 70% chance to display fish
                    } else {
                        TreasureUtil.generateTreasure(player, treasureManager); // 30% chance to spawn treasure
                    }
                    stopFishing();
                    this.cancel();
                    return;
                }

                ticksElapsed++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    /**
     * Calculates the target number of clicks needed to reel in a caught fish.
     *
     * @param caughtFish the fish that was caught
     * @return the calculated number of clicks
     */
    private int calculateTargetClicks(CustomFish caughtFish) {
        double weight = caughtFish.getWeight();
        double length = caughtFish.getLength();
        long totalReelTimeMillis = fishingData.getReelTime();
        double totalReelTimeSeconds = totalReelTimeMillis / 1000.0;

        int maxPossibleClicks = (int) Math.floor(MAX_CLICKS_PER_SECOND * totalReelTimeSeconds);
        int baseClicks = (int) Math.ceil((weight * 2.0) + (length * 1.5));

        // Improved logging
        plugin.getLogger().info("Calculating target clicks: weight=" + weight + ", length=" + length + ", baseClicks=" + baseClicks + ", maxPossibleClicks=" + maxPossibleClicks);
        return Math.min(baseClicks * 2, maxPossibleClicks);
    }

    /**
     * Updates the progress bars for reeling time and click percentage.
     *
     * @param elapsedTime    the time that has passed since the start of the reel
     * @param totalReelTime  the total time allowed for reeling
     * @param targetClicks   the total clicks required to catch the fish
     */
    private void updateFishingBars(long elapsedTime, long totalReelTime, int targetClicks) {
        double timeProgress = Math.min(1.0, (double) elapsedTime / totalReelTime);
        timeBossBar.setProgress(timeProgress);

        double clickProgress = Math.min(1.0, (double) fishingData.getClickCount() / targetClicks);
        clickBossBar.setProgress(clickProgress);
    }

    /**
     * Stops the fishing session and clears any active boss bars.
     */
    private void stopFishing() {
        fishingData.stopFishing();
        clearProgressBars();
    }

    /**
     * Clears the boss bars from the player's screen.
     */
    private void clearProgressBars() {
        timeBossBar.removePlayer(player);
        clickBossBar.removePlayer(player);
    }

    /**
     * Checks if the player is holding a fishing rod in either hand.
     *
     * @param player the player to check
     * @return true if a fishing rod is found in either hand; false otherwise
     */
    private boolean hasFishingRodInHand(Player player) {
      ItemStack mainHand = player.getInventory().getItemInMainHand();
      ItemStack offHand = player.getInventory().getItemInOffHand();

      return !isItemStackEmpty(mainHand) && mainHand.getType() == Material.FISHING_ROD ||
            !isItemStackEmpty(offHand) && offHand.getType() == Material.FISHING_ROD;
    }

    /**
    * Checks if the specified ItemStack is empty.
    *
    * An ItemStack is considered empty if it is null, its type is AIR, 
    * or its amount is less than or equal to zero.
    *
    * @param itemStack the ItemStack to check
    * @return true if the ItemStack is empty; false otherwise
    */
    private boolean isItemStackEmpty(ItemStack itemStack) {
      return itemStack == null || itemStack.getType() == Material.AIR || itemStack.getAmount() <= 0;
}
}