# Hands Of Retail — Backend

> A stateless Spring Boot REST API for managing retail operations: clients, stores, and multi-period sales reports with JWT-based security.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
   - [System Description](#11-system-description)
   - [Goals](#12-goals)
   - [Key Features](#13-key-features)
2. [Architecture](#2-architecture)
   - [Package Structure](#21-package-structure)
   - [Data Model](#22-data-model)
3. [Technologies Used](#3-technologies-used)
4. [Getting Started](#4-getting-started)
   - [Prerequisites](#41-prerequisites)
   - [Local Development Setup](#42-local-development-setup)
   - [Running Locally](#43-running-locally)
   - [Building for Production](#44-building-for-production)
   - [Running in Production](#45-running-in-production)
5. [Configuration](#5-configuration)
   - [Environment Variables](#51-environment-variables)
   - [Spring Profiles](#52-spring-profiles)
6. [API Reference](#6-api-reference)
   - [Authentication](#61-authentication)
   - [Admin — Client Management](#62-admin--client-management)
   - [Admin — Store Management](#63-admin--store-management)
   - [Admin — Daily Reports](#64-admin--daily-reports)
   - [Admin — Monthly Reports](#65-admin--monthly-reports)
   - [Admin — Yearly Reports](#66-admin--yearly-reports)
   - [Client — Self-Service Endpoints](#67-client--self-service-endpoints)
7. [Authentication & Authorization](#7-authentication--authorization)
8. [Database Migrations](#8-database-migrations)
9. [Branching Workflow](#9-branching-workflow)
10. [License](#10-license)

---

## 1. Project Overview

### 1.1 System Description

**Hands Of Retail Backend** is a stateless Spring Boot REST API service that manages retail operations. It provides a secure, JWT-authenticated interface for two user roles:

- **ADMIN** — full control over clients, stores, and all report types.
- **CLIENT** — read-only access to data scoped to their own account.

Tokens are delivered via **HttpOnly cookies** (access + refresh), keeping credentials out of JavaScript. The refresh-token is stored in the database and rotated on every use.

### 1.2 Goals

- Implement robust JWT-based authentication with cookie transport and role-based authorization.
- Provide comprehensive CRUD APIs for client and store master data.
- Enable creation, update, and querying of **daily**, **monthly**, and **yearly** sales reports.
- Support **bulk Excel (.xlsx) upload** for monthly reports.
- Deliver standardized JSON response envelopes and detailed validation error handling.
- Expose discoverable API documentation via **Swagger / OpenAPI UI**.

### 1.3 Key Features

| Feature | Details |
|---|---|
| JWT Auth (cookie-based) | Access token (15 min) + HttpOnly refresh token (7 days), auto-rotated |
| Role-based access control | `ADMIN` and `CLIENT` roles with separate route namespaces |
| Client management | Create, list, update client accounts |
| Store management | Create, list, get, update, toggle status |
| Daily report management | Create, query (by store / date range), update |
| Monthly report management | Create, query (by store / client / year / month), update, Excel bulk upload |
| Yearly report management | Create, query (by store / client / year), update |
| Global exception handling | Standardized `ApiResponse<T>` envelopes for every endpoint |
| H2 console | Available at `/h2-console` in the `local` profile |
| Flyway migrations | Schema versioned with `V1__init_schema.sql` and `V2__refresh_tokens.sql` |
| OpenAPI / Swagger UI | Auto-generated docs at `/swagger-ui.html` |

---

## 2. Architecture

### 2.1 Package Structure

```
src/main/java/com/handsofretail/hor/
├── HorApplication.java          # Spring Boot entry point
├── config/                      # Security, CORS, Swagger, etc.
├── controller/
│   ├── auth/                    # AuthController (login, refresh, logout)
│   ├── admin/                   # Admin-only controllers
│   └── client/                  # Client-scoped read controllers
├── dto/
│   ├── request/                 # Inbound request DTOs (with validation)
│   └── response/                # Outbound response DTOs + ApiResponse<T>
├── entity/                      # JPA entities (ClientUser, Store, *Report, …)
├── enums/                       # Status, Role, etc.
├── exception/                   # Global exception handler
├── mapper/                      # Entity ↔ DTO mappers
├── repository/                  # Spring Data JPA repositories
├── security/
│   ├── jwt/                     # JWT filter, provider, cookie util
│   ├── user/                    # UserDetailsService implementations
│   └── handler/                 # Auth entry points & access-denied handlers
├── service/                     # Business logic layer
├── specification/               # JPA Specifications for dynamic queries
└── util/                        # Shared utilities (Excel parser, etc.)
```

### 2.2 Data Model

```
admin_users
  └── (separate auth table for admin accounts)

client_users
  └── stores  (one client → many stores)
       ├── daily_reports   (one store → many daily entries)
       ├── monthly_reports (one store → many monthly department rows)
       └── yearly_reports  (one store → many yearly summaries)

refresh_tokens
  └── (one active token per user; rotated on every /auth/refresh call)
```

---

## 3. Technologies Used

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.4.x |
| Security | Spring Security + JWT (`jjwt 0.11.5`) + OAuth2 Resource Server |
| Persistence | Spring Data JPA + Hibernate |
| DB — Local | H2 (PostgreSQL compatibility mode, file-based) |
| DB — Production | PostgreSQL |
| Migrations | Flyway 9.20 |
| Build | Gradle 8 (Wrapper included) |
| Validation | Jakarta Bean Validation (`spring-boot-starter-validation`) |
| Excel parsing | Apache POI 5.3 (`poi-ooxml`) |
| API Docs | springdoc-openapi 2.8.9 (Swagger UI) |
| Utilities | Lombok, Jackson JSR-310, Spring DevTools |
| Testing | JUnit 5, Spring Boot Test, Spring Security Test |

---

## 4. Getting Started

### 4.1 Prerequisites

- **JDK 21** or newer ([Adoptium](https://adoptium.net/) recommended)
- **Git**
- A compatible IDE — VS Code (Java Extension Pack) or IntelliJ IDEA
- *(Production only)* A running **PostgreSQL** instance

No additional tooling is required — the project ships with a Gradle Wrapper (`gradlew` / `gradlew.bat`).

### 4.2 Local Development Setup

**1. Clone the repository**

```bash
git clone https://github.com/your-username/hor.git
cd hor
```

**2. Create the `.env` file**

Copy the example below and save it as `.env` in the project root. **Never commit this file.**

```properties
# App
SPRING_APPLICATION_NAME=hor
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=local

# H2 datasource (PostgreSQL compatibility mode)
DB_URL=jdbc:h2:file:./database/h2/retail-db;MODE=PostgreSQL;DB_CLOSE_DELAY=-1
DB_DRIVER=org.h2.Driver
DB_USERNAME=sa
DB_PASSWORD=

# JPA / Hibernate
JPA_HIBERNATE_DDL_AUTO=validate
JPA_SHOW_SQL=true
JPA_FORMAT_SQL=true
JPA_DATABASE_PLATFORM=org.hibernate.dialect.H2Dialect

# JWT
JWT_SECRET=yourVeryStrongSecretKeyHereMinimum32Characters
JWT_ACCESS_EXPIRATION=900000        # 15 min in ms
JWT_REFRESH_EXPIRATION=604800000    # 7 days in ms
JWT_ACCESS_COOKIE_NAME=access_token
JWT_REFRESH_COOKIE_NAME=refresh_token
JWT_REFRESH_COOKIE_SECURE=false     # false for local HTTP

# File upload limits
MULTIPART_MAX_FILE_SIZE=10MB
MULTIPART_MAX_REQUEST_SIZE=10MB
```

### 4.3 Running Locally

**Option 1 — Load `.env` then `bootRun` (recommended)**

*PowerShell (Windows):*
```powershell
Get-Content .env | ForEach-Object {
  if ($_ -match '^\s*#' -or $_ -match '^\s*$') { return }
  $p = $_ -split '=', 2
  if ($p.Length -eq 2) { Set-Item -Path "Env:$($p[0].Trim())" -Value $p[1].Trim() }
}
./gradlew.bat bootRun
```

*Bash / macOS / Linux:*
```bash
set -a && source .env && set +a
./gradlew bootRun
```

**Option 2 — Activate the local profile directly**

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

> The application starts on **http://localhost:8080**  
> H2 Console: **http://localhost:8080/h2-console**  
> Swagger UI: **http://localhost:8080/swagger-ui.html**

### 4.4 Building for Production

```bash
./gradlew bootJar
```

The executable JAR is placed in `build/libs/hor-0.0.1-SNAPSHOT.jar`.

### 4.5 Running in Production

**1. Set environment variables** (use your cloud provider's secret manager — do _not_ put secrets in `.env.prod` on the server):

```bash
export SPRING_PROFILES_ACTIVE=prod
export DB_URL=jdbc:postgresql://your_host:5432/hands_of_retail
export DB_DRIVER=org.postgresql.Driver
export DB_USERNAME=your_db_user
export DB_PASSWORD=your_db_password
export JPA_HIBERNATE_DDL_AUTO=validate
export JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
export JWT_SECRET=YourHighlySecureProductionSecret
export JWT_REFRESH_COOKIE_SECURE=true    # HTTPS required
```

**2. Start the application:**

```bash
java -jar build/libs/hor-0.0.1-SNAPSHOT.jar
```

Flyway will run any pending migrations automatically on startup.

---

## 5. Configuration

### 5.1 Environment Variables

All configuration is injected via environment variables. The table below lists every supported variable with its default value (where applicable).

| Variable | Description | Default |
|---|---|---|
| `SERVER_PORT` | HTTP port | `8080` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile (`local` / `prod`) | `local` |
| `DB_URL` | JDBC connection URL | H2 file DB |
| `DB_DRIVER` | JDBC driver class | `org.h2.Driver` |
| `DB_USERNAME` | DB username | `sa` |
| `DB_PASSWORD` | DB password | *(empty)* |
| `JPA_HIBERNATE_DDL_AUTO` | Hibernate DDL mode | `validate` |
| `JPA_DATABASE_PLATFORM` | Hibernate dialect | H2Dialect |
| `JPA_SHOW_SQL` | Log SQL statements | `true` |
| `JWT_SECRET` | HMAC signing key (≥ 32 chars) | *(placeholder)* |
| `JWT_ACCESS_EXPIRATION` | Access token TTL (ms) | `900000` (15 min) |
| `JWT_REFRESH_EXPIRATION` | Refresh token TTL (ms) | `604800000` (7 days) |
| `JWT_ACCESS_COOKIE_NAME` | Access token cookie name | `access_token` |
| `JWT_REFRESH_COOKIE_NAME` | Refresh token cookie name | `refresh_token` |
| `JWT_REFRESH_COOKIE_SECURE` | `Secure` flag on refresh cookie | `false` |
| `MULTIPART_MAX_FILE_SIZE` | Max single file upload size | `10MB` |
| `MULTIPART_MAX_REQUEST_SIZE` | Max multipart request size | `10MB` |
| `SWAGGER_UI_PATH` | Swagger UI path | `/swagger-ui.html` |
| `API_DOCS_PATH` | OpenAPI JSON path | `/api-docs` |

### 5.2 Spring Profiles

| Profile | Datasource | Flyway | Notes |
|---|---|---|---|
| `local` | H2 (file, PostgreSQL mode) | Enabled | H2 console enabled, SQL logged |
| `prod` | PostgreSQL | Enabled | `Secure` cookies, minimal logging |

---

## 6. API Reference

All endpoints return a consistent envelope:

```json
{
  "success": true,
  "message": "Human-readable message",
  "data": { ... }
}
```

Interactive docs are available at **`/swagger-ui.html`** when the app is running.

---

### 6.1 Authentication

Base path: `/api/v1/auth` — **Public** (no token required)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/login` | Authenticate with `email` + `password`. Returns access token in body and sets an HttpOnly refresh-token cookie. |
| `POST` | `/refresh` | Rotate refresh token. Reads the `refresh_token` cookie; returns a new access token and sets a new cookie. |
| `POST` | `/logout` | Revoke refresh token from DB and clear cookies. |

**Login request body:**
```json
{
  "email": "admin@example.com",
  "password": "secret"
}
```

**Login response `data`:**
```json
{
  "accessToken": "<jwt>",
  "tokenType": "Bearer",
  "role": "ADMIN"
}
```

---

### 6.2 Admin — Client Management

Base path: `/api/v1/admin/clients` — **ADMIN only**

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/` | Create a new client account |
| `GET` | `/` | List all clients |
| `PUT` | `/{id}` | Update a client by ID |

---

### 6.3 Admin — Store Management

Base path: `/api/v1/admin/stores` — **ADMIN only**

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/` | Create a new store |
| `GET` | `/` | List stores (filter by `clientId`, `status`) |
| `GET` | `/{storeId}` | Get a specific store |
| `PUT` | `/{storeId}` | Update store details |
| `PATCH` | `/{storeId}/status` | Toggle store status (`ACTIVE` / `INACTIVE`) |

---

### 6.4 Admin — Daily Reports

Base path: `/api/v1/admin/daily-reports` — **ADMIN only**

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/` | Create a daily report entry |
| `GET` | `/` | Query reports (filter by `storeId`, `clientId`, `from`, `to` — ISO dates) |
| `GET` | `/store/{storeId}` | Get all daily reports for a store |
| `PUT` | `/{dailyReportId}` | Update a daily report |

---

### 6.5 Admin — Monthly Reports

Base path: `/api/v1/admin/monthly-reports` — **ADMIN only**

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/` | Create a monthly report row |
| `GET` | `/` | Query reports (filter by `storeId`, `clientId`, `year`, `month`) |
| `GET` | `/store/{storeId}` | Get all monthly reports for a store |
| `PUT` | `/{monthlyReportId}` | Update a monthly report row |
| `POST` | `/upload` | **Bulk upload** monthly reports from an `.xlsx` file |

**Excel upload form parameters:**

| Param | Type | Description |
|---|---|---|
| `storeId` | `Long` | Target store |
| `reportMonth` | `Integer` | Report month (1–12) |
| `reportYear` | `Integer` | Report year (e.g. 2025) |
| `file` | `MultipartFile` | `.xlsx` file (max 10 MB) |

---

### 6.6 Admin — Yearly Reports

Base path: `/api/v1/admin/yearly-reports` — **ADMIN only**

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/` | Create a yearly report |
| `GET` | `/` | Query reports (filter by `storeId`, `clientId`, `year`) |
| `GET` | `/store/{storeId}` | Get all yearly reports for a store |
| `PUT` | `/{yearlyReportId}` | Update a yearly report |

---

### 6.7 Client — Self-Service Endpoints

Base path: `/api/v1/client/…` — **CLIENT role only** — read-only, scoped to the authenticated client.

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/client/stores` | List own stores |
| `GET` | `/client/daily-reports` | List own daily reports |
| `GET` | `/client/monthly-reports` | List own monthly reports |
| `GET` | `/client/yearly-reports` | List own yearly reports |

---

## 7. Authentication & Authorization

```
POST /api/v1/auth/login
    │
    ├─ Issues short-lived access token (JWT, 15 min) ──► returned in response body
    └─ Issues long-lived refresh token (7 days)      ──► stored in DB + set as HttpOnly cookie

Every protected request:
    Authorization: Bearer <access_token>   (or extracted from access_token cookie)

POST /api/v1/auth/refresh
    ├─ Reads refresh_token cookie
    ├─ Validates token against DB (rotation check)
    ├─ Deletes old token, issues new pair
    └─ Returns new access token + sets new refresh cookie

POST /api/v1/auth/logout
    ├─ Revokes refresh token from DB
    └─ Clears both cookies
```

**Role access matrix:**

| Resource | ADMIN | CLIENT |
|---|---|---|
| `/api/v1/auth/**` | ✅ | ✅ |
| `/api/v1/admin/**` | ✅ | ❌ |
| `/api/v1/client/**` | ❌ | ✅ (own data only) |
| `/swagger-ui.html`, `/h2-console` | ✅ (local only) | ✅ (local only) |

---

## 8. Database Migrations

Flyway manages all schema changes. Migration files live in:

```
src/main/resources/db/migration/
├── V1__init_schema.sql       # Core tables: client_users, admin_users, stores, *_reports
└── V2__refresh_tokens.sql    # refresh_tokens table for token rotation
```

Flyway runs automatically on startup. To add a new migration, create `V3__description.sql` in the same directory — never edit existing migration files.

---

## 9. Branching Workflow

| Branch | Purpose |
|---|---|
| `main` | Stable, production-ready code |
| `develop` | Integration branch for feature work |
| `feature/<name>` | Individual feature branches, merged into `develop` via PR |
| `hotfix/<name>` | Critical fixes branched from `main`, merged back to `main` + `develop` |

---

## 10. License

This project is proprietary. All rights reserved.
