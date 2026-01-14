package com.eikka.universityMenuScraper.components;

import java.util.LinkedHashSet;

public class Restaurant {

    private final String name;
    private String openingHours;
    private final LinkedHashSet<Meal> foodItems = new LinkedHashSet<>();

    public Restaurant(String name) {
        this.name = name;
    }

    public void addMeal(Meal meal){
        foodItems.add(meal);
    }

    public String getName() {
        return name;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public LinkedHashSet<Meal> getFoodItems() {
        return foodItems;
    }
    
    public boolean containsSameMeal(String mealName){
        
        for (Meal meal : foodItems){
            if (meal.name().equals(mealName)) return true;
        }
        return false;
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
