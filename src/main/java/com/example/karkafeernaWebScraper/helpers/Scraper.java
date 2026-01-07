package com.example.karkafeernaWebScraper.helpers;

import com.example.karkafeernaWebScraper.components.*;
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
                "GALLERIET (11-14.30)", "ASTRA DELIGHTS (10.30-14.00)"
        };

        for (Element element : elements) {

            // Get the restaurants name from the logo image file
            String restaurantName = element.select("img[^assets/images/logos/restaurant], [alt]").attr("alt");

            String openingHours = getOpeningHours(element);

            Restaurant restaurant = new Restaurant(restaurantName,  openingHours);

            // Get all meal items for current restaurant
            Elements meals = element.getElementsByClass("meal");

            // Loop through every single meal
            for (Element meal : meals) {
                Meal m = getMealInfo(meal);
                if (!Arrays.asList(mealSkipList).contains(m.name())) {
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

        // Get extra macros for a meal
        // If no macros are present the then Map will be empty
        LinkedHashMap<String, Float> macros = new LinkedHashMap<>();
        if(meal.getElementsByClass("food").hasAttr("title")){
            macros = formatMacros(meal.getElementsByClass("food").attr("title"));
        }

        // Get the price group of the meal
        String priceGroup = meal.getElementsByClass("group-title").text();

        // Get the prices for the meal
        LinkedHashMap<String, Float> prices = getPrices(meal);

        // Create a meal object from the fetched meal info
        return new Meal(mealName, new LinkedHashSet<>(allergens), macros, priceGroup, prices);
    }

    private LinkedHashMap<String, Float> getPrices(Element element){

        // Get the "class=info" elements that contain the prices
        Elements updatedMeal = element.getElementsByClass("info");

        LinkedHashMap<String, Float> prices = new LinkedHashMap<>();

        for(Element updatedMealInfo : updatedMeal){

            // Get the second sub-element from the updatedMealInfo Element
            String originalPrices = updatedMealInfo.children().get(1).text();

            if (!originalPrices.isEmpty()){

                // Format the string if the prices are present
                prices = formatPrices(originalPrices);
            }
        }
        return prices;
    }

    /**
     * Helper class to format the macro string to a proper key-value pair hashmap
     * @param macros String containing all the macros
     * @return Formatted key value pair containing units and their respective amounts
     */
    private LinkedHashMap<String, Float> formatMacros(String macros){

        // Remove the "100 g innehåller" from the string
        String[] macroArray = macros.split(":");

        // Make an array of each of the macros
        String[] contents = macroArray[1].trim().split(",");

        LinkedHashMap<String, Float> macroMap = new LinkedHashMap<>();

        for (String content : contents) {

            // Separate each macro element to words and numbers
            List<String> al = new ArrayList<>(List.of(content.trim().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")));

            // First element is the unit and second is the amount, e.g. "Energi(kcal)"
            String unit = al.getFirst().trim() + "(" + al.getLast().trim().replace(".", "") + ")";

            // Remove first and last, e.g. "Energi" & kcal
            al.removeFirst();
            al.removeLast();

            StringBuilder sb = new StringBuilder();

            // Combine rest of array to a string and parse to a float
            for (String s : al) {
                sb.append(s);
            }

            float amount = Float.parseFloat(sb.toString());

            macroMap.put(unit, amount);
        }

        return macroMap;
    }

    /**
     * Parses the prices from the input string and picks the clientele
     * and matches them with the correct pricing, converting the price from String to a Float
     * @param pricesString The string containing all the prices
     * @return A Linked hashMap that contains the clientele with their respective pricing as a key-value pair
     */
    private LinkedHashMap<String, Float> formatPrices(String pricesString){

        // Split the string with the € letter
        String[] prices = pricesString.split("€");
        LinkedHashMap<String, Float> priceMap = new LinkedHashMap<>();

        for (String price : prices){

            // Split the string with " " and transform it to ArrayList for easy manipulation
            String[] singlePrice = price.split(" ");
            ArrayList<String> parts = new ArrayList<>(Arrays.asList(singlePrice));

            // If the first item is empty, remove it
            if (parts.getFirst().isEmpty()){
                parts.removeFirst();
            }

            // Put the clientele first and then convert the price to a float and ad it to map
            priceMap.put(parts.get(0), Float.parseFloat(parts.get(1).replace(",", ".")));
        }

        return priceMap;
    }

    /**
     * Fetches the Kårkaféernas website
     * @return {@link Document} instance that contains all the HTML to be parsed
     */
    private Document getDoc() {
        try {
            Document document = Jsoup.connect("https://www.karkafeerna.fi/").get();
            document.select(".food-star").remove();
            return document;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
