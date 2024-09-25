package com.crunchiest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a fish with attributes such as type, size, weight, and rarity.
 */
public class Fish {
    private String type;
    private double minLength; // Minimum length in cm
    private double maxLength; // Maximum length in cm
    private double minWeight; // Minimum weight in kg
    private double maxWeight; // Maximum weight in kg
    private int rarity; // Rarity weight (higher number means less rare)

    private static final Random RANDOM = new Random();
    private static final List<Fish> FISH_TYPES = new ArrayList<>();

    static {
        FISH_TYPES.add(new Fish("Goldfish", 5.0, 15.0, 0.01, 0.1, 70)); // Common
        FISH_TYPES.add(new Fish("Trout", 20.0, 30.0, 0.2, 1.0, 20)); // Common
        FISH_TYPES.add(new Fish("Salmon", 30.0, 40.0, 0.5, 1.5, 10)); // Moderate
        FISH_TYPES.add(new Fish("Bass", 40.0, 60.0, 1.0, 3.0, 5)); // Rare
        FISH_TYPES.add(new Fish("Tuna", 80.0, 120.0, 5.0, 30.0, 1)); // Very Rare
    }

    /**
     * Constructs a Fish object with specified attributes.
     *
     * @param type        the type of the fish
     * @param minLength   the minimum length in cm
     * @param maxLength   the maximum length in cm
     * @param minWeight   the minimum weight in kg
     * @param maxWeight   the maximum weight in kg
     * @param rarity      the rarity weight (higher number means less rare)
     */
    public Fish(String type, double minLength, double maxLength,
                double minWeight, double maxWeight, int rarity) {
        this.type = type;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.minWeight = minWeight;
        this.maxWeight = maxWeight;
        this.rarity = rarity;
    }

    /**
     * Returns the type of the fish.
     *
     * @return the type of the fish
     */
    public String getType() {
        return type;
    }

    /**
     * Returns a random length of the fish within its specified range.
     *
     * @return the random length of the fish in cm
     */
    public double getLength() {
        return getRandomLength(); // Get random length within the range
    }

    /**
     * Returns a random weight of the fish within its specified range.
     *
     * @return the random weight of the fish in kg
     */
    public double getWeight() {
        return getRandomWeight(); // Get random weight within the range
    }

    /**
     * Returns the rarity of the fish.
     *
     * @return the rarity weight
     */
    public int getRarity() {
        return rarity;
    }

    /**
     * Returns a formatted string containing the length and weight of the fish.
     *
     * @return a string representation of the fish's length and weight
     */
    public String getFormattedInfo() {
        return String.format("Length: %.2f cm, Weight: %.2f kg", getLength(), getWeight());
    }

    /**
     * Generates a random length for the fish, biased towards smaller lengths.
     *
     * @return the random length of the fish in cm
     */
    private double getRandomLength() {
        double randomValue = Math.pow(RANDOM.nextDouble(), 3); // Cube the random value to bias towards smaller fish
        return Math.round((minLength + (maxLength - minLength) * randomValue) * 100.0) / 100.0; // Round to 2 decimal places
    }

    /**
     * Generates a random weight for the fish, biased towards lighter weights.
     *
     * @return the random weight of the fish in kg
     */
    private double getRandomWeight() {
        double randomValue = Math.pow(RANDOM.nextDouble(), 3); // Cube the random value to bias towards lighter fish
        return Math.round((minWeight + (maxWeight - minWeight) * randomValue) * 100.0) / 100.0; // Round to 2 decimal places
    }

    /**
     * Creates and returns a random fish based on predefined rarity.
     *
     * @return a randomly selected Fish object
     */
    public static Fish createRandomFish() {
        Random random = new Random();

        // Calculate total rarity
        int totalRarity = FISH_TYPES.stream().mapToInt(Fish::getRarity).sum();
        int rarityRoll = random.nextInt(totalRarity);

        int currentRaritySum = 0;
        for (Fish fish : FISH_TYPES) {
            currentRaritySum += fish.getRarity();
            if (rarityRoll < currentRaritySum) {
                return fish;
            }
        }
        // Fallback in case no fish is found (should not happen)
        return FISH_TYPES.get(0);
    }
}