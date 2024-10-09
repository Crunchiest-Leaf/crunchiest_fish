package com.crunchiest.util;

import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.crunchiest.data.CustomFish;
import com.crunchiest.data.FishingData;
import com.crunchiest.CrunchiestFishingPlugin;

import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.mobs.ActiveMob;

/**
 * Utility class for managing fish-related functionality in the plugin.
 */
public class FishUtil {

    private static final int TITLE_FADE_IN = 5;
    private static final int TITLE_STAY = 60;
    private static final int TITLE_FADE_OUT = 10;
    private static final int ENTITY_FOLLOW_DURATION = 60; // 3 seconds (in ticks)

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
        player.sendTitle(ChatColor.GOLD + caughtFish.getType(), fishInfo, TITLE_FADE_IN, TITLE_STAY, TITLE_FADE_OUT);
        SoundUtil.playSuccessSound(player);
        
        spawnFishInFrontOfPlayer(player, caughtFish, plugin);
    }

    /**
     * Spawns the caught fish entity in front of the player and animates it.
     *
     * @param player      The player who caught the fish.
     * @param caughtFish  The fish that was caught.
     * @param plugin      The plugin instance for scheduling tasks.
     */
    public static void spawnFishInFrontOfPlayer(Player player, CustomFish caughtFish, CrunchiestFishingPlugin plugin) {
        // Calculate spawn location in front of the player.
        Location spawnLocation = player.getEyeLocation().add(player.getLocation().getDirection().normalize().multiply(1.5));

        MythicMob fishEntity = MythicBukkit.inst().getMobManager().getMythicMob(caughtFish.getEntityType()).orElse(null);
        if (fishEntity == null) {
            player.sendMessage(ChatColor.RED + "Failed to spawn fish: Invalid MythicMob type.");
            return;
        }

        // Spawn the MythicMob fish.
        ActiveMob fish = fishEntity.spawn(BukkitAdapter.adapt(spawnLocation), 1);
        Entity fishBukkitEntity = fish.getEntity().getBukkitEntity();

        // Schedule fish movement task.
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= ENTITY_FOLLOW_DURATION || fishBukkitEntity.isDead()) {
                    dropFishItem(player, caughtFish, fishBukkitEntity);
                    fish.remove();
                    cancel();
                    return;
                }

                updateFishPosition(player, fishBukkitEntity);
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    /**
     * Updates the position of the fish entity to follow the player's movement.
     *
     * @param player     The player who caught the fish.
     * @param fishEntity The fish entity to move.
     */
    private static void updateFishPosition(Player player, Entity fishEntity) {
        if (fishEntity == null || !fishEntity.isValid()) {
            return;
        }

        Location playerLocation = player.getEyeLocation();
        float playerYaw = playerLocation.getYaw();

        // Calculate offset to position the fish slightly in front and above the player.
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
        if (fishEntity == null || !fishEntity.isValid()) {
            return;
        }

        MythicItem mythicItem = MythicBukkit.inst().getItemManager().getItem(caughtFish.getMaterialType()).orElse(null);
        if (mythicItem == null) {
            player.sendMessage(ChatColor.RED + "Failed to drop fish: Invalid MythicItem.");
            return;
        }

        AbstractItemStack abstractItemStack = mythicItem.generateItemStack(1);
        ItemStack itemStack = BukkitAdapter.adapt(abstractItemStack);
        player.getWorld().dropItemNaturally(fishEntity.getLocation(), itemStack);
    }
}