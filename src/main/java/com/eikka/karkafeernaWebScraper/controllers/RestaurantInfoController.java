package com.eikka.karkafeernaWebScraper.controllers;

import com.eikka.karkafeernaWebScraper.components.Restaurant;
import com.eikka.karkafeernaWebScraper.helpers.JSONMapper;
import com.eikka.karkafeernaWebScraper.service.RestaurantService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping(("/api/v1"))
public class RestaurantInfoController {

    private final RestaurantService restaurantService;

    public RestaurantInfoController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping("/meals")
    public ResponseEntity<Resource> getMeals(){
        return restaurantService.getAllInfo();
    }

    @GetMapping("/gql")
    public List<Restaurant> getGql() throws IOException {
        return restaurantService.getRestaurants();
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
