package com.crunchiest;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * A utility class for playing sounds to players in the game.
 */
public class SoundUtil {

    /**
     * Plays the click sound to the specified player.
     *
     * @param player The player to play the sound for.
     */
    public static void playClickSound(Player player) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 2.0f);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.2f, 0.5f);
    }

    /**
     * Plays the failure sound to the specified player.
     *
     * @param player The player to play the sound for.
     */
    public static void playFailureSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
    }

    /**
     * Plays the success sound to the specified player.
     *
     * @param player The player to play the sound for.
     */
    public static void playSuccessSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
    }

    /**
     * Plays multiple success sounds to the specified player.
     *
     * @param player The player to play the sounds for.
     */
    public static void playSuccessSounds(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.playSound(player.getLocation(), Sound.ENTITY_AXOLOTL_SPLASH, 1.0f, 1.0f);
    }
}