# Copilot Instructions for spring-tour

- This is a Java 17 Spring Boot application.
- Exposes REST APIs to fetch users from reqres.in (proxy endpoints).
- Endpoints:
  - `GET /api/users/{id}`: Get user by ID (proxies to reqres.in)
  - `GET /api/users?page={page}`: List users (proxies to reqres.in)
- Use WebClient for HTTP calls.
- Follow best practices for Spring Boot REST APIs.
