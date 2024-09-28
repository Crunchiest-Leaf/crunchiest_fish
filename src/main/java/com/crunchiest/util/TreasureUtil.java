package com.crunchiest.util;

import org.joml.Random;

public class TreasureUtil {
  
    public static String generateTreasureQuip(){
        String treasureQuips[] = 
          { "Looks like you've snagged a surprise! Is it treasure or trash?",
            "Well, that's one way to clean up the ocean! Junk or gem?",
            "Ahoy! You've pulled something from the depths—let's hope it's gold and not garbage!",
            "You never know what you'll reel in! Could be fortune... or just flotsam!",
            "Is it a treasure worth keeping or just some old driftwood? Only one way to find out!",
            "A catch is a catch! Let’s hope it shines instead of stinks!",
            "What’s this? An unexpected haul! Is it valuable or just a piece of junk?",
            "Congratulations! You've caught something! Now, is it worth a fortune or just a forgotten trinket?",
            "Every cast is an adventure! Will you land a treasure or a tale of woe?",
            "You’ve reeled in a mystery! Will it be a jewel or just yesterday’s trash?"
          };

        Random random = new Random();
        int randomIndex = random.nextInt(treasureQuips.length); // Get a random index
        return treasureQuips[randomIndex]; // Return the string at that index
    }
       
}
