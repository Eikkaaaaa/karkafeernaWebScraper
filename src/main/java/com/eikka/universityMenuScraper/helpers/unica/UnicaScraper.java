package com.eikka.universityMenuScraper.helpers.unica;

import com.eikka.universityMenuScraper.components.Meal;
import com.eikka.universityMenuScraper.components.Prices;
import com.eikka.universityMenuScraper.components.Restaurant;
import com.eikka.universityMenuScraper.components.macros.MacroTuple;
import com.eikka.universityMenuScraper.components.macros.Macros;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jspecify.annotations.NonNull;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnicaScraper {

    public boolean isScrapingDone;
    private final Logger logger = Logger.getLogger(UnicaScraper.class.getName());
    private final String[] restaurantURLs = {
            "https://www.unica.fi/en/restaurants/university-campus/assarin-ullakko/",
            "https://www.unica.fi/en/restaurants/university-campus/galilei/",
            "https://www.unica.fi/en/restaurants/university-campus/macciavelli/",
            "https://www.unica.fi/en/restaurants/university-campus/monttu-ja-mercatori/",
            "https://www.unica.fi/en/restaurants/kupittaa-campus/deli-pharma/",
            "https://www.unica.fi/en/restaurants/kupittaa-campus/delica/",
            "https://www.unica.fi/en/restaurants/kupittaa-campus/dental/",
            "https://www.unica.fi/en/restaurants/kupittaa-campus/kisalli/",
            "https://www.unica.fi/en/restaurants/kupittaa-campus/linus/",
            "https://www.unica.fi/en/restaurants/art-campus/sigyn/",
            "https://www.unica.fi/en/restaurants/others/unican-kulma/",
            "https://www.unica.fi/en/restaurants/others/fabrik-cafe/",
            "https://www.unica.fi/en/restaurants/others/piccu-maccia/",
            "https://www.unica.fi/en/restaurants/others/puutorin-nurkka/",
            "https://www.unica.fi/en/restaurants/other-restaurants/henkilostoravintola-waino/",
            "https://www.unica.fi/en/restaurants/other-restaurants/kaffeli/",
            "https://www.unica.fi/en/restaurants/other-restaurants/kaivomestari/",
            "https://www.unica.fi/en/restaurants/other-restaurants/lemminkainen/",
            "https://www.unica.fi/en/restaurants/other-restaurants/mairela/",
            "https://www.unica.fi/en/restaurants/other-restaurants/rammeri/",
            "https://www.unica.fi/en/restaurants/other-restaurants/ruokakello/"
    };

    public UnicaScraper() {
    }

    public LinkedHashSet<Restaurant> getAllRestaurants() {

        LinkedHashSet<Restaurant> restaurants = new LinkedHashSet<>();
        Map<String, Elements> restaurantHTML = this.scrapeRestaurants();
        
        addRestaurantToList(restaurants, restaurantHTML);

        return restaurants;
    }
    
    private static void addRestaurantToList(LinkedHashSet<Restaurant> restaurants, Map<String, Elements> restaurantHTML) {
        
        for (Map.Entry<String, Elements> list : restaurantHTML.entrySet()) {
            Restaurant restaurant = new Restaurant(list.getKey());
            
            String openingHours = UnicaExtractor.extractOpeningHours(list.getValue());
            restaurant.setOpeningHours(openingHours);
            
            for (Element element : list.getValue()) {
                
                // Get a list of "stations" that have separate serving hours, e.g. "STATION 1-2 10.30-15.00"
                Elements meals = element.getElementsByClass("lunch-menu-block__menu-package");
                
                addMealToRestaurant(restaurant, meals);
                
            }
            restaurants.add(restaurant);
        }
    }
    
    private static void addMealToRestaurant(Restaurant restaurant, Elements meals) {
        
        for (Element meal : meals) {
            
            String station = Objects.requireNonNull(meal.selectFirst("h5")).text();
            String mealPrices = Objects.requireNonNull(meal.selectFirst("p")).text();
            
            Elements singleMeals = meal.getElementsByClass("meal-item");
            
            UnicaExtractor.extractSingleMeal(restaurant, singleMeals, station, mealPrices);
            
        }
    }

    private Map<String, Elements> scrapeRestaurants() {

        this.isScrapingDone = false;

        // Define the options with what the Selenium Chromium browser is used with
        ChromeOptions options = getChromeOptions();

        // Empty list to hold all the meals per restaurant
        Map<String, Elements> list = new HashMap<>();

        // Make sure that earlier instance of webdriver does not exist
        WebDriver driver = null;

        try {

            // Instantiate a new web driver
            driver = new ChromeDriver(options);

            primeUnicaSession(driver);

            for (String url : this.restaurantURLs) {

                IO.println("Scraping for URL:\n\t" + url);

                try {

                    // For each URL, get the contents
                    driver.get(url);
                    boolean menuPresent = checkMenuPresence(driver);

                    // Continue scraping for restaurants that have a menu for the day
                    if (menuPresent) addRestaurantToList(driver, list);

                } catch (Exception e) {
                    logger.log(Level.WARNING, "Exception while scraping document", e);
                }
            }
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }

        this.isScrapingDone = true;
        return list;
    }

    /**
     * Go to the unicas webpage to click away the cookie banner so that we can click open all the meals to get to nutritional facts
     * @param driver The driver that handles the scraping of the websites
     */
    private static void primeUnicaSession(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        driver.get("https://www.unica.fi/en/");

        try {
            WebElement decline = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.id("declineButton")
                    )
            );

            decline.click();

            wait.until(ExpectedConditions.invisibilityOf(decline));

        } catch (TimeoutException ignored) {}
    }

    /**
     * Checks if the menu os present for the current day for the restaurant
     * @param driver Contains the contents for a single restaurant
     * @return <p>{@code true} if there is menu for the day</p> <p>{@code false} if the restaurant serves no food today</p>
     */
    private boolean checkMenuPresence(WebDriver driver) {

        // Check if the menu is present for the day
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.presenceOfElementLocated(
                            By.className("lunch-menu-block__menu-package")
                    ));
            return true;
        } catch (TimeoutException e) {
            //logger.log(Level.INFO, "Timeout while scraping document", e);
            return false;
        }
    }

    /**
     * <p>Handle adding the restaurants menu to the list</p>
     * <p>Gets the name of the restaurant from the title property</p>
     * <p>Lunch from the lunch meny block element</p>
     * @param driver {@link WebDriver} instance that handles the scraping
     * @param list {@link Map} Where the menus are added to
     *                        <p>key is the restaurants name</p>
     *                        <p>value is the entire menu, containing all info from meals to allergens</p>
     */
    private void addRestaurantToList(WebDriver driver, Map<String, Elements> list) throws InterruptedException {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(6));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("button.compass-accordion__header")
        ));

        // Expand all accordion panels
        List<WebElement> accordions = driver.findElements(
                By.cssSelector("button.compass-accordion__header")
        );

        for (WebElement accordion : accordions) {

            js.executeScript(
                    "arguments[0].scrollIntoView({block: 'center'});",
                    accordion
            );

            wait.until(ExpectedConditions.elementToBeClickable(accordion));

            if ("false".equals(accordion.getAttribute("aria-expanded"))) {
                accordion.click();

                // Wait until accordion expansion is complete
                wait.until(ExpectedConditions.attributeToBe(
                        accordion,
                        "aria-expanded",
                        "true"
                ));
            }

            String expanded = accordion.getAttribute("aria-expanded");

            if ("false".equals(expanded)) {
                accordion.click();

                // Wait for Vue state transition to complete
                try {
                    wait.until(ExpectedConditions.attributeToBe(
                            accordion,
                            "aria-expanded",
                            "true"
                    ));
                }  catch (TimeoutException e) {
                    logger.log(Level.WARNING, "Address that failed: " + driver.getCurrentUrl());
                    logger.log(Level.WARNING, "Exception while scraping document", e);
                }
            }
        }

        wait.until(driverInstance -> {
            List<WebElement> items = driverInstance.findElements(
                    By.cssSelector(".meal-item")
            );

            if (items.isEmpty()) return false;

            // Ensure Vue has rendered real text (nutrition / content)
            for (WebElement item : items) {
                if (!item.getText().isBlank()) {
                    return true;
                }
            }
            return false;
        });

        // Only now is the DOM stable enough to snapshot
        String pageSource = driver.getPageSource();
        if (pageSource == null) return;

        Document doc = Jsoup.parse(pageSource);

        Element titleElement = doc.selectFirst(
                "h1[data-epi-property-name=\"Title\"]"
        );
        if (titleElement == null) return;

        String name = titleElement.text();

        Elements body = doc.getElementsByClass("lunch-day");

        list.put(name, body);
    }

    private static @NonNull ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        options.addArguments(
                "--headless=new",   // Run Chromium without GUI
                "--no-sandbox",     // Disable Chromium OS sandbox
                "--disable-dev-shm-usage",  // Prevents Chromium from using /dev/shm shared memory
                "--disable-gpu",    // Disable GPU acceleration
                "--window-size=1920,1080",
                "--disable-extensions",
                "--disable-background-networking",
                "--disable-sync",
                "--disable-default-apps",
                "--disable-features=TranslateUI"
        );

        // Remember to change this for deployment
        options.setBinary("/Applications/Chromium.app");
        return options;
    }
}
