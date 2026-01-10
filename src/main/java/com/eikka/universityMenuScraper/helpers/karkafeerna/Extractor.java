package com.eikka.universityMenuScraper.helpers.karkafeerna;

import com.eikka.universityMenuScraper.components.Prices;
import com.eikka.universityMenuScraper.components.macros.MacroTuple;
import com.eikka.universityMenuScraper.components.macros.Macros;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class Extractor {

    /**
     * Helper class to format the macro string to a proper key-value pair hashmap
     * @param macroString String containing all the macros
     * @return Formatted key value pair containing units and their respective amounts
     */
    public static Macros extractMacros(String macroString){

        // Remove the "100 g innehåller" from the string
        String[] macroArray = macroString.split(":");

        // Make an array of each of the macros
        String[] contents = macroArray[1].trim().split(",");

        Macros macros = new Macros();

        for (String content : contents) {

            // Separate each macro element to words and numbers
            List<String> al = new ArrayList<>(List.of(content.trim().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")));

            // e.g. protein
            String unit = al.getFirst().trim().toLowerCase();

            // e.g. kcal
            String testQuantity = al.getLast().replace(".", "");

            // Remove first and last, e.g. energi & kcal
            al.removeFirst();
            al.removeLast();

            StringBuilder sb = new StringBuilder();

            // Combine rest of array to a string and parse to a float
            for (String s : al) {
                sb.append(s);
            }

            // float amount of the current unit
            float amount = Float.parseFloat(sb.toString());

            MacroTuple<Float, String> macroTuple = new MacroTuple<>(amount, testQuantity);
            macros.mapMacros(unit, macroTuple);
        }

        return macros;
    }

    /**
     * Parses the {@link Element} object to a {@link String} that can be used to extract prices
     * @param element Containing HTML code where the prices are
     * @return {@link Prices} Object containing all the prices
     */
    public static Prices extractAllPrices(Element element){
        Elements elementContainingPrices = element.getElementsByClass("info");
        Prices prices = new Prices();

        for (Element e : elementContainingPrices) {
            String originalPrices = e.children().get(1).text();

            if (!originalPrices.isEmpty()){
                prices = extractSinglePrice(originalPrices);
            }
        }

        return prices;
    }

    /**
     * Parses a single {@link String} that contains all the prices for all clientele of a restaurant
     * @param priceString {@link String} Containing all the price information in an unformatted manner
     * @return {@link Prices} Object where the prices are mapped to each clientele
     */
    private static Prices extractSinglePrice(String priceString){

        // From original price array: Studerande 3,10 € Forskarstud. 6,20 € Personal 7,70 € Andra 9.50 €
        // Picks singular prices: [Studerande 3,10 ,  Forskarstud. 6,20 ,  Personal 7,70 ,  Andra 9.50 ]
        String[] singlePrice = priceString.split("€");

        Prices prices = new Prices();

        for (String part : singlePrice){
            // Clean string: "Studerande 3,10 " -> "studerande 3,10"
            part = part.trim().toLowerCase();

            String[] parts = new String[2];

            if (part.startsWith("researcher stud")) {
                List<String> tempList = List.of(part.trim().split(" "));
                parts[0] = tempList.getFirst() + "_" + tempList.get(1).replace(".", "ents");
                parts[1] = tempList.get(2);
            } else {
                parts = part.trim().split(" ");
            }

            // Pick clientele and format the price to a float
            String clientele = parts[0];
            float amount = Float.parseFloat(parts[1].replace(",", "."));

            // Add price to the Prices object
            prices.mapPrices(clientele, amount);
        }

        return prices;
    }
}
