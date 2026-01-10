package com.eikka.universityMenuScraper.controllers;

import com.eikka.universityMenuScraper.components.Restaurant;
import com.eikka.universityMenuScraper.service.RestaurantService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class RestaurantGqlController {

    private final RestaurantService restaurantService;

    public RestaurantGqlController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @QueryMapping
    public Restaurant restaurantByName(@Argument String name) throws IOException {
        return restaurantService.restaurantByName(name);
    }

    @QueryMapping
    public List<Restaurant> allRestaurants() throws IOException {
        return restaurantService.getRestaurants();
    }
}
