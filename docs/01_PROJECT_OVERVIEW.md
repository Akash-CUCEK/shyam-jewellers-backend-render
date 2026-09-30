# Shyam Jewellers Backend - Project Overview

## Scope

This document is a Phase 1 discovery artifact for the existing Shyam Jewellers Spring Boot backend. It documents the current implementation only. It does not redesign, refactor, or recommend code changes.

Discovery reflects the repository working tree as inspected on 2026-07-08.

## Business Context

The backend is the single server-side system for:

- Website user portal
- Website admin portal
- Mobile user app
- Mobile admin app

All clients are expected to consume the same REST APIs. The current implementation centralizes authentication, catalog, admin management, order, offer photo, repair request, and home service logic in one Spring Boot application.

## Application Type

The project is a Spring Boot monolith with horizontal technical packages:

```text
com.shyam
  common
  config
  constants
  controller
  dao
  dto
  entity
  mapper
  repository
  service
  validation
```

The dominant request flow is:

```text
Controller -> Service -> Mapper and/or DAO -> Repository -> JPA Entity -> Database
```

Some mapper classes also perform persistence-facing work and email side effects.

## Technology Stack

| Area | Current Implementation |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.3 |
| API | Spring MVC REST controllers |
| Security | Spring Security, method security, JWT filter |
| Persistence | Spring Data JPA, Hibernate |
| Database driver | PostgreSQL dependency in Maven |
| Production profile database config | SQL Server JDBC URL and driver class configured in `application-prod.yml` |
| Cache/session state | Redis via Spring Data Redis and Lettuce |
| Auth tokens | JWT access token, UUID refresh token |
| Password hashing | BCryptPasswordEncoder |
| Email | Spring Mail / JavaMailSender |
| Media upload | Cloudinary Java SDK |
| Excel | Apache POI |
| PDF | iText 5 |
| API docs | Springdoc OpenAPI dependency and selected `@Operation` annotations |
| Build | Maven wrapper and Maven project |
| Container | Root `Dockerfile`, additional compose file under `.mvn/docker-compose.yml` |
| Tests | One Spring context-load test using `test` profile and H2 |

## Current Capabilities

| Capability | Current Status |
|---|---|
| Admin login | Implemented with email/password, JWT access token, Redis refresh token, refresh cookie |
| Admin logout | Implemented with access-token blacklist and refresh-token deletion by email/role/device |
| Admin password reset | Implemented with OTP email and password update |
| Admin management | Register, edit, get, list, delete implemented |
| User login | Implemented as email OTP flow |
| User verification | Implemented with JWT access token and Redis refresh token |
| User logout | Code exists but is commented out and not exposed |
| Refresh token rotation | Implemented by `/refreshToken` using Redis hash validation and deletion/re-store |
| Public catalog | Product list, product by ID, category/material/price/gender/filter searches |
| Admin catalog | Product and category CRUD-style endpoints under admin controller |
| Offer photos | Latest offer-photo update and public read |
| Orders | Create, update, get by ID, paginated admin list, invoice PDF bytes |
| Home service | Create, get, search, edit, delete, list |
| Repair service | Create, get, search, edit, delete, list |
| Email delivery | Used by OTP and admin registration/password flows |
| Cloudinary upload | Used by add-product image upload |
| Excel import | Category upload with validation and error workbook response |

## Runtime Profiles

| File | Current Role |
|---|---|
| `application.yml` | Sets active profile to `local` |
| `application.properties` | Sets `spring.application.name=shyam` |
| `application-local.yml` | Local PostgreSQL, JPA `ddl-auto:update`, mail, Redis, Cloudinary, JWT secret |
| `application-render.yml` | Render-style PostgreSQL environment variables, JPA `ddl-auto:update`, mail, Redis, Cloudinary, JWT secret |
| `application-prod.yml` | SQL Server database URL/driver, JPA `ddl-auto:update`, Flyway disabled, mail, JWT secret |
| `application-test.yml` | H2 in-memory database, `create-drop`, test Redis/mail/cloudinary/JWT placeholders |

## Source of Truth Observed

The active implementation source is under `src/main/java` and `src/main/resources`. The repository also contains generated build output under `target/`; these compiled classes mirror earlier builds and are not treated as design source.

Database migrations exist under:

```text
src/main/resources/migration
```

Spring Boot Flyway defaults expect `db/migration`, and the Maven file does not include a Flyway dependency. The current profile configuration uses Hibernate `ddl-auto:update`.

## Current API Shape

The project uses a response envelope for most REST responses:

```text
BaseResponseDTO<T>
  response: T
  errors: ErrorResponseDTO
```

Error responses are produced by `GlobalException` for `SYMException` and generic `Exception`.

Major route groups:

| Route Group | Purpose |
|---|---|
| `/auth/api/v1/admin/**` | Admin auth, admin management, admin product/category actions |
| `/api/v1/auth/**` | User OTP login and verification |
| `/api/v1/public/**` | Public catalog/category/offer photo APIs |
| `/refreshToken` | Refresh token validation and rotation |
| `/createOrder`, `/updateOrder`, `/getOrderById`, `/admin/orders`, `/getOrderInvoiceById` | Order APIs |
| `/api/homeService/**` | Home service request APIs |
| `/api/common/**` | Repair request APIs |

## Current Documentation State

Existing root-level documentation files are present:

```text
ARCHITECTURE_REVIEW.md
ENTERPRISE_UPGRADE_PLAN.md
README.md
```

This Phase 1 documentation set is separate from those files and is limited to discovery of the current implementation.

