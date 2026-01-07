package com.eikka.karkafeernaWebScraper.components;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;

public class AllRestaurants {

    private final String updatedAt;
    private final LinkedHashSet<Restaurant> restaurants;

    private final String source = "https://www.karkafeerna.fi";

    public AllRestaurants(LinkedHashSet<Restaurant> restaurants) {
        this.restaurants = restaurants;
        this.updatedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void addRestaurant(Restaurant restaurant) {
        restaurants.add(restaurant);
    }

    @Override
    public String toString() {
        return "AllRestaurants{" +
                "updatedAt='" + updatedAt + '\'' +
                ", restaurants=" + restaurants +
                '}';
    }
}
