# Task Management API (Spring Boot)

RESTful API for managing tasks with pagination, filtering, keyword search, and categories.

## Features
- CRUD for Tasks
- Pagination & Sorting for tasks
- Filters: status, priority, categoryId
- Keyword search in title and description
- CRUD for Categories
- Proper exception handling (404, 409)

## Tech Stack
- Java
- Spring Boot
- Spring Data JPA
- Bean Validation (Jakarta Validation)
- (Your DB: H2 / MySQL / PostgreSQL)

## How to Run
### Using Maven Wrapper
```bash
./mvnw spring-boot:run
