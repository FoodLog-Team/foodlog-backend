# FoodLog Backend

Backend application for FoodLog — a food tracking and nutrition logging service built with Spring Boot 4, PostgreSQL, and Google GenAI.

## Tech Stack

- **Java 21** with **Spring Boot 4.1.2**
- **PostgreSQL 17** as the primary database
- **Flyway** for database migrations
- **Spring Security** with JWT authentication
- **Spring AI** with Google GenAI for AI-powered features
- **MapStruct** for object mapping
- **Lombok** for boilerplate reduction
- **Docker & Docker Compose** for containerized deployment

## Prerequisites

Before cloning and running the project, ensure you have the following installed:

| Tool          | Minimum Version | Purpose                              |
|---------------|-----------------|--------------------------------------|
| Java (JDK)    | 21              | Application runtime and compilation  |
| Maven         | 3.9+            | Build tool and dependency management |
| Docker        | Latest stable   | Containerization                     |
| Docker Compose| 2.x             | Multi-container orchestration        |
| Git           | Latest stable   | Version control                      |

You will also need:

- A **Google AI Studio** account to obtain a Gemini API key ([get one here](https://aistudio.google.com/apikey))

## Cloning the Repository

```bash
git clone https://github.com/FoodLog-Team/foodlog-backend.git
cd FoodLog-backend
```

## Setting Up Environment Variables

The application requires several environment variables. Create a `.env` file in the project root directory with the following content:

```bash
# --- Database Configuration ---
POSTGRES_DB=foodlog
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_PORT=5432
POSTGRES_DOCKER_PORT=5432

# --- Application Server ---
SPRING_LOCAL_PORT=8080
SPRING_DOCKER_PORT=8080

# --- JWT Authentication ---
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production

# --- Google Gemini AI ---
GEMINI_API_KEY=your-gemini-api-key-here

# --- Refresh Token Configuration ---
REFRESH_TOKEN_EXPIRATION=7
REFRESH_TOKEN_MAX_AMOUNT=5
REFRESH_TOKEN_MAX_AGE=30
REFRESH_TOKEN_COOKIE_PATH=/api/auth/refresh
REFRESH_TOKEN_COOKIE_SECURE=true
```

### Important Notes on Environment Variables

- **`JWT_SECRET`**: Must be a strong, randomly generated string. Never commit this to version control.
- **`GEMINI_API_KEY`**: Your Google AI Studio API key. The application will not start without it.
- **`REFRESH_TOKEN_COOKIE_SECURE`**: Set to `true` in production (HTTPS). You may set it to `false` for local development over HTTP.
- **`POSTGRES_PASSWORD`**: Change the default password for production use.

## Running the Application

### Option 1: Docker Compose (Recommended)

The easiest way to run the full stack (application + database) is via Docker Compose:

```bash
docker compose up --build
```

This command will:

1. Build the Java application image using the multi-stage `Dockerfile`
2. Start a PostgreSQL 17 container with the configured database
3. Start the application container, connecting it to the database
4. Wait for the database to be healthy before launching the app

The API will be available at `http://localhost:8080/api`.

To run in detached mode (background):

```bash
docker compose up --build -d
```

To stop the services:

```bash
docker compose down
```

To stop services and remove persistent database volumes:

```bash
docker compose down -v
```

### Option 2: Local Development (Without Docker)

If you prefer to run the application locally with your own IDE:

**1. Start PostgreSQL locally**

Ensure you have a PostgreSQL instance running on `localhost:5432` with the database and user matching your `.env` configuration:

```bash
createdb foodlog
# Or via psql:
# psql -U postgres -c "CREATE DATABASE foodlog;"
```

**2. Export environment variables**

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/foodlog
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
export JWT_SECRET=your-super-secret-jwt-key-change-this-in-production
export GEMINI_API_KEY=your-gemini-api-key-here
export REFRESH_TOKEN_EXPIRATION=7
export REFRESH_TOKEN_MAX_AMOUNT=5
export REFRESH_TOKEN_MAX_AGE=30
export REFRESH_TOKEN_COOKIE_PATH=/api/auth/refresh
export REFRESH_TOKEN_COOKIE_SECURE=false
```

**3. Build and run the application**

```bash
./mvnw spring-boot:run
```

Or build a JAR and run it directly:

```bash
./mvnw clean package -DskipTests
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

The API will be available at `http://localhost:8080/api`.

## Project Structure

```
├── Dockerfile              # Multi-stage Docker build (Maven + JRE)
├── docker-compose.yaml     # Service orchestration (app + PostgreSQL)
├── pom.xml                 # Maven dependencies and build config
├── checkstyle.xml          # Code style rules
└── src/
    ├── main/
    │   ├── java/           # Application source code
    │   └── resources/
    │       ├── application.yaml       # Spring Boot configuration
    │       └── db/migration/          # Flyway SQL migrations
    └── test/               # Test sources
```

## API Documentation

Once the application is running, access the Swagger UI at:

```
http://localhost:8080/api/swagger-ui.html
```

## Database Migrations

The project uses **Flyway** for database schema management. Migrations are located in `src/main/resources/db/migration/` and are applied automatically on startup. The current migrations:

| File | Description |
|------|-------------|
| `V1__create_users_table.sql` | Creates the users table |
| `V2__create_refresh_tokens_table.sql` | Creates the refresh tokens table |
| `V3__create_user_nutrition_profiles_table.sql` | Creates user nutrition profiles |
| `V4__create_nutrition_targets_table.sql` | Creates nutrition targets |

## Running Tests

```bash
./mvnw test
```

Tests use **Testcontainers** with PostgreSQL for integration testing.
