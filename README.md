# Kårkaféerna JSON API

## What it is?

* It is a backend application to scrape the data from Kårkaféernas website to a more device friendly from

* All the data is fetched from openly available `https://www.karkafeerna.fi` website

* The data can be queried in 2 kind of ways
  * A normal `REST API` http://localhost:8080/api/v1/meals
    * A single `GET` command returns the menus for the current day in JSON form
  * A `Graphql API` http://localhost:8080/graphiql

### GraphQL queries

The `Graphql API` can be used to query all of the restaurants at the same time, or just a single restaurant. When querying a single restaurant, the restaurants name is used as the parameter, first letter capitalized.

#### All restaurants

```graphql
query all {
  allRestaurants {
    name
    openingHours
    foodItems {
      name
      allergens
      macros {
        energi {
          amount
          quantity
        }
        fett {
          amount
          quantity
        }
        kolhydrater {
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
        studerande
        forskarstuderande
        personal
        andra
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
  restaurantByName(name: "<Restaurant-name-here>"){
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

* In the future the user should be able to query the API to pick the restaurant they want to get the menu for
  * Implemented in the `Graphql API`
  * Not yet in the `REST API`

* Log to get the history of the meals a restaurant has served

* Perhaps also Unica restaurants

* Implement an API key functionality
  * API is free to use but a key has to be made
