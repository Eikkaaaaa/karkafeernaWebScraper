package com.eikka.karkafeernaWebScraper.components;

import com.eikka.karkafeernaWebScraper.components.macros.Macros;

import java.util.Set;

public record Meal(String name,
                   Set<String> allergens,
                   Macros macros,
                   String priceGroup,
                   Prices prices) {
}
