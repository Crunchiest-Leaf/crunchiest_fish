package com.crunchiest.listener;

import com.crunchiest.data.FishingData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.UUID;

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
 * Listens for player interactions and handles fishing-related actions.
 */
public class InteractionListener implements Listener {

    private final FishingListener fishingListener;

    /**
     * Constructs a new InteractionListener.
     *
     * @param fishingListener The FishingListener instance to delegate fishing data to.
     */
    public InteractionListener(FishingListener fishingListener) {
        this.fishingListener = fishingListener;
    }

    /**
     * Handles player interaction events.
     *
     * @param event The PlayerInteractEvent triggered by the player's action.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        // Get the FishingData for the player
        FishingData data = fishingListener.getFishingData(playerUUID);
        if (data == null) return; // If there's no fishing data, return early

        // Check if the player is allowed to reel and is left-clicking.
        if (data.canReel() &&
                (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            // Handle the player's clicking when reeling in.
            fishingListener.handlePlayerClick(playerUUID, player);
        } else {
            // Optional feedback for players who attempt to click when not able to reel
            //sendMessage(player, "You can't reel in right now!"); // Consider defining sendMessage
        }
    }

    /**
     * Sends a message to the specified player.
     *
     * @param player the player to send the message to
     * @param message the message to send
     */
    private void sendMessage(Player player, String message) {
        player.sendMessage(message); // Sends the message directly to the player
    }
}