package com.example.karkafeernaWebScraper.components;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Restaurant {

    private String restaurant;
    private LinkedHashSet<Meal> foodItems = new LinkedHashSet<>();

    public Restaurant(String name) {
        this.restaurant = name;
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
