# Trellis — Kanban WebApp

Aplicação web de quadros Kanban (boards, colunas e tarefas) construída com uma arquitetura de microsserviços em **Spring Boot**. Projeto pessoal, **em desenvolvimento**.

> Este repositório contém apenas o lado servidor (gateway, servidor de autenticação e API de boards). O front-end é esperado em `http://localhost:5173` (configurável) e não faz parte deste repositório.

## Arquitetura

```
 Front-end (:5173)
        │
        ▼
 ┌─────────────┐   sessão (Redis)    ┌────────────────┐
 │   gateway   │────────────────────▶│  auth-server   │  OAuth2 / OIDC
 │   (:8081)   │   token relay       │ (Authorization │  Login com Google
 └─────────────┘                     │    Server)     │
        │                            └───────┬────────┘
        ▼                                    │
 ┌─────────────┐                             │
 │   backend   │─────────────┬───────────────┘
 │ boards API  │             ▼
 └─────────────┘     PostgreSQL (Supabase)
```

| Módulo | Função |
|---|---|
| [`gateway`](./gateway) | Spring Cloud Gateway (WebFlux). Faz o papel de cliente OAuth2, mantém a sessão no Redis, aplica CORS e roteia requisições com *token relay*. Acompanha um `Dockerfile`. |
| [`auth-server`](./auth-server) | Spring Authorization Server. Cadastro de usuários, verificação de conta por código enviado por e-mail, troca de senha/usuário/e-mail, login com Google (OIDC) e papéis/privilégios. |
| [`backend`](./backend) | API de domínio do Kanban: boards, colunas e tarefas, com membros por board e prioridades de tarefa. |

## Tecnologias

- Java 17 e Maven (wrapper `mvnw` em cada módulo)
- Spring Boot 4.1 (Web MVC, Data JPA, Validation, Security, Mail, WebSocket no `backend`)
- Spring Authorization Server e Spring Security OAuth2 Client
- Spring Cloud Gateway (WebFlux) + Spring Session com Redis
- PostgreSQL (hospedado no Supabase) com migrações **Flyway**
- MapStruct e Lombok
- JUnit (testes de serviço no `auth-server` e no `backend`)

## Funcionalidades

**Autenticação (`auth-server`)**
- Registro de usuário e verificação de conta com código de 6 dígitos por e-mail
- Login via OIDC, incluindo login com Google
- Alteração de senha, nome de usuário e e-mail, com solicitação de código antes da mudança
- Papéis e privilégios; endpoint `GET /user/me`

**Kanban (`backend`)**
- Boards com dono e membros (`OWNER`, `ADMIN`, `MEMBER`)
- Colunas ordenadas por posição
- Tarefas com título, descrição, data de entrega, prioridade (`BAIXA`, `MEDIA`, `ALTA`), cor e categorias

## Endpoints

**Rotas expostas pelo gateway**

| Rota | Destino | Observação |
|---|---|---|
| `/` | Front-end | Redireciona (302) |
| `/api/register`, `/api/verify` | auth-server | Públicas; cookie removido |
| `/auth/password`, `/auth/username`, `/auth/email` (e `/**`) | auth-server | Protegidas; token relay |

**`auth-server`** — `POST /auth/register`, `POST /auth/account/verify`, `POST /auth/{password|username|email}/change-request`, `PATCH /auth/{password|username|email}`, `GET /user/me`

**`backend`** — `GET /board/{id}`, `POST /board/create`, `PUT /board/update`

> Os endpoints de colunas e tarefas são tratados pelos serviços do `backend`; confira o código para a lista atualizada, pois o projeto está em evolução.

## Pré-requisitos

- JDK 17
- PostgreSQL (o projeto usa o Supabase, mas qualquer instância PostgreSQL serve)
- Redis em `localhost:6379` (para o gateway)
- Conta SMTP para envio dos códigos de verificação
- Credenciais OAuth2 do Google (para o login com Google)

## Configuração

Os `application.properties` leem valores sensíveis de variáveis de ambiente. Defina-as antes de subir os serviços:

| Variável | Usada em | Descrição |
|---|---|---|
| `SUPABASE_DB_URL` | auth-server, backend | URL JDBC do PostgreSQL |
| `SUPABASE_DB_USERNAME` | auth-server, backend | Usuário do banco |
| `SUPABASE_DB_PASSWORD` | auth-server, backend | Senha do banco |
| `SUPPORT_EMAIL` | auth-server | Conta de e-mail que envia os códigos |
| `APP_PASSWORD` | auth-server | Senha (de app) da conta de e-mail |
| `clientId` | auth-server | Client ID do Google OAuth2 |
| `clientSecret` | auth-server | Client Secret do Google OAuth2 |

Configurações do gateway (`gateway/src/main/resources/application.properties`):

```properties
server.port=8081
spring.data.redis.host=localhost
spring.data.redis.port=6379
fe-app.base-uri=http://localhost:5173
oauth2.auth-server-url=http://localhost:8080
oauth2.auth-server-internal-url=http://localhost:8080
oauth2.secure-resource-url=http://localhost:9000
```

O schema do banco é criado e versionado pelo **Flyway** (`src/main/resources/db/migration`) e o Hibernate roda com `ddl-auto=validate`.

## Como rodar

Em terminais separados, a partir da raiz do repositório:

```bash
# 1. Servidor de autenticação
cd auth-server && ./mvnw spring-boot:run

# 2. API do Kanban
cd backend && ./mvnw spring-boot:run

# 3. Gateway
cd gateway && ./mvnw spring-boot:run
```

Se os serviços usarem a mesma porta padrão (8080), ajuste `server.port` de cada um para evitar conflito e mantenha os valores do gateway (`oauth2.*`) e a `redirectUri` do cliente OIDC em `auth-server/.../config/SecurityConfig.java` coerentes com as portas escolhidas.

### Docker (gateway)

```bash
cd gateway
docker build -t trellis-gateway .
docker run -p 8081:8081 trellis-gateway
```

### Testes

```bash
cd auth-server && ./mvnw test
cd backend && ./mvnw test
```

## Estrutura do repositório

```
.
├── auth-server/   # Spring Authorization Server + cadastro/verificação de usuários
├── backend/       # API de boards, colunas e tarefas
└── gateway/       # Spring Cloud Gateway (sessão, CORS, token relay)
```

## Status

Em desenvolvimento. Alguns pontos ainda em evolução, como a segurança do `backend` (hoje com `permitAll`, assumindo que o acesso passa pelo gateway), a configuração de portas e URLs para produção e o front-end, que ainda não está neste repositório.

## Licença

Nenhuma licença definida até o momento.