# Kårkaféerna and Unica JSON API

## What it is?

* It is a backend application to scrape the data from Kårkaféernas and Unicas websites to a more device friendly from

* All the data is fetched from following websites
  * https://www.karkafeerna.fi/en/lunch
  * https://www.unica.fi/en/restaurants/university-campus/assarin-ullakko/
  * https://www.unica.fi/en/restaurants/university-campus/galilei/
  * https://www.unica.fi/en/restaurants/university-campus/macciavelli/
  * https://www.unica.fi/en/restaurants/university-campus/monttu-ja-mercatori/
  * https://www.unica.fi/en/restaurants/kupittaa-campus/deli-pharma/
  * https://www.unica.fi/en/restaurants/kupittaa-campus/delica/
  * https://www.unica.fi/en/restaurants/kupittaa-campus/dental/
  * https://www.unica.fi/en/restaurants/kupittaa-campus/kisalli/
  * https://www.unica.fi/en/restaurants/kupittaa-campus/linus/
  * https://www.unica.fi/en/restaurants/art-campus/sigyn/
  * https://www.unica.fi/en/restaurants/others/unican-kulma/
  * https://www.unica.fi/en/restaurants/others/fabrik-cafe/
  * https://www.unica.fi/en/restaurants/others/piccu-maccia/
  * https://www.unica.fi/en/restaurants/others/puutorin-nurkka/
  * https://www.unica.fi/en/restaurants/other-restaurants/henkilostoravintola-waino/
  * https://www.unica.fi/en/restaurants/other-restaurants/kaffeli/
  * https://www.unica.fi/en/restaurants/other-restaurants/kaivomestari/
  * https://www.unica.fi/en/restaurants/other-restaurants/lemminkainen/
  * https://www.unica.fi/en/restaurants/other-restaurants/mairela/
  * https://www.unica.fi/en/restaurants/other-restaurants/rammeri/
  * https://www.unica.fi/en/restaurants/other-restaurants/ruokakello

* The data can be queried in 2 kind of ways
  * A normal `REST API` http://localhost:8080/api/v1/restaurants
    * A single `GET` command returns the menus for the current day in JSON form
  * A `Graphql API` http://localhost:8080/graphiql

### REST API info

The `REST API` for the HTTP requests can be done:
#### `http://localhost:8080/api/v1/restaurants`
  * Returns all of the restaurants and their respective info
  * Contains also:
    * `updatedAt` timestamp when the data was updated
    * `source` source of the data

#### `http://localhost:8080/api/v1/formatted` 
* Get the data only for the restaurants. 
* This omits timestamp and source

#### `http://localhost:8080/api/v1/restaurants/{restaurant_name}`
* Returns info for a single restaurant
* Does not contain timestamps or datasource
* Capitalization-agnostic, all letters can be upper- or lowercase
* Restaurants that are available:
  * `Arken`
  * `Astra`
  * `Aurum`
  * `Aurum Bistro`
    * Becomes `Aurum-Bistro`
    * `/restaurants/Aurum-Bistro`
  * `Kåren`
    * Switch `å -> a` in the URL
    * `/restaurants/karen`
  * `Assarin Ullakko`
  * `Galilei`
  * `Macciavelli`
  * `Monttu ja Mercatori`
  * `Deli Pharma`
  * `Delica`
  * `Dental`
  * `Kisälli`
  * `Linus`
  * `Sigyn`
  * `Unican Kulma`
  * `Fabrik Cafe`
  * `Piccu Maccia`
  * `Puutorin Nurkka`
  * `Henkilöstöravintola Wäinö`
  * `Kaffeli`
  * `Kaivomestari`
  * `Lemminkäinen`
  * `Mairela`
  * `Rammeri`
  * `Ruokakello`

### GraphQL queries

The `Graphql API` can be used to query all of the restaurants at the same time, or just a single restaurant. When querying a single restaurant, the restaurants name is used as the parameter.

#### All restaurants with all possible parameters

```graphql
query all {
  allRestaurants {
    name
    openingHours
    foodItems {
      name
      allergens
      macros {
        calories {
          amount
          quantity
        }
        fat {
          amount
          quantity
        }
        carbs {
          amount
          quantity
        }
        protein {
          amount
          quantity
        }
        salt {
          amount
          quantity
        }
      }
      priceGroup
      prices {
        students
        researcher_students
        staff
        others
      }
    }
  }
}
```

#### Single restaurant

Single restaurants can be searched with the exact same parameters just by changing the query

##### From this
```graphql
query all {
  allRestaurants{
    name
  }
}
```
```json
{
  "data": {
    "allRestaurants": [
      {
        "name": "Arken"
      },
      {
        "name": "Astra"
      },
      {
        "name": "Aurum"
      },
      {
        "name": "Aurum Bistro"
      },
      {
        "name": "Kåren"
      }
    ]
  }
}
```

##### To this
```graphql
query all {
  restaurantByName(name: "{Restaurant-name-here}"){
    name
  }
}
```
```json
{
  "data": {
    "restaurantByName": {
      "name": "Aurum"
    }
  }
}
```

### Future development

* Log to get the history of the meals a restaurant has served
* Perhaps also Unica restaurants [DONE]
* Implement an API key functionality
  * API is free to use but a key has to be made
