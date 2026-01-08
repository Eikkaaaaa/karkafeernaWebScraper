package com.eikka.karkafeernaWebScraper.components;

import java.util.LinkedHashSet;

public class Restaurant {

    private final String name;
    private final String openingHours;
    private final LinkedHashSet<Meal> foodItems = new LinkedHashSet<>();

    public Restaurant(String name, String openingHours) {
        this.name = name;
        this.openingHours = openingHours;
    }

    public void addMeal(Meal meal){
        foodItems.add(meal);
    }

    public String getName() {
        return name;
    }
    public String getOpeningHours() {
        return openingHours;
    }

    public LinkedHashSet<Meal> getFoodItems() {
        return foodItems;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "name='" + name + '\'' +
                ", openingHours='" + openingHours + '\'' +
                ", foodItems=" + foodItems +
                '}';
    }
}
