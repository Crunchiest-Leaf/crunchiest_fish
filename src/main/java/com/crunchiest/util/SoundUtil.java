package com.crunchiest.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

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
 * A utility class for playing sounds to players in the game.
 */
public class SoundUtil {

    /**
     * Plays the click sound to the specified player.
     *
     * @param player The player to play the sound for.
     */
    public static void playClickSound(Player player) {
        playSound(player, Sound.UI_BUTTON_CLICK, 1.0f, 2.0f);
        playSound(player, Sound.UI_BUTTON_CLICK, 0.2f, 0.5f);
    }

    /**
     * Plays the failure sound to the specified player.
     *
     * @param player The player to play the sound for.
     */
    public static void playFailureSound(Player player) {
        playSound(player, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
    }

    /**
     * Plays the success sound to the specified player.
     *
     * @param player The player to play the sound for.
     */
    public static void playSuccessSound(Player player) {
        playSound(player, Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
    }

    /**
     * Plays multiple success sounds to the specified player.
     *
     * @param player The player to play the sounds for.
     */
    public static void playSuccessSounds(Player player) {
        playSound(player, Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
        playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        playSound(player, Sound.ENTITY_AXOLOTL_SPLASH, 1.0f, 1.0f);
    }

    /**
     * Plays treasure-related sounds to the specified player.
     *
     * @param player The player to play the sounds for.
     */
    public static void playTreasureSounds(Player player) {
        playSound(player, Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
        playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        playSound(player, Sound.ENTITY_AXOLOTL_SPLASH, 1.0f, 1.0f);
        playSound(player, Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
    }

    /**
     * Plays a sound at the player's location with the specified volume and pitch.
     *
     * @param player The player to play the sound for.
     * @param sound  The sound to be played.
     * @param volume The volume of the sound.
     * @param pitch  The pitch of the sound.
     */
    private static void playSound(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
}