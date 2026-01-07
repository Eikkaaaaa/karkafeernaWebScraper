package com.example.karkafeernaWebScraper.components;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Restaurant {

    private final String restaurant;
    private final String openingHours;
    private final LinkedHashSet<Meal> foodItems = new LinkedHashSet<>();

    public Restaurant(String name, String openingHours) {
        this.restaurant = name;
        this.openingHours = openingHours;
    }

    public void addMeal(Meal meal){
        foodItems.add(meal);
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "name='" + restaurant + '\'' +
                ", foodItems=" + foodItems +
                '}';
    }
}
