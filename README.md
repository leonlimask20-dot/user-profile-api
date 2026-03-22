# API de Perfis de Usuário
![CI](https://github.com/leonlimask20-dot/user-profile-api/actions/workflows/ci.yml/badge.svg)
API REST segura para gerenciamento de perfis com autenticação JWT stateless, controle de acesso por papéis, persistência em PostgreSQL e testes automatizados.

---

## Links rápidos

| | |
|---|---|
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/api-docs` |
| Rodar com Docker | [Ir para seção](#execução) |
| Rodar localmente | [Ir para seção](#sem-docker) |

---

## Principais competências demonstradas

- Autenticação JWT stateless com Spring Security
- Autorização por papel (`ROLE_USER`, `ROLE_ADMIN`) e por recurso (dono do perfil)
- Persistência com Spring Data JPA e PostgreSQL
- Documentação interativa com OpenAPI 3 / Swagger UI
- Containerização com Docker e Docker Compose
- Testes unitários com JUnit 5 e Mockito
- Tratamento centralizado de erros com `@RestControllerAdvice`
- Validação de entrada com Bean Validation

---

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 17 |
| Spring Boot | 3.2.3 |
| Spring Security | 6.x |
| Spring Data JPA | 3.x |
| PostgreSQL | 15+ |
| jjwt | 0.11.5 |
| springdoc-openapi | 2.3.0 |
| Lombok | — |
| JUnit 5 + Mockito | — |
| Docker + Docker Compose | — |

---

## Fluxo de autenticação

```
1. POST /api/auth/login  →  recebe email e senha
2. AuthenticationManager →  valida credenciais via BCrypt
3. JwtService            →  gera token assinado com HMAC-SHA256
4. JwtAuthFilter         →  valida o token a cada requisição subsequente
```

O servidor não armazena sessões. A identidade do usuário é extraída do token a cada chamada.

---

## Arquitetura

```
src/main/java/com/leonlima/userapi/
├── controller/    → Camada web: recebe requisições e retorna respostas HTTP
├── service/       → Regras de negócio e controle de acesso por recurso
├── repository/    → Acesso a dados via Spring Data JPA
├── model/         → Entidades JPA
├── dto/           → Contratos de entrada e saída da API
├── security/      → JwtAuthFilter, SecurityConfig, JwtService, OpenApiConfig
└── exception/     → Tratamento centralizado de erros
```

---

## Segurança

### Autenticação vs Autorização

**Autenticação** — quem é o usuário:
- Verificada no login pelo `AuthenticationManager` + `DaoAuthenticationProvider`
- A cada requisição, o `JwtAuthFilter` extrai e valida o token JWT do header `Authorization`
- Token válido → usuário registrado no `SecurityContextHolder`

**Autorização** — o que o usuário pode fazer:
- Aplicada em duas camadas:
  1. **Por rota** na `SecurityConfig`: `/api/admin/**` exige `ROLE_ADMIN`
  2. **Por recurso** no `UserService`: usuário comum só acessa o próprio perfil

### Proteção de senhas

Senhas armazenadas com `BCryptPasswordEncoder`. O BCrypt aplica salt aleatório a cada hash — duas senhas iguais geram hashes diferentes. A senha nunca é retornada nos DTOs de resposta.

### Tokens JWT

- Assinados com HMAC-SHA256 e chave secreta de 256 bits
- Expiração configurável via `jwt.expiration` (padrão: 24 horas)
- Stateless: o servidor não armazena sessões — cada requisição é validada pelo token

### Controle de acesso por recurso

A checagem está no `UserService`, não no controller — garante que a regra é aplicada independente de como o endpoint é chamado:

```java
if (!requester.getId().equals(id) && requester.getRole() != User.Role.ADMIN) {
    throw new AccessDeniedException("Acesso negado ao perfil solicitado");
}
```

### Validações de entrada

Todos os campos são validados via Bean Validation: email obrigatório e com formato válido, senha com mínimo 6 caracteres, nome entre 2 e 100 caracteres. Erros retornam 400 com todos os campos inválidos listados.

---

## Pré-requisitos

- Java 17+
- Maven 3.8+
- Docker Desktop

---

## Execução

### Com Docker Compose

```bash
docker-compose up --build
```

Sobe a aplicação e o PostgreSQL automaticamente. Aguarde o healthcheck do banco antes da API inicializar.

### Sem Docker

```sql
CREATE DATABASE userprofiledb;
```

Configure `src/main/resources/application.properties` e execute:

```bash
mvn spring-boot:run
```

API disponível em `http://localhost:8080`.

---

## Documentação interativa

Acesse o Swagger UI em **http://localhost:8080/swagger-ui.html**

<!-- Substitua pela sua captura de tela do Swagger UI -->
<!-- ![Swagger UI](docs/swagger-ui.png) -->

Para testar endpoints protegidos:
1. Execute `POST /api/auth/login`
2. Copie o token da resposta
3. Clique em **Authorize** no topo da página
4. Cole o token no formato: `Bearer SEU_TOKEN` e confirme

---

## Testes

```bash
mvn test
```

Os testes usam banco H2 em memória — sem dependência de PostgreSQL rodando.

Cobertura: `AuthServiceTest` e `UserServiceTest` com JUnit 5 + Mockito cobrindo os fluxos principais de autenticação, autorização e CRUD de perfis.

---

## Endpoints

### Autenticação (público)

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/auth/register` | Cadastro — retorna token JWT |
| POST | `/api/auth/login` | Login — retorna token JWT |

### Perfis (requer `Authorization: Bearer <token>`)

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/users/{id}` | Consultar perfil |
| PUT | `/api/users/{id}` | Atualizar perfil (parcial) |
| DELETE | `/api/users/{id}` | Remover conta |
| GET | `/api/users` | Listar todos (somente ADMIN) |

### Utilitários

| Rota | Descrição |
|------|-----------|
| `/swagger-ui.html` | Documentação interativa |
| `/api-docs` | Especificação OpenAPI 3 (JSON) |

---

## Exemplos

### Cadastro

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Leon Lima",
    "email": "leon@email.com",
    "password": "senha123",
    "bio": "Desenvolvedor Java"
  }'
```

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "userId": 1,
  "name": "Leon Lima",
  "email": "leon@email.com",
  "role": "USER"
}
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "leon@email.com", "password": "senha123"}'
```

### Consultar perfil

```bash
curl http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer SEU_TOKEN"
```

### Atualizar perfil

```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"bio": "Desenvolvedor Java Pleno"}'
```

---

## Considerações para produção

- Substituir `ddl-auto=update` por migrations com Flyway ou Liquibase
- Mover o segredo JWT para AWS Secrets Manager ou Kubernetes Secrets
- Configurar HTTPS com certificado TLS
- Adicionar rate limiting no endpoint de login para mitigar brute force
- Substituir H2 nos testes por Testcontainers para paridade com o banco de produção

---

## Autor

**LNL**
GitHub: [@leonlimask20-dot](https://github.com/leonlimask20-dot)
Email: leonlimask@gmail.com
