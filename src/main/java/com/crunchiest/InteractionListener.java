package com.crunchiest;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

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
        }
    }
}