package com.eikka.universityMenuScraper.helpers.unica;

import com.eikka.universityMenuScraper.components.Meal;
import com.eikka.universityMenuScraper.components.Prices;
import com.eikka.universityMenuScraper.components.Restaurant;
import com.eikka.universityMenuScraper.components.macros.MacroTuple;
import com.eikka.universityMenuScraper.components.macros.Macros;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * <p>{@link UnicaExtractor} is responsible for extracting all the information for a single meal for the restaurant</p>
 */
public class UnicaExtractor {
    
    /**
     * "Main" method for extracting the information from a singular meal from the current restaurant
     * @param restaurant The restaurant where the current meal in question is served
     * @param elements All the information for the current meal item
     * @param station The station string where the meal is served in the restaurant in question
     * @param mealPrices Prices for all the clientele for the current meal; can be "3,10 / 7,70 / 9,20" or just "12"
     */
    public static void extractSingleMeal(Restaurant restaurant, Elements elements, String station, String mealPrices) {
        
        for (Element singleMeal : elements) {
            
            String mealName = UnicaExtractor.extractMealName(singleMeal, station);
            
            // If the meal is jus an "empty box", no need to parse it any further
            if (mealName == null) return;
            
            Set<String> allergens = UnicaExtractor.extractAllergens(singleMeal);
            
            Macros macros;
            macros = UnicaExtractor.extractMacros(singleMeal);
            
            Prices prices = new Prices(UnicaExtractor.extractPrices(mealPrices));
            String priceGroup = getPriceGroup(prices);
            
            Meal mealItem = new Meal(mealName, allergens, macros, priceGroup, prices);
            
            // Ensure that the restaurant has no duplicates for a meal
            if (!restaurant.containsSameMeal(mealName)) restaurant.addMeal(mealItem);
        }
    }
    
    /**
     * Unica misses price ranges for their meals so for that reason arbitrary price classes are created.
     * Price class is defined by the student meals price, and it tries to match the price ranges for Kårkaféerna
     * @param prices {@link Prices} class that has all the prices for each client
     * @return {@link String} that matches the price range for the current meal
     */
    private static String getPriceGroup(Prices prices) {
        float studentPrice = prices.getStudents();
        
        if (studentPrice < 2.8) {
            return "Other";
        }  else if (studentPrice < 3.3) {
            return "Normal";
        } else if (studentPrice < 5.7) {
            return "Deli";
        }  else {
            return "Special";
        }
    }
    
    /**
     * Searches the {@link Element} that contains which day it is and the string at what time the lunch is served for the current day, e.g. "Lunch served 10.30–20.00"
     * After finding the Strings, method formats it to a bit nicer style, because weekly opening hours are in another part of the webpage.
     * @param element Contains the element where is the opening hours for the current day
     * @return Neatly formatted {@link String} where the opening hours are displayed for the day
     */
    public static String extractOpeningHours(Elements element) {
        Element openingDate = element.selectFirst("h4");

        if (openingDate == null) return null;
        String[] day = openingDate.text().split(" ");
        String today = day[0];

        Element openingHours = element.selectFirst("p");
        if (openingHours == null) {
            return null;
        }
        return openingHours.text().replace("Lunch served " , today + ": ").replace("–", "-").trim();
    }
    
    /**
     * Return a set containing all the allergens, that can be appended to a singular meal item
     * @param element Element containing the {@link String} of allergen abbreviations, e.g. "A, G, Veg."
     * @return {@link Set} containing each of the allergens that are present for the meal
     */
    private static Set<String> extractAllergens(Element element){

        String allergenString = element.select("div[class=\"meal-item--name-container\"] > p").html();
        List<String> allergenList = Arrays.asList(allergenString.split(","));

        allergenList.replaceAll(s -> allergenMatcher(s.trim()));

        return new HashSet<>(allergenList);
    }
    
    /**
     * Match the (Finnish) abbreviation of the allergen to it's corresponding English full name
     * @param singleAllergen The abbreviation of the allergen
     * @return The full name of the allergen
     */
    private static String allergenMatcher(String singleAllergen){

        return switch (singleAllergen) {
            case "G" -> "Gluten-free";
            case "L" -> "Lactose-free";
            case "VL" -> "Low lactose";
            case "M" -> "Dairy-free";
            case "Veg" -> "Suitable for vegans";
            case "VS" -> "Contains fresh garlic";
            case "A" -> "Contains allergens";
            default -> "";
        };
    }
    
    /**
     * <p>Extracts the name for a meal and formats it to its own "station" that Unica has for some restaurants, for example Assarin Ullakko.</p>
     * <p>Kårkaféerna doesn't have separate stations so the station information is formatted to the name of the meal </p>
     * <p>the Station can be same for more than one meal, which is why it is given as a parameter, and if there is no specified station present the meals name is presented as is</p>
     * <p>Sometimes the meal slot can be left accidentally empty, and in that case this method returns {@code null}</p>
     * @param meal The element that has the meal name present
     * @param station The station {@link String} where the corresponding meal is served at the moment
     * @return Formatted meal name with the station where it is served (if present)
     */
    private static String extractMealName(Element meal, String station) {
        StringBuilder sb = new StringBuilder();
        
        // First span element contains the name for the meal
        String singleMeal = Objects.requireNonNull(meal.select("span").first()).text();
        
        // If the meal slot was empty, return null
        if (singleMeal.isEmpty()) return null;
        if (station.isEmpty()) return singleMeal;
        
        // Append the name of the station between [square brackets]
        sb.append(singleMeal).append(" [").append(station).append("]");
        return sb.toString();
    }
    
