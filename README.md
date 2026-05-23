# User Profile API

![CI](https://github.com/leonlimask20-dot/user-profile-api/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?logo=spring&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-authentication-000000?logo=jsonwebtokens&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-compose-2496ED?logo=docker&logoColor=white)
![Tests](https://img.shields.io/badge/tests-JUnit5%20+%20Mockito-2E7D32)

REST API for managing user profiles with stateless JWT authentication,
role-based access control and interactive documentation via Swagger UI.

---

## Quick links

| | |
|---|---|
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| Run with Docker | `docker-compose up --build` |
| Run tests | `mvn test` |

---

## Key skills demonstrated

- Secure REST API with stateless JWT authentication
- Role-based access control: USER and ADMIN
- Spring Security with SecurityFilterChain and a custom JWT filter
- BCrypt for password hashing with a random salt
- Centralized error handling with `@RestControllerAdvice`
- Interactive documentation with Swagger UI (OpenAPI 3)
- Unit tests with JUnit 5 and Mockito
- Containerization with Docker and Docker Compose
- CI pipeline with GitHub Actions

---

## Tech stack

| Technology | Version |
|---|---|
| Java | 17 |
| Spring Boot | 3.2.3 |
| Spring Security | 6.x |
| Spring Data JPA | 3.x |
| PostgreSQL | 15 |
| JWT (jjwt) | 0.11.5 |
| Swagger UI | springdoc-openapi 2.3 |
| JUnit 5 + Mockito | — |
| Docker + Docker Compose | — |

---

## Authentication flow

```
1. POST /api/auth/register  →  creates a user with a BCrypt-hashed password
2. POST /api/auth/login     →  validates credentials, returns a JWT
3. GET  /api/users/profile  →  JwtAuthFilter validates the token
4. Access granted           →  returns the profile data
```

---

## Endpoints

| Method | Route | Access |
|--------|------|--------|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |
| GET | `/api/users/profile` | USER, ADMIN |
| PUT | `/api/users/profile` | USER, ADMIN |
| DELETE | `/api/users/{id}` | The user themselves only |

---

## How to run

```bash
docker-compose up --build
```

Open the Swagger UI at `http://localhost:8080/swagger-ui.html`

---

## Tests

```bash
mvn test
```

---

## 🤖 Agent Architecture

This project was built and code-reviewed using a **multi-agent
context-optimization workflow**: specialized AI agents each audit a single
slice of the codebase — security, controllers, persistence, tests — within a
strict context budget. The approach cuts review time and token cost while
keeping full traceability of every finding.

Methodology, agent templates and the full playbook: **[Stop Burning Context — Claude Code Playbook](https://leonlim3.gumroad.com/l/claude-code-context-playbook)**

---

## Author

**LNL**
GitHub: [@leonlimask20-dot](https://github.com/leonlimask20-dot)
Email: leonlimask@gmail.com
