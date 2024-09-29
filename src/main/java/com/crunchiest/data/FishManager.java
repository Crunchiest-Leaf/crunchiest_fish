package com.crunchiest.data;
//
import java.util.ArrayList;
import java.util.List;
// 
import com.crunchiest.config.FishConfig;

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


/**
 * Manages the collection of fish, allowing dynamic loading and updating of fish data.
 */
public class FishManager {
    private final List<CustomFish> fishList;

    /**
     * Constructs a FishManager.
     */
    public FishManager() {
        this.fishList = new ArrayList<>();
    }

    /**
     * Adds a list of custom fish to the manager.
     *
     * @param customFish the list of custom Fish to add
     */
    public void addCustomFish(List<CustomFish> customFish) {
        fishList.clear(); // Clear existing fish
        fishList.addAll(customFish); // Add new custom fish
    }

    /**
     * Refreshes the fish data from the given FishConfig instance.
     *
     * @param fishConfig The FishConfig instance to load data from
     */
    public void refreshFishData(FishConfig fishConfig) {
        List<CustomFish> updatedFishList = fishConfig.loadFishData();
        addCustomFish(updatedFishList); // Refresh fish data in the manager
    }

    /**
     * Gets the list of all fish.
     *
     * @return List of Fish objects.
     */
    public List<CustomFish> getFishList() {
        return fishList;
    }

    /**
     * Creates a random fish based on rarity from the fish list.
     *
     * @return a randomly selected Fish object or null if no fish available
     */
    public CustomFish createRandomFish() {
        if (fishList.isEmpty()) {
            return null; // No fish available
        }

        // Calculate total rarity
        int totalRarity = fishList.stream().mapToInt(CustomFish::getRarity).sum();
        int rarityRoll = (int) (Math.random() * totalRarity);

        int currentRaritySum = 0;
        for (CustomFish fish : fishList) {
            currentRaritySum += fish.getRarity();
            if (rarityRoll < currentRaritySum) {
                return fish;
            }
        }

        return null; // Fallback (should not happen)
    }

}