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

public class UnicaExtractor {
    
    public static void extractSingleMeal(Restaurant restaurant, Elements elements, String station, String mealPrices) {
        
        for (Element singleMeal : elements) {
            
            String mealName = UnicaExtractor.extractMealName(singleMeal, station);
            
            if (mealName == null) return;
            
            Set<String> allergens = UnicaExtractor.extractAllergens(singleMeal);
            
            Macros macros;
            macros = UnicaExtractor.extractMacros(singleMeal);
            
            Prices prices = new Prices(UnicaExtractor.extractPrices(mealPrices));
            String priceGroup = getPriceGroup(prices);
            
            Meal mealItem = new Meal(mealName, allergens, macros, priceGroup, prices);
            
            if (!restaurant.containsSameMeal(mealName)) restaurant.addMeal(mealItem);
        }
    }
    
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
    
    public static String extractOpeningHours(Elements element) {
        Element openingDate = element.selectFirst("h4");

        assert openingDate != null;
        String[] day = openingDate.text().split(" ");
        String today = day[0];

        Element openingHours = element.selectFirst("p");
        if (openingHours == null) {
            return null;
        }
        return openingHours.text().replace("Lunch served " , today + ": ").replace("–", "-").trim();
    }

    private static Set<String> extractAllergens(Element element){

        String allergenString = element.select("div[class=\"meal-item--name-container\"] > p").html();
        List<String> allergenList = Arrays.asList(allergenString.split(","));

        allergenList.replaceAll(s -> allergenMatcher(s.trim()));

        return new HashSet<>(allergenList);
    }

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

    private static String extractMealName(Element meal, String station) {
        StringBuilder sb = new StringBuilder();
        String singleMeal = Objects.requireNonNull(meal.select("span").first()).text();
        if (singleMeal.isEmpty()) return null;
        if (station.isEmpty()) return singleMeal;
        sb.append(singleMeal).append(" [").append(station).append("]");
        return sb.toString();
    }

    private static Prices extractPrices(String priceString){

        Pattern checkForNumbers = Pattern.compile("^(?:-[1-9](?:\\d{0,2}(?:,\\d{3})+|\\d*)|(?:0|[1-9](?:\\d{0,2}(?:,\\d{3})+|\\d*)))(?:.\\d+|)$");

        Prices prices = new Prices();
        ArrayList<String> parsedPrices = new ArrayList<>(List.of(priceString.split("/")));

        parsedPrices.removeIf(priceElement -> priceElement.contains("g"));
        parsedPrices.replaceAll(s -> s.replace(",", "."));
        parsedPrices.replaceAll(s -> s.replace("€", ""));
        parsedPrices.replaceAll(s -> s.replace(" ", ""));

        boolean isNumber =  false;
        try {
            isNumber = checkForNumbers.matcher(parsedPrices.getFirst()).matches();
        } catch (IndexOutOfBoundsException | NoSuchElementException e) {
            Logger.getLogger(UnicaExtractor.class.getName()).log(Level.WARNING, e.getMessage());
        }

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
    
    private static void extractOtherMacros(Macros macs, Element element) {
        if (element.text().toLowerCase().startsWith("saturated")) return;
        String[] macros = element.text().toLowerCase().trim().split(" ");
        
        String name = macros[0].toLowerCase();
        float amount = Float.parseFloat(macros[1]);
        String quantity = macros[2].toLowerCase();
        
        MacroTuple<Float, String> macro = new MacroTuple<>(amount, quantity);
        
        macs.mapMacros(name, macro);
    }
    
    private static float extractCalories(Element element) {
        String calorieString = element.getAllElements().getLast().text();   // Returns e.g. "754 kJ, 180 kcal"
        String[] parts = calorieString.split(" ");
        return Float.parseFloat(parts[parts.length - 2]);
    }
}
