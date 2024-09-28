package com.crunchiest.util;

// Bukkit API and Minecraft classes
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

// Custom project-specific classes
import com.crunchiest.data.CustomFish;
import com.crunchiest.data.FishingData;
import com.crunchiest.CrunchiestFishingPlugin;

// Java utility classes
import java.util.ArrayList;
import java.util.List;

public class FishUtil {

  public static void displayCaughtFish(Player player, FishingData fishingData, CrunchiestFishingPlugin plugin) {
      CustomFish caughtFish = fishingData.getCaughtFish();
      String fishInfo = caughtFish.getFormattedInfo();
      player.sendMessage(fishInfo);
      player.sendTitle(ChatColor.GOLD + caughtFish.getType(), fishInfo, 5, 60, 10);
      SoundUtil.playSuccessSounds(player);
      spawnFishInFrontOfPlayer(player, caughtFish, plugin);
  }

  public static void spawnFishInFrontOfPlayer(Player player, CustomFish caughtFish, CrunchiestFishingPlugin plugin) {
      Location spawnLocation = player.getEyeLocation().add(player.getLocation().getDirection().normalize().multiply(1.5));
      Entity fishEntity = player.getWorld().spawnEntity(spawnLocation, caughtFish.getEntityType());

      new BukkitRunnable() {
          int ticks = 0;
          final int duration = 60; // Hover for 3 seconds

          @Override
          public void run() {
              if (ticks < duration) {
                  Location playerLocation = player.getEyeLocation();
                  float playerYaw = playerLocation.getYaw();

                  double distance = 0.7;
                  double xOffset = distance * Math.cos(Math.toRadians(playerYaw + 90));
                  double zOffset = distance * Math.sin(Math.toRadians(playerYaw + 90));

                  fishEntity.teleport(playerLocation.clone().add(xOffset, -0.75, zOffset));
                  fishEntity.setRotation(playerYaw + 90, 0);

                  ticks++;
              } else {
                  dropFishItem(player, caughtFish, fishEntity);
                  fishEntity.remove();
                  this.cancel();
              }
          }
      }.runTaskTimer(plugin, 0L, 1L);
  }

  public static void dropFishItem(Player player, CustomFish caughtFish, Entity fishEntity) {
      ItemStack fishItem = new ItemStack(getMaterialFromEntityType(caughtFish.getEntityType()));
      ItemMeta meta = fishItem.getItemMeta();

      if (meta != null) {
          meta.setDisplayName(ChatColor.GOLD + StringUtil.removeUnderscores(caughtFish.getType()));
          meta.setLore(createFishLore(caughtFish));
          fishItem.setItemMeta(meta);
      }

      player.getWorld().dropItemNaturally(fishEntity.getLocation(), fishItem);
  }

  private static List<String> createFishLore(CustomFish caughtFish) {
      List<String> description = new ArrayList<>();
      description.add("-=- - - - - - - - - - - - - - - - - -=-");
      description.add(ChatColor.GRAY + "Length: " + caughtFish.getLength() + " cm");
      description.add(ChatColor.GRAY + "Weight: " + caughtFish.getWeight() + " kg");
      description.add("-=- - - - - - - - - - - - - - - - - -=-");
      description.addAll(StringUtil.formatEntityDescriptions(caughtFish.getDescription()));
      return description;
  }

  private static Material getMaterialFromEntityType(EntityType entityType) {
      switch (entityType) {
          case SALMON:
              return Material.SALMON;
          case TROPICAL_FISH:
              return Material.TROPICAL_FISH;
          case PUFFERFISH:
              return Material.PUFFERFISH;
          default:
              return Material.COD;
      }
  }
}