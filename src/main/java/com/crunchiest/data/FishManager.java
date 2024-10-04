package com.crunchiest.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.logging.Level;
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
    Logger logger;

    /**
     * Constructs a FishManager.
     */
    public FishManager(Logger logger) {
        this.fishList = new ArrayList<>();
        this.logger = logger;
    }

    /**
     * Adds a list of custom fish to the manager, replacing existing fish.
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
        this.logger.log(Level.INFO, "Fish Data Received by Fish Manager");
    }

    /**
     * Gets the list of all fish.
     *
     * @return List of Fish objects.
     */
    public List<CustomFish> getFishList() {
        return new ArrayList<>(fishList); // Return a copy to protect internal state
    }

    /**
     * Creates a random fish based on rarity from the fish list.
     *
     * @return an Optional containing a randomly selected Fish object or an empty Optional if no fish available
     */
    public Optional<CustomFish> createRandomFish() {
        if (fishList.isEmpty()) {
            return Optional.empty(); // No fish available
        }

        // Calculate total rarity
        int totalRarity = fishList.stream().mapToInt(CustomFish::getRarity).sum();
        int rarityRoll = ThreadLocalRandom.current().nextInt(totalRarity); // Use ThreadLocalRandom for better performance

        int currentRaritySum = 0;
        for (CustomFish templateFish : fishList) {
            currentRaritySum += templateFish.getRarity();
            if (rarityRoll < currentRaritySum) {
                // Create a new CustomFish based on the templateFish's properties
                return Optional.of(new CustomFish(
                    templateFish.getType(),
                    templateFish.getMinLength(),
                    templateFish.getMaxLength(),
                    templateFish.getMinWeight(),
                    templateFish.getMaxWeight(),
                    templateFish.getRarity(),
                    templateFish.getDescription(),
                    templateFish.getEntityType()
                ));
            }
        }

        return Optional.empty(); // Fallback (should not happen)
    }
}