package com.eikka.universityMenuScraper.helpers.unica;

import com.eikka.universityMenuScraper.components.Restaurant;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnicaScraper {

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
    
    /**
     * Orchestrates the full scraping and transformation pipeline for Unica restaurants.
     *
     * <p>This method performs the following high-level steps:</p>
     * <ol>
     *   <li>Invokes {@link #scrapeRestaurants()} to fetch and scrape raw HTML content
     *       for each configured restaurant page.</li>
     *   <li>Transforms the scraped HTML fragments into structured
     *       {@link Restaurant} domain objects.</li>
     *   <li>Returns a deterministic, insertion-ordered set of restaurants.</li>
     * </ol>
     *
     * <p>A {@link LinkedHashSet} is used to preserve insertion order while preventing
     * duplicate restaurants in cases where the scraper is re-run or URLs overlap.</p>
     *
     * <p>This method represents the public entry point of the Unica scraping workflow
     * and guarantees that returned {@link Restaurant} objects are fully populated
     * with opening hours and meal data.</p>
     *
     * @return a {@link LinkedHashSet} of fully populated {@link Restaurant} objects,
     *         one per successfully scraped restaurant page
     */
    public LinkedHashSet<Restaurant> getAllRestaurants() {

        LinkedHashSet<Restaurant> restaurants = new LinkedHashSet<>();
        Map<String, Elements> restaurantHTML = this.scrapeRestaurants();
        
        addRestaurantToList(restaurants, restaurantHTML);

        return restaurants;
    }
    
    /**
     * Converts raw scraped HTML fragments into structured {@link Restaurant} objects
     * and appends them to the provided collection.
     *
     * <p>The input map is expected to contain:</p>
     * <ul>
     *   <li><b>Key:</b> Restaurant name, extracted from the page title</li>
     *   <li><b>Value:</b> A collection of {@code lunch-day} elements representing
     *       the full daily menu structure</li>
     * </ul>
     *
     * <p>For each restaurant entry, this method:</p>
     * <ol>
     *   <li>Instantiates a new {@link Restaurant} with the given name.</li>
     *   <li>Extracts and assigns opening hours using {@link UnicaExtractor}.</li>
     *   <li>Iterates over each lunch day and delegates meal extraction
     *       to {@link #addMealToRestaurant(Restaurant, Elements)}.</li>
     *   <li>Adds the fully populated {@link Restaurant} to the result set.</li>
     * </ol>
     *
     * <p>This method performs no scraping itself; it strictly handles transformation
     * from HTML to domain objects.</p>
     *
     * @param restaurants the target collection where parsed {@link Restaurant}
     *                    objects are added
     * @param restaurantHTML a map containing restaurant names and their corresponding
     *                       scraped HTML menu content
     */
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
    
    /**
     * Extracts individual meals from a collection of menu packages and attaches them
     * to the given {@link Restaurant}.
     *
     * <p>Each menu package represents a single serving station (e.g. "STATION 1–2")
     * with its own serving time window and pricing. This method:</p>
     * <ol>
     *   <li>Extracts station metadata (name and serving hours).</li>
     *   <li>Extracts pricing information for the station.</li>
     *   <li>Collects all individual meal items belonging to the station.</li>
     *   <li>Delegates parsing of individual meals to {@link UnicaExtractor}.</li>
     * </ol>
     *
     * <p>The HTML structure is assumed to be stable and to contain:</p>
     * <ul>
     *   <li>An {@code h5} element describing the station and serving hours</li>
     *   <li>A {@code p} element describing meal prices</li>
     *   <li>One or more {@code meal-item} elements representing individual dishes</li>
     * </ul>
     *
     * <p>If any required structural element is missing, a {@link NullPointerException}
     * will be thrown via {@link Objects#requireNonNull(Object)}, signaling a breaking
     * change in the upstream HTML structure.</p>
     *
     * @param restaurant the {@link Restaurant} entity to which extracted meals
     *                   will be attached
     * @param meals a collection of menu package elements, each representing
     *              a serving station and its meals
     */
    private static void addMealToRestaurant(Restaurant restaurant, Elements meals) {
        
        for (Element meal : meals) {
            
            String station = Objects.requireNonNull(meal.selectFirst("h5")).text();
            String mealPrices = Objects.requireNonNull(meal.selectFirst("p")).text();
            
            Elements singleMeals = meal.getElementsByClass("meal-item");
            
            UnicaExtractor.extractSingleMeal(restaurant, singleMeals, station, mealPrices);
            
        }
    }
    
    /**
     * <p>Handle scraping and formatting the restaurants to a list</p>
     * <p>The key is the name of the restaurant</p>
     * <p>Value is the body of the webpage containing all information about meals</p>
     * @return neatly formatted map for restaurants and their meal information
     */
    private Map<String, Elements> scrapeRestaurants() {

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
     *                        <ul>
     *                          <li>key is the restaurants name</li>
     *                          <li>value is the entire menu, containing all info from meals to allergens</li>
     *                        </ul>
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

        // Open each hamburger menu in the webpage
        for (WebElement accordion : accordions) {
            
            //Scroll the accordion into the viewport to trigger IntersectionObserver callbacks and lazy Vue component rendering.
            js.executeScript(
                    "arguments[0].scrollIntoView({block: 'center'});",
                    accordion
            );

            // Ensure the accordion is interactable before attempting a click
            wait.until(ExpectedConditions.elementToBeClickable(accordion));

            // Expand the accordion if it is currently collapsed
            // The expansion state is communicated via the aria-expanded attribute
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

        // Custom wait condition that ensures meal items are not only present in the DOM, but also contain real rendered text.
        // This prevents capturing the page source before Vue has finished injecting meal names and nutritional values.
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

        // Get the restaurants name from the title of the webpage
        Element titleElement = doc.selectFirst(
                "h1[data-epi-property-name=\"Title\"]"
        );
        
        // If the restaurant has no name then return
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
