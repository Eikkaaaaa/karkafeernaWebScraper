package com.example.karkafeernaWebScraper.records;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class AllRestaurants {

    private String updatedAt;
    private Set<Restaurant> restaurants;

    public AllRestaurants(Set<Restaurant> restaurants) {
        this.restaurants = restaurants;
        this.updatedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
