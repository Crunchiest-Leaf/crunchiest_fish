package com.crunchiest.data;

import java.util.List;
import java.util.Random;

import org.bukkit.entity.EntityType;

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
 * Represents a fish with attributes such as type, size, weight, rarity, and description.
 */
public class CustomFish {
  private String type;
  private double length; // Randomly generated length for this fish instance
  private double weight; // Randomly generated weight for this fish instance
  private double minLength; // Minimum length
  private double maxLength; // Maximum length
  private double minWeight; // Minimum weight
  private double maxWeight; // Maximum weight
  private int rarity;
  private List<String> description;
  private EntityType entityType;

  private static final Random RANDOM = new Random();

  /**
   * Constructs a CustomFish object with specified attributes and generates random length/weight.
   */
  public CustomFish(String type, double minLength, double maxLength,
            double minWeight, double maxWeight, int rarity, List<String> description, EntityType entityType) {
      this.type = type;
      this.minLength = minLength;
      this.maxLength = maxLength;
      this.minWeight = minWeight;
      this.maxWeight = maxWeight;
      this.rarity = rarity;
      this.description = description;
      this.entityType = entityType;

      // Generate random length and weight on fish creation
      this.length = getRandomLength(minLength, maxLength);
      this.weight = getRandomWeight(minWeight, maxWeight);
  }

  // Getter for entity type
  public EntityType getEntityType() {
      return entityType;
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
   * Returns the length of the fish.
   *
   * @return the length of the fish in cm
   */
  public double getLength() {
      return length; // Return the stored length
  }

  /**
   * Returns the weight of the fish.
   *
   * @return the weight of the fish in kg
   */
  public double getWeight() {
      return weight; // Return the stored weight
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
      return String.format("Type: %s, Length: %.2f cm, Weight: %.2f kg", 
          type, getLength(), getWeight());
  }

  /**
   * Returns the description of the fish as a formatted string.
   *
   * @return a string representation of the fish's description
   */
  public List<String> getDescription() {
      return description;
  }

  // New getters for min/max length and weight

  /**
   * Returns the minimum length of the fish.
   *
   * @return the minimum length in cm
   */
  public double getMinLength() {
      return minLength;
  }

  /**
   * Returns the maximum length of the fish.
   *
   * @return the maximum length in cm
   */
  public double getMaxLength() {
      return maxLength;
  }

  /**
   * Returns the minimum weight of the fish.
   *
   * @return the minimum weight in kg
   */
  public double getMinWeight() {
      return minWeight;
  }

  /**
   * Returns the maximum weight of the fish.
   *
   * @return the maximum weight in kg
   */
  public double getMaxWeight() {
      return maxWeight;
  }

  /**
   * Generates a random length for the fish, biased towards smaller lengths.
   *
   * @param minLength the minimum length in cm
   * @param maxLength the maximum length in cm
   * @return the random length of the fish in cm
   */
  private double getRandomLength(double minLength, double maxLength) {
      double randomValue = Math.pow(RANDOM.nextDouble(), 3); // Cube the random value to bias towards smaller fish
      return Math.round((minLength + (maxLength - minLength) * randomValue) * 100.0) / 100.0; // Round to 2 decimal places
  }

  /**
   * Generates a random weight for the fish, biased towards lighter weights.
   *
   * @param minWeight the minimum weight in kg
   * @param maxWeight the maximum weight in kg
   * @return the random weight of the fish in kg
   */
  private double getRandomWeight(double minWeight, double maxWeight) {
      double randomValue = Math.pow(RANDOM.nextDouble(), 3); // Cube the random value to bias towards lighter fish
      return Math.round((minWeight + (maxWeight - minWeight) * randomValue) * 100.0) / 100.0; // Round to 2 decimal places
  }
}