package com.crunchiest.util;

// Bukkit API and Minecraft classes
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

// Custom project-specific classes
import com.crunchiest.data.CustomFish;
import com.crunchiest.data.FishingData;
import com.crunchiest.CrunchiestFishingPlugin;

// Java utility classes
import java.util.ArrayList;
import java.util.List;

import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.mobs.MythicMob;
// mythic
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.drops.droppables.MythicItemDrop;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.mobs.ActiveMob;

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

public class FishUtil {

    /**
     * Displays information about the caught fish to the player.
     *
     * @param player      The player to display the fish information to.
     * @param fishingData The fishing data containing details about the caught fish.
     * @param plugin      The plugin instance used for scheduling tasks.
     */
    public static void displayCaughtFish(Player player, FishingData fishingData, CrunchiestFishingPlugin plugin) {
        if (player == null || fishingData == null) {
            throw new IllegalArgumentException("Player and fishing data must not be null.");
        }

        CustomFish caughtFish = fishingData.getCaughtFish();
        if (caughtFish == null) {
            player.sendMessage(ChatColor.RED + "No fish caught.");
            return;
        }

        String fishInfo = caughtFish.getFormattedInfo();
        player.sendMessage(fishInfo);
        player.sendTitle(ChatColor.GOLD + caughtFish.getType(), fishInfo, 5, 60, 10);
        SoundUtil.playSuccessSound(player);
        spawnFishInFrontOfPlayer(player, caughtFish, plugin);
    }

    /**
     * Spawns the caught fish entity in front of the player.
     *
     * @param player      The player who caught the fish.
     * @param caughtFish  The fish that was caught.
     * @param plugin      The plugin instance for scheduling tasks.
     */
    public static void spawnFishInFrontOfPlayer(Player player, CustomFish caughtFish, CrunchiestFishingPlugin plugin) {
        Location spawnLocation = player.getEyeLocation().add(player.getLocation().getDirection().normalize().multiply(1.5));
        MythicMob fishEntity = MythicBukkit.inst().getMobManager().getMythicMob(caughtFish.getEntityType()).orElse(null);
        ActiveMob fish = fishEntity.spawn(BukkitAdapter.adapt(spawnLocation),1);
        Entity fishBukkitEntity = fish.getEntity().getBukkitEntity();

        new BukkitRunnable() {
            int ticks = 0;
            final int duration = 60; // Duration in ticks (3 seconds)

            @Override
            public void run() {
                if (ticks < duration) {
                    updateFishPosition(player, fishBukkitEntity);
                    ticks++;
                } else {
                    dropFishItem(player, caughtFish, fishBukkitEntity);
                    fish.remove();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

        /**
     * Updates the position of the fish entity above the player's eye level.
     *
     * @param player     The player who caught the fish.
     * @param fishEntity The fish entity to move.
     */
    private static void updateFishPosition(Player player, Entity fishEntity) {
      Location playerLocation = player.getEyeLocation();
      float playerYaw = playerLocation.getYaw();

      double distance = 0.7;
      double xOffset = distance * Math.cos(Math.toRadians(playerYaw + 90));
      double zOffset = distance * Math.sin(Math.toRadians(playerYaw + 90));

      fishEntity.teleport(playerLocation.clone().add(xOffset, -0.75, zOffset));
      fishEntity.setRotation(playerYaw + 90, 0);
  }


    /**
     * Drops the fish item at the location of the fish entity.
     *
     * @param player     The player who caught the fish.
     * @param caughtFish The fish that was caught.
     * @param fishEntity The fish entity that was spawned.
     */
    public static void dropFishItem(Player player, CustomFish caughtFish, Entity fishEntity) {
        MythicItem item = MythicBukkit.inst().getItemManager().getItem(caughtFish.getMaterialType()).orElse(null);
        AbstractItemStack abstractItem = item.generateItemStack(1);
        ItemStack stack = BukkitAdapter.adapt(abstractItem);
        player.getWorld().dropItemNaturally(fishEntity.getLocation(), stack);
    }


}