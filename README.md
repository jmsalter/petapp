# Animal Picture App

Java microservice for fetching random cat, dog, or bear pictures, storing them in an embedded database, and retrieving the latest stored picture for each animal type.

## Stack

- Java 17
- Spring Boot 3
- Spring Web + Spring Data JPA
- H2 embedded database
- Thymeleaf for a minimal UI
- Maven build

## Why H2

For this challenge, H2 is the best fit because it ships inside the application, needs no separate install, works locally and in Docker, and persists to a single file under `./data`. If this were moving beyond a take-home exercise, I would switch to PostgreSQL.

## What It Does

- `POST /api/pictures`
  - Fetches and stores `count` pictures for the requested animal type.
- `GET /api/pictures/{animalType}/latest`
  - Returns metadata for the latest stored picture of that animal type.
- `GET /api/pictures/{id}/content`
  - Streams the stored image bytes back to the client.
- `GET /`
  - Simple UI to fetch new pictures and preview the latest saved image.

## Request Example

```json
{
  "animalType": "cat",
  "count": 3
}
```

## Run Locally

Prerequisites:

- Java 17+

Run:

```bash
./mvnw spring-boot:run
```

Then open:

- UI: `http://localhost:8080`
- H2 console: `http://localhost:8080/h2-console`

The default database URL is:

```text
jdbc:h2:file:./data/animal-picture-app
```

## Run Tests

```bash
./mvnw test
```

## Build JAR

```bash
./mvnw clean package
java -jar target/animal-picture-app-0.0.1-SNAPSHOT.jar
```

## Run With Docker

```bash
docker compose up --build
```

This uses the same embedded H2 database, mounted to `./data` so the stored pictures survive container restarts.

## VS Code

Open the folder in VS Code and import the Maven project. The Java Extension Pack will detect `pom.xml` automatically, and the included Maven wrapper means no local Maven install is required.

## Notes

- The remote image services are called directly using Java's built-in `HttpClient`.
- `place.dog` and `placebear.com` are used directly. For cats, the app uses `placekittens.com` as a drop-in alternative because `placekitten.com` has been unreliable; the challenge brief explicitly allows that fallback.
- Images are stored in the database as binary data to keep the app self-contained.
- Tests cover the service and REST API behavior.
