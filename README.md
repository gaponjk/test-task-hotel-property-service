# Hotel Property Service

A lightweight RESTful API service built with **Java 21** and **Spring Boot 3.4**.

## Quick Start & API Documentation

1. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```
   The service starts on port **8092**.

2. **Interactive Swagger UI:**  
   Once the application is running, open the interactive API documentation to test all endpoints:  
   **http://localhost:8092/swagger-ui.html**

   *(All REST endpoints are mapped under the `/property-view` prefix).*

---

## Database Migrations (Liquibase)

The database schema and sample data are managed via **Liquibase** changelogs located in `src/main/resources/db.changelog/`:
* `changeset/*` — table structures, primary/foreign keys.
* `data/01-insert-initial-data.yaml` — initial seed data for hotels and amenities.
* `db.changelog-master.yaml` — master changelog file.

### Verifying Migrations via H2 Web Console

To manually inspect migrated tables and data:

1. Open **http://localhost:8092/h2-console** in your browser.
2. Enter the connection settings:
   * **JDBC URL:** `jdbc:h2:mem:hoteldb`
   * **User Name:** `sa`
   * **Password:** *(leave empty)*
3. Click **Connect** and query the tables  directly.
