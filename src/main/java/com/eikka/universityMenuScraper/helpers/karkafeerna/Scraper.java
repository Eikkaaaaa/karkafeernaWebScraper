package com.eikka.universityMenuScraper.helpers.karkafeerna;

import com.eikka.universityMenuScraper.components.AllRestaurants;
import com.eikka.universityMenuScraper.components.Meal;
import com.eikka.universityMenuScraper.components.Restaurant;
import com.eikka.universityMenuScraper.components.Prices;
import com.eikka.universityMenuScraper.components.macros.Macros;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

public class Scraper {

    public Scraper() {
    }


    /**
     * Method to parse the HTML data from Kårkaféernas website
     * @return {@link AllRestaurants} class that holds the data for restaurants and their respective menus
     */
    public AllRestaurants allRestaurants() {

        // Get all Elements containing lunches and restaurant names
        Elements elements = this.getDoc().select(".row.lunch-item ");

        // Initialise new class to contain each restaurant class
        AllRestaurants allRestaurants =  new AllRestaurants(new LinkedHashSet<>());

        addRestaurantToList(elements, allRestaurants);

        return allRestaurants;
    }

    /**
     * Loops through all the restaurant elements in the list and
     *  adds their respective menus to the {@link AllRestaurants} object
     * @param elements Restaurants with meals
     * @param allRestaurants Class to contain all parsed info
     */
    private void addRestaurantToList(Elements elements, AllRestaurants allRestaurants) {

        String[] mealSkipList = {
                "GALLERIET (11-14.30)", "ASTRA DELIGHTS (10.30-14.00)", "BUFFÉ SOLSIDAN (11-14)"
        };

        for (Element element : elements) {

            // Get the restaurants name from the logo image file
            String restaurantName = element.select("img[^assets/images/logos/restaurant], [alt]").attr("alt");

            String openingHours = getOpeningHours(element);

            Restaurant restaurant = new Restaurant(restaurantName, openingHours);

            // Get all meal items for current restaurant
            Elements meals = element.getElementsByClass("meal");

            // Loop through every single meal
            for (Element meal : meals) {
                Meal m = getMealInfo(meal);

                boolean isAstrasOpeningHour = Arrays.asList(mealSkipList).contains(m.name());
                boolean isAlreadyAddedToMeals = restaurant.getFoodItems().stream().anyMatch(foodItem -> foodItem.name().equals(m.name()));

                // If meal is not Astras opening hours, and not already on the meal list, add it to meals
                if (!isAstrasOpeningHour && !isAlreadyAddedToMeals) {
                    restaurant.addMeal(m);
                }
            }

            allRestaurants.addRestaurant(restaurant);
        }
    }

    /**
     * Fetches the opening hour string from the same element where the image for {@code restaurantName} parameter is located
     * @param element HTML element to search for the opening hours
     * @return Parsed opening hours string
     */
    private String getOpeningHours(Element element) {

        try {
            return element.select("p").first().text().trim();
        } catch (NullPointerException e) {
            return "No opening hours found";
        }
    }

    /**
     * Extract the wanted meal info from the given meal {@link Element}
     * @param meal {@link Element} The element that contains the wanted meal info
     * @return {@link Meal} record that contains wanted info for a meal
     */
    private Meal getMealInfo(Element meal){

        // Get the meals name
        String mealName = meal.getElementsByClass("food").text();

        // Get each allergen for a single meal
        List<String> allergens = meal.getElementsByClass("food-diet").eachAttr("title");

        // Empty Macros element to hold macros
        Macros macros = new Macros();

        if(meal.getElementsByClass("food").hasAttr("title")){
            macros = Extractor.extractMacros(meal.getElementsByClass("food").attr("title"));
        }

        // Get the price group of the meal
        String priceGroup = meal.getElementsByClass("group-title").text();

        // Get the prices for the meal
        Prices prices = Extractor.extractAllPrices(meal);

        // Create a meal object from the fetched meal info
        return new Meal(mealName, new LinkedHashSet<>(allergens), macros, priceGroup, prices);
    }

    /**
     * Fetches the Kårkaféernas website
     * @return {@link Document} instance that contains all the HTML to be parsed
     */
    private Document getDoc() {
        try {
            Document documentSE = Jsoup.connect("https://www.karkafeerna.fi/").get();
            Document documentEN = Jsoup.connect("https://www.karkafeerna.fi/en/lunch").get();
            documentEN.select(".food-star").remove();
            return documentEN;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
