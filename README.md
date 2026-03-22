# User Profile API

![CI](https://github.com/leonlimask20-dot/user-profile-api/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?logo=spring&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-autenticação-000000?logo=jsonwebtokens&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-compose-2496ED?logo=docker&logoColor=white)
![Testes](https://img.shields.io/badge/testes-JUnit5%20+%20Mockito-2E7D32)

API REST para gerenciamento de perfis de usuários com autenticação JWT stateless, controle de acesso por papéis e documentação interativa via Swagger UI.

---

## Links rápidos

| | |
|---|---|
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| Rodar com Docker | `docker-compose up --build` |
| Rodar testes | `mvn test` |

---

## Principais competências demonstradas

- API REST segura com autenticação JWT stateless
- Controle de acesso por papéis: USER e ADMIN
- Spring Security com SecurityFilterChain e filtro JWT customizado
- BCrypt para hash de senhas com salt aleatório
- Tratamento centralizado de erros com `@RestControllerAdvice`
- Documentação interativa com Swagger UI (OpenAPI 3)
- Testes unitários com JUnit 5 e Mockito
- Containerização com Docker e Docker Compose
- Pipeline de CI com GitHub Actions

---

## Tecnologias

| Tecnologia | Versão |
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

## Fluxo de autenticação

```
1. POST /api/auth/register  →  cria usuário com senha BCrypt
2. POST /api/auth/login     →  valida credenciais, retorna JWT
3. GET  /api/users/profile  →  JwtAuthFilter valida o token
4. Acesso liberado          →  retorna dados do perfil
```

---

## Endpoints

| Método | Rota | Acesso |
|--------|------|--------|
| POST | `/api/auth/register` | Público |
| POST | `/api/auth/login` | Público |
| GET | `/api/users/profile` | USER, ADMIN |
| PUT | `/api/users/profile` | USER, ADMIN |
| DELETE | `/api/users/{id}` | Apenas o próprio usuário |

---

## Como executar

```bash
docker-compose up --build
```

Acesse o Swagger UI em `http://localhost:8080/swagger-ui.html`

---

## Testes

```bash
mvn test
```

---

## Autor

**LNL**
GitHub: [@leonlimask20-dot](https://github.com/leonlimask20-dot)
Email: leonlimask@gmail.com
