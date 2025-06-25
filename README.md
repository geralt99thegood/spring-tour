# Springtour

This is a Java 17 Spring Boot application that exposes REST APIs to fetch users from reqres.in.

## Features
- Get user by ID (proxies to reqres.in)
- List users (proxies to reqres.in)

## How to Run

```
./mvnw spring-boot:run
```

## API Endpoints
- `GET /api/users/{id}`: Get user by ID
- `GET /api/users`: List users
