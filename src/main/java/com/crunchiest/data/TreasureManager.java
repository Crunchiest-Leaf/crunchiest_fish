package com.crunchiest.data;

import com.crunchiest.config.TreasureConfig;
import java.util.ArrayList;
import java.util.List;

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
     * Adds a list of custom treasures to the manager.
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
        return treasureList;
    }

    /**
     * Creates a random treasure based on rarity from the treasure list.
     *
     * @return a randomly selected CustomTreasure object or null if no treasure available
     */
    public CustomTreasure rollForTreasure() {
        if (treasureList.isEmpty()) {
            return null; // No treasures available
        }

        // Calculate total rarity
        int totalRarity = treasureList.stream().mapToInt(CustomTreasure::getRarity).sum();
        int rarityRoll = (int) (Math.random() * totalRarity);

        int currentRaritySum = 0;
        for (CustomTreasure treasure : treasureList) {
            currentRaritySum += treasure.getRarity();
            if (rarityRoll < currentRaritySum) {
                return treasure;
            }
        }

        return null; // Fallback (should not happen)
    }
}