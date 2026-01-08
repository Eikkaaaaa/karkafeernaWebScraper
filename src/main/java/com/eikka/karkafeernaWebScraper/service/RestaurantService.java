package com.eikka.karkafeernaWebScraper.service;

import com.eikka.karkafeernaWebScraper.components.Restaurant;
import com.eikka.karkafeernaWebScraper.helpers.JSONMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class RestaurantService {

    private final JSONMapper jsonMapper;

    public RestaurantService(JSONMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public ResponseEntity<Resource> getAllInfo(){
        try {
            Path filePath = Paths.get("files/restaurants.json");
            Resource resource = new InputStreamResource(new FileInputStream(filePath.toFile()));

            if(!resource.exists()){
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok().body(resource);
        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    public List<Restaurant> getRestaurants() throws IOException {
        return this.jsonMapper.allRestaurants();
    }

    public Restaurant restaurantByName(String name) throws IOException {
        return jsonMapper.allRestaurants().stream().filter(restaurant -> restaurant.getName().equals(name)).findFirst().orElse(null);
    }
}
