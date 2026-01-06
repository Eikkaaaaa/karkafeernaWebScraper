# Kårkaféerna JSON API

## What it is?

* It is a backend application to scrape the data from Kårkaféernas website to a more device friendly from

* All the data is fetched from openly available `https://www.karkafeerna.fi` website

* The API endpoint is hosted at `http://localhost:8080/api/v1/meals`

* A single `GET` command returns the menu for the current day in JSON form

## Future development

* In the future the user should be able to query the API to pick the restaurant they want to get the menu for

* Log to get the history of the meals a restaurant has served

* Perhaps also Unica restaurants

* Implement an API key functionality
  * API is free to use but a key has to be made
