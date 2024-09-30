package com.crunchiest.util;

// Bukkit API for color codes in strings
import net.md_5.bungee.api.ChatColor;

// Java utility classes for string handling
import java.util.List;
import java.util.stream.Collectors;

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
 * A utility class for string manipulation and formatting.
 */
public class StringUtil {

    /**
     * Removes underscores from the given string and replaces them with spaces.
     *
     * @param string The input string with underscores.
     * @return A new string with underscores replaced by spaces.
     */
    public static String removeUnderscores(String string) {
        return string.replace("_", " ");
    }

    /**
     * Formats a list of entity descriptions by adding a specific color to each description.
     *
     * @param descriptions The list of descriptions to format.
     * @return A list of formatted descriptions with the specified color.
     */
    public static List<String> formatEntityDescriptions(List<String> descriptions) {
        ChatColor color = ChatColor.GOLD; // Color to apply to descriptions
        return descriptions.stream()
                .map(s -> color + s) // Prepend color to each description
                .collect(Collectors.toList());
    }
}