package com.crunchiest.util;
// Bukkit API for materials, players, and items
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

// Custom classes from your project
import com.crunchiest.data.TreasureManager;
import com.crunchiest.data.CustomTreasure;

// Java utility classes
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
import java.util.Collections;

public class TreasureUtil {

  public static void generateTreasure(Player player, TreasureManager treasureManager) {
      CustomTreasure treasure = treasureManager.rollForTreasure();
      String treasureName = StringUtil.removeUnderscores(treasure.getName());
      player.sendMessage("You fish up some treasure... or trash...");
      SoundUtil.playTreasureSounds(player);
      player.sendTitle(ChatColor.GOLD + treasureName, ChatColor.DARK_GREEN + generateTreasureQuip(), 5, 60, 10);
      spawnTreasureItem(player, treasure);
  }

  public static void spawnTreasureItem(Player player, CustomTreasure treasure) {
      ItemStack treasureItem = new ItemStack(treasure.getMaterial());
      ItemMeta meta = treasureItem.getItemMeta();

      if (meta != null) {
          meta.setDisplayName(ChatColor.GOLD + StringUtil.removeUnderscores(treasure.getName()));
          meta.setLore(createTreasureLore(treasure));
          treasureItem.setItemMeta(meta);
      }
      player.getWorld().dropItemNaturally(player.getLocation(), treasureItem);
  }

  private static List<String> createTreasureLore(CustomTreasure treasure) {
    List<String> description = new ArrayList<>();

    // Add a decorative header
    description.add("§7-=-=-=- Treasure Found -=-=-=-"); // Assuming treasure has a name for emphasis
    description.add("§f" + "A unique and mysterious item...");
    description.add("");

    // Add formatted descriptions with additional spacing
    description.addAll(StringUtil.formatEntityDescriptions(treasure.getDescription())
        .stream()
        .map(line -> "§f- " + line) // Prefix each line with a bullet point
        .toList());

    // Add a decorative footer
    description.add("");
    description.add("§7-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

    return description;
}

    private static final List<String> TREASURE_QUIPS = Collections.unmodifiableList(
        Arrays.asList(
            "Looks like you've snagged a surprise! Is it treasure or trash?",
            "Well, that's one way to clean up the ocean! Junk or gem?",
            "Ahoy! You've pulled something from the depths—let's hope it's gold and not garbage!",
            "You never know what you'll reel in! Could be fortune... or just flotsam!",
            "Is it a treasure worth keeping or just some old driftwood? Only one way to find out!",
            "A catch is a catch! Let’s hope it shines instead of stinks!",
            "What’s this? An unexpected haul! Is it valuable or just a piece of junk?",
            "Congratulations! You've caught something! Now, is it worth a fortune or just a forgotten trinket?",
            "Every cast is an adventure! Will you land a treasure or a tale of woe?",
            "You’ve reeled in a mystery! Will it be a jewel or just yesterday’s trash?"
        )
    );

    private static final Random RANDOM = new Random(); // Reusing the same Random instance

    private static String generateTreasureQuip() {
        int randomIndex = RANDOM.nextInt(TREASURE_QUIPS.size()); // Get a random index
        return TREASURE_QUIPS.get(randomIndex); // Return the string at that index
    }
}
