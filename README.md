# FoodLog Backend

Backend application for **FoodLog**, a food diary that helps users track
meals, calories, and macronutrients.

The application can recognize food from meal photos, retrieve
nutritional information, and calculate daily nutrition targets based on
a user's profile.

## Features

-   User registration and authentication with JWT access tokens and
    refresh tokens.
-   Meal creation with one or two photos.
-   AI-powered food recognition through Groq.
-   Food nutrition lookup through USDA FoodData Central.
-   Manual food item addition by name and weight.
-   Asynchronous meal processing with individual item statuses.
-   Meal calorie and macronutrient totals.
-   Nutrition profiles with physical characteristics, activity level,
    and weight goal.
-   Recommended daily nutrition targets calculated using the Mifflin-St
    Jeor formula.
-   Custom daily nutrition targets.
-   Persistent photo storage.

## Tech Stack

-   **Java 21**
-   **Spring Boot 4.1.2**
-   **Spring Security** and OAuth2 Resource Server
-   **JWT** access tokens and refresh token rotation
-   **PostgreSQL 17**
-   **Flyway** database migrations
-   **Groq API** for food recognition
-   **USDA FoodData Central API** for nutritional information
-   **Apache Tika** for photo format detection
-   **MapStruct** for object mapping
-   **Lombok**
-   **Maven**
-   **Docker** and **Docker Compose**
-   **Testcontainers** for PostgreSQL-based tests
-   **Springdoc OpenAPI** for API documentation

## Prerequisites

-   Java 21
-   Docker Engine
-   Docker Compose v2
-   Groq API key
-   USDA FoodData Central API key

For local Maven development, the project includes the Maven Wrapper, so
a separate Maven installation is not required.

## Getting Started

### 1. Clone the repository

``` bash
git clone https://github.com/FoodLog-Team/foodlog-backend.git
cd foodlog-backend
```

### 2. Configure environment variables

Create a `.env` file in the project root:

``` bash
cp .env.example .env
```

Fill in the required values:

``` dotenv
# PostgreSQL
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=foodlog
POSTGRES_DOCKER_PORT=5432
POSTGRES_PORT=5432

# Application
SPRING_LOCAL_PORT=8080
SPRING_DOCKER_PORT=8080

# Groq
GROQ_API_KEY=your-groq-api-key
GROQ_MODEL=qwen/qwen3.8-27b

# USDA FoodData Central
USDA_API_KEY=your-usda-api-key

# JWT
JWT_SECRET=your-base64-encoded-secret
JWT_EXPIRATION=900000

# Refresh tokens
REFRESH_TOKEN_COOKIE_PATH=/api/auth
REFRESH_TOKEN_EXPIRATION=30d
REFRESH_TOKEN_COOKIE_SECURE=false
REFRESH_TOKEN_MAX_AMOUNT=5
REFRESH_TOKEN_MAX_AGE=30d

# Meal photo storage
MEAL_PHOTOS_DIRECTORY=./uploads
```

`JWT_SECRET` must contain a sufficiently long, randomly generated
Base64-encoded secret.

Set `REFRESH_TOKEN_COOKIE_SECURE=true` when running behind HTTPS. For
local development over HTTP, use `false`.

### 3. Start the application

Build and start the application with Docker Compose:

``` bash
docker compose up --build
```

The command starts PostgreSQL and the application. Flyway applies
database migrations automatically during application startup.

The API is available at:

``` text
http://localhost:8080/api
```

To run the services in the background:

``` bash
docker compose up --build -d
```

To stop the services:

``` bash
docker compose down
```

To stop the services and remove the database and photo-storage volumes:

``` bash
docker compose down -v
```

## API Documentation

Interactive API documentation is available through Swagger UI:

``` text
http://localhost:8080/api/swagger-ui/index.html
```

The OpenAPI specification is available at:

``` text
http://localhost:8080/api/v3/api-docs
```

All endpoints use the `/api` context path.

## API Overview

### Authentication

  -----------------------------------------------------------------------
  Method             Endpoint               Description
  ------------------ ---------------------- -----------------------------
  `POST`             `/auth/register`       Register a user

  `POST`             `/auth/login`          Authenticate a user

  `POST`             `/auth/refresh`        Rotate the refresh token and
                                            issue a new access token

  `POST`             `/auth/logout`         Revoke the current refresh
                                            token

  `POST`             `/auth/logout-all`     Revoke all refresh tokens for
                                            the authenticated user
  -----------------------------------------------------------------------

Registration and login return a JWT access token in the response body
and set the refresh token in an HttpOnly cookie.

Use the access token in the `Authorization` header for protected
endpoints:

``` http
Authorization: Bearer <access-token>
```

The refresh endpoint reads the refresh token from the cookie and sets a
rotated token in the response cookie.

### Meals

  -------------------------------------------------------------------------
  Method             Endpoint                 Description
  ------------------ ------------------------ -----------------------------
  `POST`             `/meals`                 Create a meal entry and start
                                              photo recognition

  `GET`              `/meals/{mealEntryId}`   Get a meal entry with its
                                              items and nutrition totals

  `POST`             `/meals/products`        Add a food item to an
                                              existing meal entry
  -------------------------------------------------------------------------

