# URL Shortener

A web application for shortening URLs with support for custom aliases, configurable expiration, and JWT-based authentication.

## Tech Stack

- **Backend:** Java 21, Spring Boot 4, Spring Security, Spring Data JPA, Flyway
- **Frontend:** Vue 3, Vite 7
- **Database:** PostgreSQL
- **Cache:** Redis

## Prerequisites

- Java 21
- PostgreSQL
- Redis
- Node.js (for frontend development)

## Setup

### Database

Create a PostgreSQL database:

```sql
CREATE DATABASE url_shortener;
```

Flyway will run the schema migrations automatically on startup.

### Environment Variables

| Variable         | Required | Default                 | Description                  |
|------------------|----------|-------------------------|------------------------------|
| `DB_HOST`        | No       | `localhost`             | PostgreSQL host              |
| `DB_PORT`        | No       | `5432`                  | PostgreSQL port              |
| `DB_NAME`        | No       | `url_shortener`         | Database name                |
| `DB_USERNAME`    | Yes      |                         | Database username            |
| `DB_PASSWORD`    | Yes      |                         | Database password            |
| `REDIS_HOST`     | No       | `localhost`             | Redis host                   |
| `REDIS_PORT`     | No       | `6379`                  | Redis port                   |
| `APP_BASE_URL`   | No       | `http://localhost:8080` | Base URL for shortened links |
| `JWT_SECRET_KEY` | Yes      |                         | Secret key for signing JWTs  |

### Running the Application

```bash
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET_KEY=your_secret_key
./mvnw spring-boot:run
```

The backend starts on port 8080 by default.

## Frontend

The Vue frontend is in the `frontend/` directory.

```bash
cd frontend
npm install
npm run dev
```

The dev server runs on port 3000 and proxies API requests to `http://localhost:8080`.

To build for production:

```bash
npm run build
```

## API

### Authentication

All endpoints except registration, login, token refresh, and short URL redirect require a valid JWT access token in the `Authorization` header:

```
Authorization: Bearer <access_token>
```

#### Register

`POST /api/users`

| Field      | Type   | Required | Description                           |
|------------|--------|----------|---------------------------------------|
| `username` | String | Yes      | Username                              |
| `password` | String | Yes      | Password (8-64 characters)            |

**Response (201 Created):**

```json
{
  "id": 1,
  "username": "johndoe"
}
```

#### Login

`POST /api/tokens`

| Field      | Type   | Required | Description |
|------------|--------|----------|-------------|
| `username` | String | Yes      | Username    |
| `password` | String | Yes      | Password    |

**Response (200 OK):**

```json
{
  "accessToken": "eyJhbG...",
  "refreshToken": "eyJhbG..."
}
```

#### Refresh Token

`POST /api/tokens/refresh`

| Field          | Type   | Required | Description        |
|----------------|--------|----------|--------------------|
| `refreshToken` | String | Yes      | Valid refresh token |

**Response (200 OK):**

```json
{
  "accessToken": "eyJhbG...",
  "refreshToken": null
}
```

### Create Short URL

`POST /api/short-url` (requires authentication)

| Field         | Type    | Required | Description                         |
|---------------|---------|----------|-------------------------------------|
| `originalUrl` | String  | Yes      | The URL to shorten                  |
| `customAlias` | String  | No       | Custom short code (min 1 character) |
| `expireInDays`| Integer | No       | Expiration in days (1-365)          |

**Example request:**

```json
{
  "originalUrl": "https://example.com/very/long/url",
  "customAlias": "my-link",
  "expireInDays": 30
}
```

**Example response (201 Created):**

```json
{
  "originalUrl": "https://example.com/very/long/url",
  "shortUrl": "http://localhost:8080/my-link",
  "expirationDate": "2026-04-11T12:00:00"
}
```

If no `customAlias` is provided, a short code is generated automatically using Base52 encoding.

### Redirect

`GET /{shortCode}`

Redirects (302) to the original URL. Returns 404 if the short code does not exist or has expired. No authentication required.

## Running Tests

```bash
./mvnw test
```
