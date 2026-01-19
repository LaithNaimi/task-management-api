# Task Management API (Spring Boot)

RESTful API for managing tasks with pagination, filtering, keyword search, and categories.

## Features
- CRUD for Tasks (Create, Read, Update, Delete)
- Pagination & Sorting for tasks
- Filters: status, priority, categoryId
- Keyword search in title and description
- CRUD for Categories
- Soft Delete for Tasks (optional)
- Proper exception handling (404, 409, custom ProblemDetails)
- Flyway migrations for DB schema management
- SpringDoc Swagger for API documentation

## Tech Stack
- Java 21
- Spring Boot 4.0.1
- Spring Data JPA
- Spring Security (for upcoming authentication/authorization)
- Bean Validation (Jakarta Validation)
- PostgreSQL (used for development)
- Flyway (for database migrations)
- SpringDoc OpenAPI (for API documentation)

## How to Run
### Using Maven Wrapper
1. Clone the repository.
2. Open a terminal and navigate to the project directory.
3. Run the following command to start the application:

```bash
./mvnw spring-boot:run