`POST /meals` uses `multipart/form-data`:

  -----------------------------------------------------------------------
  Field         Type        Required            Description
  ------------- ----------- ------------------- -------------------------
  `photo1`      File        Yes                 Primary meal photo

  `photo2`      File        No                  Additional meal photo

  `plateSize`   String      No                  Plate size: `SMALL`,
                                                `MEDIUM`, or `LARGE`
  -----------------------------------------------------------------------

The endpoint returns `202 Accepted` with the newly created meal entry ID
and its initial processing status.

`POST /meals/products` accepts a JSON body:

``` json
{
  "mealEntryId": 1,
  "name": "Whole wheat bread",
  "weight": 100
}
```

The item is processed asynchronously. Use `GET /meals/{mealEntryId}` to
retrieve the current processing status and results.

### Nutrition Profile

  -----------------------------------------------------------------------
  Method             Endpoint               Description
  ------------------ ---------------------- -----------------------------
  `POST`             `/nutrition-profile`   Create the authenticated
                                            user's nutrition profile

  `PATCH`            `/nutrition-profile`   Partially update the
                                            nutrition profile
  -----------------------------------------------------------------------

The profile contains:

-   Date of birth
-   Height in centimeters
-   Weight in kilograms
-   Biological sex
-   Activity level
-   Weight goal

A nutrition profile is required for recommended nutrition target
calculations. Creating a profile also initializes recommended nutrition
targets.

### Nutrition Target

  --------------------------------------------------------------------------------------
  Method             Endpoint                              Description
  ------------------ ------------------------------------- -----------------------------
  `POST`             `/nutrition-target/recommendations`   Calculate and save
                                                           recommended daily targets

  `GET`              `/nutrition-target`                   Get the currently saved
                                                           nutrition targets

  `PUT`              `/nutrition-target/custom`            Save custom daily nutrition
                                                           targets
  --------------------------------------------------------------------------------------

Recommended targets use the selected BMR formula and calculation
strategy. The currently supported options are:

-   Formula: `MIFFLIN_ST_JEOR`
-   Calculation strategy: `BASIC`

When the active mode is `RECOMMENDED`, changes to the nutrition profile
synchronize the saved targets. In `CUSTOM` mode, profile changes do not
overwrite custom targets.

`GET /nutrition-target` returns the currently saved target
configuration.

Custom targets are provided in grams:

``` json
{
  "carbs": 250,
  "protein": 150,
  "fats": 80
}
```

The calorie target is calculated from the macronutrient values using 4
kcal per gram of carbohydrates, 4 kcal per gram of protein, and 9 kcal
per gram of fat.

## Meal Processing

Meal processing is asynchronous:

1.  The application stores the uploaded photos.
2.  Groq analyzes the photos and returns recognized food items with
    estimated weights.
3.  The application searches USDA FoodData Central for nutritional
    information.
4.  Nutrition values are calculated for each item based on its weight.
5.  The meal entry is updated with item results and total nutrition
    values.

Meal entries expose the following analysis statuses:

-   `PENDING`
-   `PROCESSING`
-   `COMPLETED`
-   `FAILED`

Individual meal items expose their own fetching statuses:

-   `PENDING`
-   `COMPLETED`
-   `FAILED`

Users can also add food items manually by providing a name and weight.
Manual items use the same nutrition lookup flow.

## Nutrition Calculations

The recommended daily calorie target is based on the Mifflin-St Jeor
formula, which estimates Basal Metabolic Rate (BMR) using the user's
age, height, weight, and biological sex.

The calculation strategy also considers:

-   Physical activity level
-   Weight goal
-   Macronutrient distribution

Nutrition values for meal items are retrieved from USDA FoodData Central
and scaled according to the item's weight.

## Database Migrations

Flyway manages the database schema. Migrations are located in:

``` text
src/main/resources/db/migration/
```

  ---------------------------------------------------------------------------------------
  Migration                                        Description
  ------------------------------------------------ --------------------------------------
  `V1__create_users_table.sql`                     Creates the users table

  `V2__create_refresh_tokens_table.sql`            Creates the refresh tokens table

  `V3__create_user_nutrition_profiles_table.sql`   Creates user nutrition profiles

  `V4__create_nutrition_targets_table.sql`         Creates nutrition targets

  `V5__create_meal_entries_table.sql`              Creates meal entries

  `V6__create_meal_entry_photos_table.sql`         Creates meal entry photos

  `V7__create_meal_entry_items_table.sql`          Creates meal entry items
  ---------------------------------------------------------------------------------------

Migrations are applied automatically when the application starts.

## Running Tests

Run the test suite with:

``` bash
./mvnw test
```

The project uses Spring Boot Test, JUnit, and Testcontainers with
PostgreSQL.

## Project Structure

``` text
├── Dockerfile
├── docker-compose.yaml
├── pom.xml
├── checkstyle.xml
├── lombok.config
└── src
    ├── main
    │   ├── java
    │   │   └── team/foodlog/backend
    │   │       ├── config
    │   │       ├── controller
    │   │       ├── dto
    │   │       ├── exception
    │   │       ├── mapper
    │   │       ├── model
    │   │       ├── properties
    │   │       ├── repository
    │   │       ├── security
    │   │       └── service
    │   └── resources
    │       ├── application.yaml
    │       └── db/migration
    └── test
```
