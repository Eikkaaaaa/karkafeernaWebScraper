package com.example.karkafeernaWebScraper.helpers;

import com.example.karkafeernaWebScraper.records.AllRestaurants;
import com.example.karkafeernaWebScraper.records.Restaurant;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Scraper {

    public Scraper() {
    }

    /**
     * Method to parse the HTML data from Kårkaféernas website
     * @return {@link AllRestaurants} class that holds the data for restaurants and their respective menus
     */
    public AllRestaurants allRestaurants() {
        Elements elements = this.getDoc().select(".row.lunch-item ");
        Set<Restaurant> allRestaurants = new LinkedHashSet<>();

        // TODO: Add allergens as well
        for (Element element : elements) {
            String restaurantName = element.select("img[^assets/images/logos/restaurant], [alt]").attr("alt");

            Elements foods = element.getElementsByClass("food");
            Set<String> meals = new HashSet<>();

            for (Element food : foods) {
                meals.add(food.text());
            }

            allRestaurants.add(new Restaurant(restaurantName, meals));
        }

        return new AllRestaurants(allRestaurants);
    }

    /**
     * Fetches the Kårkaféernas website
     * @return {@link Document} instance that contains all the HTML to be parsed
     */
    private Document getDoc() {
        try {
            // TODO: Update with the "proper" address after the vacations
            Document document = Jsoup.connect("https://www.karkafeerna.fi/?year=2026&week=03").get();
            document.select(".food-star").remove();
            return document;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
