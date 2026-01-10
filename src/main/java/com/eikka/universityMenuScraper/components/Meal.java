package com.eikka.universityMenuScraper.components;

import com.eikka.universityMenuScraper.components.macros.Macros;

import java.util.Set;

public record Meal(String name,
                   Set<String> allergens,
                   Macros macros,
                   String priceGroup,
                   Prices prices) {
}
