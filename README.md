# URL Shortener

A web application for shortening URLs with support for custom aliases and configurable expiration.

## Tech Stack

- **Backend:** Java 21, Spring Boot 4, Spring Data JPA, Flyway
- **Frontend:** Vue 3, Vite 7
- **Database:** PostgreSQL

## Prerequisites

- Java 21
- PostgreSQL
- Node.js (for frontend development)

## Setup

### Database

Create a PostgreSQL database:

```sql
CREATE DATABASE url_shortener;
```

Flyway will run the schema migration automatically on startup.

### Environment Variables

| Variable       | Required | Default                | Description                 |
|----------------|----------|------------------------|-----------------------------|
| `DB_HOST`      | No       | `localhost`            | PostgreSQL host             |
| `DB_PORT`      | No       | `5432`                 | PostgreSQL port             |
| `DB_NAME`      | No       | `url_shortener`        | Database name               |
| `DB_USERNAME`  | Yes      |                        | Database username           |
| `DB_PASSWORD`  | Yes      |                        | Database password           |
| `APP_BASE_URL` | No       | `http://localhost:8080`| Base URL for shortened links|

### Running the Application

```bash
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
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

The dev server proxies API requests to `http://localhost:8080`.

To build for production:

```bash
npm run build
```

## API

### Create Short URL

`POST /api/short-url`

**Request body:**

| Field         | Type    | Required | Description                              |
|---------------|---------|----------|------------------------------------------|
| `originalUrl` | String  | Yes      | The URL to shorten                       |
| `customAlias` | String  | No       | Custom short code (min 1 character)      |
| `expireInDays`| Integer | No       | Expiration in days (1-365)               |

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

Redirects (302) to the original URL. Returns 404 if the short code does not exist or has expired.

## Running Tests

```bash
./mvnw test
```
