package com.crunchiest.data;

import com.crunchiest.config.TreasureConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

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
 * Manages the collection of treasures, allowing dynamic loading and updating of treasure data.
 */
public class TreasureManager {
    private final List<CustomTreasure> treasureList;

    /**
     * Constructs a TreasureManager.
     */
    public TreasureManager() {
        this.treasureList = new ArrayList<>();
    }

    /**
     * Adds a list of custom treasures to the manager, replacing existing treasures.
     *
     * @param customTreasures the list of custom treasures to add
     */
    public void addCustomTreasures(List<CustomTreasure> customTreasures) {
        treasureList.clear(); // Clear existing treasures
        treasureList.addAll(customTreasures); // Add new custom treasures
    }

    /**
     * Refreshes the treasure data from the given TreasureConfig instance.
     *
     * @param treasureConfig The TreasureConfig instance to load data from
     */
    public void refreshTreasureData(TreasureConfig treasureConfig) {
        List<CustomTreasure> updatedTreasureList = treasureConfig.loadTreasureData();
        addCustomTreasures(updatedTreasureList); // Refresh treasure data in the manager
    }

    /**
     * Gets the list of all treasures.
     *
     * @return List of CustomTreasure objects.
     */
    public List<CustomTreasure> getTreasureList() {
        return new ArrayList<>(treasureList); // Return a copy to protect internal state
    }

    /**
     * Creates a random treasure based on rarity from the treasure list.
     *
     * @return an Optional containing a randomly selected CustomTreasure object or an empty Optional if no treasure available
     */
    public Optional<CustomTreasure> rollForTreasure() {
        if (treasureList.isEmpty()) {
            return Optional.empty(); // No treasures available
        }

        // Calculate total rarity
        int totalRarity = treasureList.stream().mapToInt(CustomTreasure::getRarity).sum();
        int rarityRoll = ThreadLocalRandom.current().nextInt(totalRarity); // Use ThreadLocalRandom for better performance

        int currentRaritySum = 0;
        for (CustomTreasure treasure : treasureList) {
            currentRaritySum += treasure.getRarity();
            if (rarityRoll < currentRaritySum) {
                return Optional.of(treasure);
            }
        }

        return Optional.empty(); // Fallback (should not happen)
    }
}