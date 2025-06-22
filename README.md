# Quote Search API

A reactive Spring Boot (WebFlux) API backed by MongoDB, designed for fast and efficient retrieval of quotes.  
The API supports querying by ID, by author (case-insensitive), or retrieving all quotes from the database.

---

## 📦 Features

- 🔎 Search quotes by ID or author (case-insensitive)
- 🧵 Reactive Kotlin & Spring WebFlux stack
- 🧪 Unit + Integration tests with Testcontainers
- 📘 Postman collection for exploring endpoints
- ⚡ Load testing scripts (k6) to validate performance targets

---

## 🚀 Getting Started

> 🛠 Prerequisites:
> - Java 17+
> - Docker
> - Gradle (or use the included wrapper `./gradlew`)

### 🔧 1. Start the full stack

Run the provided startup script:

```bash
./clean_start.sh
```

This will:
 - Build the Docker image
 - Launch the app and MongoDB with pre-loaded quotes via Docker Compose (docker-compose file should be present in ../deployment/docker-compose.yml)
 - Expose the backend at http://localhost:8080/hello

## 📬 API Reference

### `GET /quotes/{id}`
Returns a single quote by MongoDB ID.

### `GET /quotes?author=Lauren Bacall`
Returns all quotes (case-insensitive) by the given author.

### `GET /quotes/all`
Streams all quotes in the collection.

---

## 🔁 Postman Collections

A Postman collection is included in:
[Meta coding challenge.postman_collection.json](Meta%20coding%20challenge.postman_collection.json)

-----

# ⚙️ Load Testing with k6

We're using [k6](https://k6.io/) to validate performance targets.

## 📌 Requirements

Install k6 CLI:

```bash
 brew install k6        # macOS
 choco install k6       # Windows
```

Or see [https://grafana.com/docs/k6/next/set-up/install-k6/](https://grafana.com/docs/k6/next/set-up/install-k6/) for other platforms.

## 🧪 Run Load Tests

All test scripts are located in `/load-tests`.

### 🔍 Search by ID – 50 RPS under 200ms

```bash
 k6 run -e QUOTE_ID=5eb17aadb69dc744b4e70d35 load-tests/quote_by_id.js
```

### 🔍 Search by Author – 50 RPS under 200ms

```bash
 k6 run -e AUTHOR="Lauren Bacall" load-tests/quote_by_author.js
```

### 📚 Search All – response under 30s

```bash
 k6 run load-tests/quote_all.js
```

