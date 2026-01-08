package com.eikka.karkafeernaWebScraper.helpers;

import com.eikka.karkafeernaWebScraper.components.Restaurant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

@Component
public class JSONMapper {

    private final Gson GSON = new Gson();

    public JSONMapper() {
    }

    /**
     * <p>Calls {@link Scraper} daily at 00:15 to fetch updated menus, then updates the JSON file automatically with the updated menu.</p>
     * <p>Small delay is to make sure to have updated data</p>
     * <p>Also used for creating an easy-to-read JSON file for graphql, omitting the "updatedAt" and "source" parts</p>
     * @throws IOException IOException if file cannot be written
     */
    @Scheduled(cron = "0 15 0 * * *", zone = "Europe/Helsinki")
    public void createJSONFile() throws IOException {
        Scraper scraper = new Scraper();
        IO.println(GSON.toJson(scraper.allRestaurants()));
        File file = new File("files/restaurants.json");

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(GSON.toJson(scraper.allRestaurants()));
        } catch (IOException e) {
            throw new IOException(e);
        }

        file = new File("files/restaurants_gql.json");

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(GSON.toJson(scraper.allRestaurants().getRestaurants()));
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * Uses the {@code files/restaurants_gql.json} as a "database" to instantiate {@link Restaurant} objects from
     * @return A list containing all of the {@link Restaurant} objects with updated meal data for the day
     * @throws IOException exception if file cannot be read
     */
    public List<Restaurant> allRestaurants() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("files/restaurants_gql.json"));

        Type restaurantType = new TypeToken<List<Restaurant>>(){}.getType();
        return GSON.fromJson(br, restaurantType);
    }
}
