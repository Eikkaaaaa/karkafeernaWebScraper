package com.eikka.universityMenuScraper.controllers;

import com.eikka.universityMenuScraper.components.Restaurant;
import com.eikka.universityMenuScraper.service.RestaurantService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(("/api/v1/restaurants"))
public class RestaurantInfoController {

    private final RestaurantService restaurantService;

    public RestaurantInfoController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public ResponseEntity<Resource> getMeals(){
        return restaurantService.getAllInfo();
    }

    @GetMapping("/{name}")
    public ResponseEntity<Restaurant> getRestaurant(@PathVariable String name) throws IOException {
        Restaurant restaurant = restaurantService.restaurantByName(name);
        if (restaurant != null) {
            return ResponseEntity.ok().body(restaurant);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/formatted")
    public List<Restaurant> getGql() throws IOException {
        return restaurantService.getRestaurants();
    }
}
