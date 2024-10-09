package com.crunchiest.data;

import org.bukkit.Material;

import java.util.List;

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
 * Represents a treasure with attributes such as name, description, material, and rarity.
 */
public class CustomTreasure {
    private final String name;
    private final int rarity;
    private final int quantity;

    /**
     * Constructs a CustomTreasure object with specified attributes.
     *
     * @param name        The name of the treasure.
     * @param description A list of strings describing the treasure.
     * @param material    The material type of the treasure.
     * @param rarity      An integer representing the rarity of the treasure.
     */
    public CustomTreasure(String name, int rarity, int quantity) {
        this.name = name;
        this.rarity = rarity;
        this.quantity = quantity;
    }

    /**
     * Gets the name of the treasure.
     *
     * @return The name of the treasure.
     */
    public String getName() {
        return name;
    }

    public int getQuantity(){
      return quantity;
    }


    /**
     * Gets the rarity of the treasure.
     *
     * @return An integer representing the rarity of the treasure.
     */
    public int getRarity() {
        return rarity;
    }

    /**
     * Returns a string representation of the treasure, including its name, material, and rarity.
     *
     * @return A formatted string representing the treasure.
     */
    @Override
    public String toString() {
        return String.format("Treasure{name='%s', material=%s, rarity=%d}", name, rarity);
    }

}