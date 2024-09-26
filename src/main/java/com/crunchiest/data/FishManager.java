package com.crunchiest.data;

import java.util.ArrayList;
import java.util.List;

import com.crunchiest.config.FishConfig;

/**
 * Manages the collection of fish, allowing dynamic loading and updating of fish data.
 */
public class FishManager {
    private final List<Fish> fishList;

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
    public void addCustomFish(List<Fish> customFish) {
        fishList.clear(); // Clear existing fish
        fishList.addAll(customFish); // Add new custom fish
    }

    /**
     * Refreshes the fish data from the given FishConfig instance.
     *
     * @param fishConfig The FishConfig instance to load data from
     */
    public void refreshFishData(FishConfig fishConfig) {
        List<Fish> updatedFishList = fishConfig.loadFishData();
        addCustomFish(updatedFishList); // Refresh fish data in the manager
    }

    /**
     * Gets the list of all fish.
     *
     * @return List of Fish objects.
     */
    public List<Fish> getFishList() {
        return fishList;
    }

    /**
     * Creates a random fish based on rarity from the fish list.
     *
     * @return a randomly selected Fish object or null if no fish available
     */
    public Fish createRandomFish() {
        if (fishList.isEmpty()) {
            return null; // No fish available
        }

        // Calculate total rarity
        int totalRarity = fishList.stream().mapToInt(Fish::getRarity).sum();
        int rarityRoll = (int) (Math.random() * totalRarity);

        int currentRaritySum = 0;
        for (Fish fish : fishList) {
            currentRaritySum += fish.getRarity();
            if (rarityRoll < currentRaritySum) {
                return fish;
            }
        }

        return null; // Fallback (should not happen)
    }
}