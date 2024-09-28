package com.crunchiest.util;
// Bukkit API for color codes in strings
import net.md_5.bungee.api.ChatColor;

// Java utility classes for string handling
import java.util.List;
import java.util.stream.Collectors;

public class StringUtil {

    public static String removeUnderscores(String string){
      string = string.replace("_", " ");
      return string;
    }

      public static List<String> formatEntityDescriptions(List<String> descriptions) {
          ChatColor color = ChatColor.GOLD;
          return descriptions.stream()
              .map(s -> color + s)
              .collect(Collectors.toList());
      }
}
