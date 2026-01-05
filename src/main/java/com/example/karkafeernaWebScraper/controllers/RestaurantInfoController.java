package com.example.karkafeernaWebScraper.controllers;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping(("/api/v1"))
public class RestaurantInfoController {

    @GetMapping("/meals")
    public ResponseEntity<Resource> getMeals(){
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
}