    /**
     * Extracts the price data from single meals price.
     * The prices of the meal can vary from one single price for all the clients of a restaurant, or different prices for all.
     * This checks how many separate prices there are, and returns {@link Prices} instance that patches said meal.
     * This enables checking only one client groups prices later.
     * @param priceString {@link String} right after the name of the meal that is to be parsed
     * @return {@link Prices} entity containing all the corresponding price for a singular meal
     */
    private static Prices extractPrices(String priceString){

        // Pattern to check that a number is a number, and not something other
        Pattern checkForNumbers = Pattern.compile("^(?:-[1-9](?:\\d{0,2}(?:,\\d{3})+|\\d*)|(?:0|[1-9](?:\\d{0,2}(?:,\\d{3})+|\\d*)))(?:.\\d+|)$");

        Prices prices = new Prices();
        
        // Separates all the meal prices to an Array
        ArrayList<String> parsedPrices = new ArrayList<>(List.of(priceString.split("/")));

        // Handles some odd outliers and parses the elements to format that can be changed to a float
        parsedPrices.removeIf(priceElement -> priceElement.contains("g"));
        parsedPrices.replaceAll(s -> s.replace(",", "."));
        parsedPrices.replaceAll(s -> s.replace("€", ""));
        parsedPrices.replaceAll(s -> s.replace(" ", ""));

        boolean isNumber =  false;
        
        // Checks that the parsed prices are numbers
        try {
            isNumber = checkForNumbers.matcher(parsedPrices.getFirst()).matches();
        } catch (IndexOutOfBoundsException | NoSuchElementException e) {
            Logger.getLogger(UnicaExtractor.class.getName()).log(Level.WARNING, e.getMessage());
        }
        
        // Matches prices to their respective clientele
        // If only one price is present, it is assigned to all clients
        // If two, the lower one is for students and rest are for others
        // This puts lowest price for students and the prices get progressively higher
        if(isNumber){
            switch (parsedPrices.size()) {
                case 1:
                    prices.setStudents(Float.parseFloat(parsedPrices.getFirst()));
                    prices.setResearcherStudents(Float.parseFloat(parsedPrices.getFirst()));
                    prices.setStaff(Float.parseFloat(parsedPrices.getFirst()));
                    prices.setOthers(Float.parseFloat(parsedPrices.getFirst()));
                    break;
                case 2:
                    prices.setStudents(Float.parseFloat(parsedPrices.getFirst()));
                    prices.setResearcherStudents(Float.parseFloat(parsedPrices.get(1)));
                    prices.setStaff(Float.parseFloat(parsedPrices.get(1)));
                    prices.setOthers(Float.parseFloat(parsedPrices.get(1)));
                    break;
                case 3:
                    prices.setStudents(Float.parseFloat(parsedPrices.getFirst()));
                    prices.setResearcherStudents(Float.parseFloat(parsedPrices.get(1)));
                    prices.setStaff(Float.parseFloat(parsedPrices.get(2)));
                    prices.setOthers(Float.parseFloat(parsedPrices.get(2)));
                    break;
                case 4:
                    prices.setStudents(Float.parseFloat(parsedPrices.getFirst()));
                    prices.setResearcherStudents(Float.parseFloat(parsedPrices.get(1)));
                    prices.setStaff(Float.parseFloat(parsedPrices.get(2)));
                    prices.setOthers(Float.parseFloat(parsedPrices.get(3)));
                    break;

            }
        }

        return prices;
    }
    
    /**
     * Takes the whole element containing the Nutrients table, and row-by-row parses the contents
     * @param element Nutrients table
     * @return {@link Macros} class containing all the singular {@link MacroTuple} instances "macros"
     */
    private static Macros extractMacros(Element element) {
        
        Macros macros = new Macros();
        
        try {
            // Remove the "Per 100g" text from the table
            element.getElementsByTag("tr").getFirst().remove();
        } catch (NoSuchElementException _) { return null; }
        
        Elements tableRows = element.getElementsByTag("tr");    // Get each row of nutrients
        
        float cals = extractCalories(tableRows.getFirst()); // Get calories from the first row
        MacroTuple<Float, String> calories = new MacroTuple<>(cals, "kcal");
        macros.setCalories(calories);
        
        tableRows.removeFirst();
        
        for (Element tableRow : tableRows) {
            extractOtherMacros(macros, tableRow);
        }
        
        return macros;
    }
    
    /**
     * Method needed to parse other macros than calories.
     * Extracts macros from the element containing the current row of the table, and from there picks macros name and value.
     * @param macs  Macros class that contains all the macros in a key-value pair
     * @param element Current HTML table row containing wanted key and value
     */
    private static void extractOtherMacros(Macros macs, Element element) {
        
        // No need for separate instance for saturated fats
        if (element.text().toLowerCase().startsWith("saturated")) return;
        String[] macros = element.text().toLowerCase().trim().split(" ");
        
        String name = macros[0].toLowerCase();
        float amount = Float.parseFloat(macros[1]);
        String quantity = macros[2].toLowerCase();
        
        // Utilise MacroTuple class for easy formatting later
        MacroTuple<Float, String> macro = new MacroTuple<>(amount, quantity);
        
        macs.mapMacros(name, macro);
    }
    
    /**
     * Extracts energy per 100g from the "Nutritional value" string.
     * @param element The table element that contains "Ingredients" and "Nutritional value"
     * @return Numeral amount of calories per 100g
     */
    private static float extractCalories(Element element) {
        String calorieString = element.getAllElements().getLast().text();   // Returns e.g. "754 kJ, 180 kcal"
        String[] parts = calorieString.split(" ");
        
        // Pick the second to last value, which is kcals per 100g, dismiss all others
        return Float.parseFloat(parts[parts.length - 2]);
    }
}
