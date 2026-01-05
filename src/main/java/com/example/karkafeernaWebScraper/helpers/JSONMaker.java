package com.example.karkafeernaWebScraper.helpers;

import com.google.gson.Gson;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Component
public class JSONMaker {

    private final Gson GSON = new Gson();

    public JSONMaker() {
    }

    /**
     * Calls {@link Scraper} daily at 00:15 to fetch updated menus,
     * then updates the JSON file automatically with the updated menu.
     * Small delay is to make sure to have updated data
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
    }
}
