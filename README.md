# Task Management API

A secure RESTful API for managing personal tasks and categories, built with Spring Boot, Spring Data JPA, PostgreSQL, Flyway, and JWT-based authentication.

The API supports user registration and login, task and category CRUD operations, pagination, sorting, filtering, keyword search, and OpenAPI documentation via Swagger UI.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Installation Instructions](#installation-instructions)
- [Configuration](#configuration)
- [Usage](#usage)
- [Authentication](#authentication)
- [API Endpoints](#api-endpoints)
- [Error Handling](#error-handling)
- [Testing](#testing)
- [Contribution Guidelines](#contribution-guidelines)
- [License](#license)

---

## Project Overview

Task Management API is a backend service for organizing tasks by category and tracking their status, priority, and due dates.

It is designed around a per-user data model:

- Users can register and log in to obtain JWT access tokens.
- Authenticated users can manage their own tasks and categories.
- Task listing supports pagination, sorting, filtering, and keyword search.
- Database schema changes are managed through Flyway migrations.
- API documentation is exposed through OpenAPI/Swagger.

This project is a solid foundation for a task management web or mobile application.

---

## Features

### Authentication and User Access

- User registration with email, username, and password
- User login with JWT token issuance
- Stateless authentication using Bearer tokens
- Role claim included in the JWT payload
- Endpoint protection for tasks and categories

### Task Management

- Create, read, update, and delete tasks
- Associate tasks with categories
- Track task status (`TODO`, `IN_PROGRESS`, `DONE`)
- Track task priority (`LOW`, `MEDIUM`, `HIGH`, `DEFAULT`)
- Optional due date support
- Automatic timestamps for creation and update
- Per-user task ownership

### Task Querying

- Pagination for task listing
- Sorting through Spring Data `Pageable`
- Filter by:
  - status
  - priority
  - category ID
- Keyword search in title and description

### Category Management

- Create, list, update, and delete categories
- Unique category names per user
- Prevent deletion of categories that are still referenced by tasks
- Per-user category ownership

### Developer Experience

- Flyway-based schema migration
- Bean Validation for request validation
- RFC 7807-style `ProblemDetail` error responses
- Swagger UI / OpenAPI documentation
- Maven Wrapper included for easier local setup

---

## Technologies Used

- **Java 21**
- **Spring Boot 4.0.1**
- **Spring Web**
- **Spring Data JPA**
- **Spring Security**
- **Spring OAuth2 Resource Server**
- **Spring Validation (Jakarta Validation)**
- **PostgreSQL**
- **Flyway**
- **SpringDoc OpenAPI**
- **Lombok**
- **Maven / Maven Wrapper**

---

## Project Structure

```text
src/
├─ main/
│  ├─ java/com/laith/taskmanagement/
│  │  ├─ config/
│  │  ├─ controller/
│  │  ├─ dto/
│  │  ├─ exception/
│  │  ├─ mapper/
│  │  ├─ model/
│  │  ├─ repository/
│  │  ├─ security/
│  │  └─ service/
│  └─ resources/
│     ├─ application.properties
│     └─ db/migration/
├─ test/
└─ pom.xml
```

---

## Installation Instructions

### Prerequisites

Before running the project locally, make sure you have:

- **Java 21** installed
- **PostgreSQL** installed and running
- **Git** installed
- Optional: **Maven** installed globally
  - Not required if you use the included Maven Wrapper

### 1. Clone the Repository

```bash
git clone https://github.com/LaithNaimi/task-management-api.git
cd task-management-api
```

### 2. Create the PostgreSQL Database

Create a PostgreSQL database for the application:

```sql
CREATE DATABASE taskmanagement;
```

### 3. Configure the Application

The project uses `src/main/resources/application.properties` for datasource and JWT settings.

Review and update the values to match your local environment before starting the application.

At minimum, verify:

- database URL
- database username
- database password
- JWT secret
- JWT issuer
- token expiration

### 4. Install Dependencies and Run the Application

Using Maven Wrapper:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

### 5. Flyway Migration

Flyway runs automatically on startup and applies the database schema migrations.

---

## Configuration

The application currently relies on properties defined in `application.properties`.

Typical configuration keys include:

| Property | Description |
|---|---|
| `spring.datasource.url` | PostgreSQL JDBC connection string |
| `spring.datasource.username` | Database username |
| `spring.datasource.password` | Database password |
| `spring.jpa.hibernate.ddl-auto` | Hibernate schema behavior |
| `spring.flyway.enabled` | Enables Flyway migrations |
| `spring.flyway.locations` | Migration file location |
| `jwt.secret` | Secret key used to sign JWT tokens |
| `jwt.expiration-seconds` | JWT token lifetime |
| `jwt.issuer` | JWT issuer claim |

### Recommended Improvement

For production or team usage, consider moving secrets and environment-specific values into environment variables or external configuration rather than committing them directly in `application.properties`.

---

## Usage

After the application starts:

- Base URL: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI spec: `http://localhost:8080/v3/api-docs`

The security configuration currently allows CORS from:

- `http://localhost:4200`

This is useful if you plan to connect an Angular frontend during local development.

---

## Authentication

### Public Endpoints

The following endpoints are publicly accessible:

- `POST /api/auth/register`
- `POST /api/auth/login`
- Swagger/OpenAPI endpoints

### Protected Endpoints

The following endpoint groups require a valid Bearer token:

- `/api/tasks/**`
- `/api/categories/**`

### Authentication Flow

1. Register a new user or log in with an existing account.
2. Copy the JWT token from the response.
3. Send it in the `Authorization` header:

```http
Authorization: Bearer <your_token>
```

### Example Register Request

```http
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "username": "laith",
  "email": "laith@example.com",
  "password": "strongPassword123"
}
```

### Example Login Request

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "email": "laith@example.com",
  "password": "strongPassword123"
}
```

### Example Auth Response

```json
{
  "token": "<jwt_token>",
  "tokenType": "Bearer",
  "expiresAt": "2026-03-18T12:00:00Z",
  "userId": 1,
  "role": "USER"
}
```

---

## API Endpoints

## Auth

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user and receive a JWT | No |
| POST | `/api/auth/login` | Authenticate a user and receive a JWT | No |

### Register Request Body

```json
{
  "username": "john",
  "email": "john@example.com",
  "password": "securePassword"
}
```

### Login Request Body

```json
{
  "email": "john@example.com",
  "password": "securePassword"
}
```

---

## Categories

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| GET | `/api/categories` | List all categories for the current user | Yes |
| GET | `/api/categories/{id}` | Get a category by ID | Yes |
| POST | `/api/categories` | Create a category | Yes |
| PUT | `/api/categories/{id}` | Update a category | Yes |
| DELETE | `/api/categories/{id}` | Delete a category | Yes |

### Create / Update Category Request Body

```json
{
  "name": "Work"
}
```

### Example Category Response

```json
{
  "id": 1,
  "name": "Work",
  "createdAt": "2026-03-18T09:30:00",
  "updatedAt": "2026-03-18T09:30:00"
}
```

---

## Tasks

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| GET | `/api/tasks` | List tasks with pagination, filtering, and search | Yes |
| GET | `/api/tasks/{id}` | Get a task by ID | Yes |
| POST | `/api/tasks` | Create a task | Yes |
| PATCH | `/api/tasks/{id}` | Partially update a task | Yes |
| DELETE | `/api/tasks/{id}` | Delete a task | Yes |

### Query Parameters for `GET /api/tasks`

| Parameter | Type | Required | Description |
|---|---|---|---|
| `status` | enum | No | `TODO`, `IN_PROGRESS`, `DONE` |
| `priority` | enum | No | `LOW`, `MEDIUM`, `HIGH`, `DEFAULT` |
| `categoryId` | long | No | Filter by category |
| `q` | string | No | Search keyword in title or description |
| `page` | integer | No | Page index |
| `size` | integer | No | Page size |
| `sort` | string | No | Sorting, e.g. `dueDate,asc` |

### Example Task Creation Request

```json
{
  "title": "Finish README",
  "description": "Write project documentation",
  "status": "TODO",
  "priority": "HIGH",
  "categoryId": 1,
  "dueDate": "2026-03-20"
}
```

### Example Task Creation Request Without Optional Defaults

If `status` and `priority` are omitted, the entity defaults to:

- `status = TODO`
- `priority = DEFAULT`

```json
{
  "title": "Buy groceries",
  "description": "Milk, bread, eggs",
  "categoryId": 2,
  "dueDate": "2026-03-21"
}
```

### Example Task Response

```json
{
  "id": 12,
  "title": "Finish README",
  "description": "Write project documentation",
  "status": "TODO",
  "priority": "HIGH",
  "categoryId": 1,
  "categoryName": "Work",
  "dueDate": "2026-03-20",
  "createdAt": "2026-03-18T10:00:00",
  "updatedAt": "2026-03-18T10:00:00"
}
```

### Example Task List Request

```http
GET /api/tasks?page=0&size=10&sort=dueDate,asc&status=TODO&q=readme
Authorization: Bearer <your_token>
```

### Example Paginated Response Shape

Because the endpoint returns a Spring Data `Page<TaskResponseDTO>`, the response typically includes metadata such as:

```json
{
  "content": [
    {
      "id": 12,
      "title": "Finish README",
      "description": "Write project documentation",
      "status": "TODO",
      "priority": "HIGH",
      "categoryId": 1,
      "categoryName": "Work",
      "dueDate": "2026-03-20",
      "createdAt": "2026-03-18T10:00:00",
      "updatedAt": "2026-03-18T10:00:00"
    }
  ],
  "page": {
    "size": 10,
    "number": 0,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

> Note: The exact page wrapper shape depends on the Spring Boot / Spring Data JSON serialization configuration in use.

---

## Error Handling

The API uses structured `ProblemDetail` responses for many error scenarios, including:

- `400 Bad Request`
- `401 Unauthorized`
- `403 Forbidden`
- `404 Not Found`
- `405 Method Not Allowed`
- `409 Conflict`
- `500 Internal Server Error`

Validation errors may also include a `fieldErrors` object for request-body validation failures.

### Example Error Response

```json
{
  "type": "about:blank",
  "title": "Validation Failed",
  "status": 400,
  "detail": "Request validation failed",
  "timestamp": "2026-03-18T10:15:00Z",
  "fieldErrors": {
    "title": "size must be between 3 and 100"
  }
}
```

---

## Testing

At the moment, the repository does not appear to include a dedicated automated test suite or test dependencies.

A future enhancement would be to add:

- unit tests for services and mappers
- controller tests using MockMvc
- repository integration tests
- authentication and authorization tests

Once tests are added, a conventional Maven command would be:

```bash
./mvnw test
```

---

## Contribution Guidelines

Contributions are welcome. To keep the project maintainable, consider the following workflow:

1. Fork the repository.
2. Create a feature branch:

```bash
git checkout -b feature/improve-task-search
```

3. Make focused changes with clear commit messages.
4. Follow existing coding conventions and package structure.
5. Add or update documentation for any API behavior changes.
6. Add tests for new functionality whenever a test suite is introduced.
7. Open a pull request describing:
   - what changed
   - why it changed
   - how it was tested

### Recommended Contribution Practices

- Keep endpoints RESTful and consistent
- Preserve validation and error-response quality
- Avoid breaking existing request/response contracts unnecessarily
- Prefer small, reviewable pull requests
- Update Flyway migrations carefully for schema changes

---

## License

This repository does **not currently include a license file**.

Until a license is added by the repository owner, the project should be treated as **all rights reserved by default**.

If you plan to open-source the project formally, consider adding a standard license such as:

- MIT License
- Apache License 2.0
- GPLv3

---

## Suggested Next Improvements

If you continue evolving this API, good next steps would be:

- add automated tests
- externalize configuration with environment variables
- add refresh tokens or token revocation strategy
- add Docker / Docker Compose setup
- add API rate limiting and monitoring
- add CI workflows for build and test automation

