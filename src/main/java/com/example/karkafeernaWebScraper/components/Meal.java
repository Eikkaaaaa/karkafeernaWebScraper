package com.example.karkafeernaWebScraper.components;

import java.util.LinkedHashMap;
import java.util.Set;

public record Meal(String name,
                   Set<String> allergens,
                   LinkedHashMap<String, Float> macros_per_100g,
                   String priceGroup,
                   LinkedHashMap<String, Float> prices) {
}
