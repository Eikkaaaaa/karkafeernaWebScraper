package com.eikka.universityMenuScraper.service;

import com.eikka.universityMenuScraper.components.Restaurant;
import com.eikka.universityMenuScraper.helpers.JSONMapper;
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
            Path filePath = Paths.get("src/main/resources/data/restaurants.json");
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

        name = name.replaceAll("-", " ");
        
        String search = switch (name.toLowerCase()) {
            case "karen" -> "kåren";
            case "kisalli" -> "kisälli";
            case "henkilostoravintola-waino"  -> "henkilöstöravintola häinö";
            case "lemminkainen" -> "lemminkäinen";
            default -> name;
        };

        return jsonMapper.allRestaurants().stream().filter(restaurant -> restaurant.getName().equalsIgnoreCase(search)).findFirst().orElse(null);
    }
}
