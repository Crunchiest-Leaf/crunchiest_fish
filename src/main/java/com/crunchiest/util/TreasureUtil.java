package com.crunchiest.util;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.crunchiest.data.CustomTreasure;
import com.crunchiest.data.TreasureManager;

import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.MythicItem;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Arrays;
import java.util.Optional;

/**
 * Utility class for generating, managing, and spawning treasure items in the game.
 */
public class TreasureUtil {

    private static final Random RANDOM = new Random(); // Efficient shared random instance

    // Predefined quips that can be shown to players upon fishing treasure.
    private static final List<String> TREASURE_QUIPS = Collections.unmodifiableList(Arrays.asList(
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
    ));

    /**
     * Generates a random quip to be displayed when the player fishes up treasure.
     *
     * @return a random quip string from the predefined list
     */
    private static String generateTreasureQuip() {
        int randomIndex = RANDOM.nextInt(TREASURE_QUIPS.size());
        return TREASURE_QUIPS.get(randomIndex);
    }

    /**
     * Generates a treasure item for the player and displays a title with a quip.
     *
     * @param player          The player who fished up the treasure.
     * @param treasureManager The manager handling treasure generation.
     */
    public static void generateTreasure(Player player, TreasureManager treasureManager) {
        // Display a random quip to the player along with a title.
        player.sendTitle(ChatColor.GOLD + "TREASURE OR TRASH?!", ChatColor.GREEN + generateTreasureQuip(), 10, 40, 10);
        
        // Generate the treasure item.
        ItemStack treasure = createTreasureStack(treasureManager);
        
        // If treasure generation was successful, drop the item for the player.
        if (treasure != null) {
            dropTreasureItem(player, treasure);
        } else {
            player.sendMessage(ChatColor.RED + "Something went wrong with the treasure generation.");
        }
    }

    /**
     * Creates a treasure item stack based on a rolled treasure.
     *
     * @param treasureManager The manager that handles treasure data and rolls.
     * @return An ItemStack representing the treasure, or null if no treasure was rolled.
     */
    private static ItemStack createTreasureStack(TreasureManager treasureManager) {
        // Roll for a treasure.
        Optional<CustomTreasure> rolledTreasure = treasureManager.rollForTreasure();

        if (rolledTreasure.isPresent()) {
            CustomTreasure treasure = rolledTreasure.get();
            
            // Fetch the MythicItem from MythicBukkit based on the treasure's name.
            MythicItem mythicItem = MythicBukkit.inst().getItemManager().getItem(treasure.getName()).orElse(null);

            if (mythicItem == null) {

                return null;
            }

            // Generate the item stack from the MythicItem.
            AbstractItemStack abstractItem = mythicItem.generateItemStack(treasure.getQuantity());
            return BukkitAdapter.adapt(abstractItem); // Convert to Bukkit's ItemStack
        } else {
            return null; // No treasure was rolled.
        }
    }

    /**
     * Drops the generated treasure item at the player's location.
     *
     * @param player The player who fished up the treasure.
     * @param stack  The ItemStack representing the treasure.
     */
    private static void dropTreasureItem(Player player, ItemStack stack) {
        player.getWorld().dropItemNaturally(player.getLocation(), stack);
        player.sendMessage(ChatColor.GOLD + "You've fished up a treasure!");
    }
}