package com.crunchiest.data;

import org.bukkit.Material;
import java.util.List;

public class CustomTreasure {

    private final String name;
    private final List<String> description;
    private final Material material;
    private final int rarity;

    public CustomTreasure(String name, List<String> description, Material material, int rarity) {
        this.name = name;
        this.description = description;
        this.material = material;
        this.rarity = rarity;
    }

    public String getName() {
        return name;
    }

    public List<String> getDescription() {
        return description;
    }

    public Material getMaterial() {
        return material;
    }

    public int getRarity() {
        return rarity;
    }
}