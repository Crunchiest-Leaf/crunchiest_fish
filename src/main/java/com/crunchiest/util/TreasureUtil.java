package com.crunchiest.util;

// Bukkit API for materials, players, and items
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

// Custom classes from project
import com.crunchiest.data.CustomTreasure;
import com.crunchiest.data.TreasureManager;
import java.util.Optional;

// Java utility classes
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Arrays;

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
 */

/**
 * Utility class for generating, managing, and spawning treasure items in the game.
 */
public class TreasureUtil {

    // A list of quips that may be shown to the player upon fishing up treasure.
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

    // Reusing the same Random instance for efficiency.
    private static final Random RANDOM = new Random();

    /**
     * Generates a random treasure for the player, displays a title, and spawns the item.
     *
     * @param player the player who is fishing for treasure
     * @param treasureManager the manager responsible for treasure generation
     */
    public static void generateTreasure(Player player, TreasureManager treasureManager) {
      Optional<CustomTreasure> optionalTreasure = treasureManager.rollForTreasure();
  
      optionalTreasure.ifPresentOrElse(
          treasure -> {
              String treasureName = StringUtil.removeUnderscores(treasure.getName());
              player.sendMessage("You fish up some treasure... or trash...");
              SoundUtil.playTreasureSounds(player);
              player.sendTitle(ChatColor.GOLD + treasureName, ChatColor.DARK_GREEN + generateTreasureQuip(), 5, 60, 10);
              spawnTreasureItem(player, treasure);
          },
          () -> player.sendMessage(ChatColor.RED + "No treasure found! Better luck next time.")
      );
  }

    /**
     * Spawns a treasure item at the player's location with appropriate name and lore.
     *
     * @param player the player who receives the treasure
     * @param treasure the treasure item to be spawned
     */
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

    /**
     * Creates lore for the treasure item including a decorative header and formatted description.
     *
     * @param treasure the treasure for which the lore is created
     * @return a list of lore strings formatted with color and descriptions
     */
    private static List<String> createTreasureLore(CustomTreasure treasure) {
        List<String> description = new ArrayList<>();

        // Add a decorative header
        description.add("§7-=-=-=- Treasure Found -=-=-=-");
        description.add("§fA unique and mysterious item...");
        description.add(""); // Add spacing

        // Add formatted descriptions with additional spacing
        description.addAll(StringUtil.formatEntityDescriptions(treasure.getDescription())
                .stream()
                .map(line -> "§f " + line) // Prefix each line with a bullet point
                .toList());

        // Add a decorative footer
        description.add("");
        description.add("§7-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

        return description;
    }

    /**
     * Generates a random quip to be displayed when the player fishes up treasure.
     *
     * @return a random quip string from the predefined list
     */
    private static String generateTreasureQuip() {
        int randomIndex = RANDOM.nextInt(TREASURE_QUIPS.size()); // Get a random index
        return TREASURE_QUIPS.get(randomIndex); // Return the string at that index
    }
}