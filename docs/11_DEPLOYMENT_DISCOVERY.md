# Shyam Jewellers Backend - Deployment Discovery

## Build System

The project is built with Maven.

Key files:

```text
pom.xml
mvnw
mvnw.cmd
.mvn/wrapper/maven-wrapper.properties
```

Maven project coordinates:

| Field | Value |
|---|---|
| Group ID | `com.shyam` |
| Artifact ID | `shyam` |
| Version | `0.0.1-SNAPSHOT` |
| Java version | 21 |
| Spring Boot parent | 3.5.3 |

Build plugins:

- `maven-compiler-plugin` with Lombok annotation processor.
- `spring-boot-maven-plugin`.
- `spotless-maven-plugin` using Google Java Format.

## Main Dependencies

| Capability | Dependency |
|---|---|
| Web | `spring-boot-starter-web` |
| JPA | `spring-boot-starter-data-jpa` |
| Security | `spring-boot-starter-security` |
| OAuth2 client | `spring-boot-starter-oauth2-client` |
| Mail | `spring-boot-starter-mail` |
| Redis | `spring-boot-starter-data-redis` |
| JWT | `jjwt-api`, `jjwt-impl`, `jjwt-jackson` |
| PDF | `itextpdf` |
| Excel | `poi-ooxml` |
| OpenAPI | `springdoc-openapi-starter-webmvc-ui` |
| Database | `postgresql` |
| Cloudinary | `cloudinary-http44` |
| Validation | `hibernate-validator` |
| Test | Spring Boot test, Spring Security test, H2 |

## Dockerfile

Root `Dockerfile` is a two-stage build:

```text
Build stage:
  image: maven:3.9.6-eclipse-temurin-21
  workdir: /app
  copy pom.xml and src
  run mvn clean package -DskipTests

Runtime stage:
  image: eclipse-temurin:21-jre-alpine
  workdir: /app
  copy target jar as app.jar
  expose 8080
  entrypoint java -jar app.jar
```

Current behavior:

- Tests are skipped during image build.
- Runtime image exposes port 8080.
- Active Spring profile depends on application configuration/environment; `application.yml` defaults to `local`.

## Docker Compose

Compose file exists at:

```text
.mvn/docker-compose.yml
```

Services:

| Service | Current Definition |
|---|---|
| `redis` | Uses `redis:7`, container `redis_cache`, maps `6379:6379` |
| `backend` | Builds context `.`, dockerfile `docker/Dockerfile`, depends on Redis, maps `8080:8080`, mounts `./uploads:/app/uploads` |

Observed compose details:

- Compose file is under `.mvn`, not project root.
- Backend references `docker/Dockerfile`, while the repository contains root `Dockerfile`.
- Compose does not define PostgreSQL or SQL Server.
- Backend environment variables include DB host/port/name/user/password, mail credentials, Redis host/port.
- Compose does not include JWT or Cloudinary variables.

## Runtime Profiles

### Default

`application.yml` sets:

```text
spring.profiles.active=local
```

### Local

`application-local.yml`:

| Area | Current Value |
|---|---|
| Server port | `${PORT:8080}` |
| Database | PostgreSQL |
| JPA DDL | `update` |
| SQL logging | `${JPA_SHOW_SQL:true}` |
| Mail | SMTP configuration from env/defaults |
| Redis | Host/port/password/SSL from env/defaults |
| Cloudinary | Env/default placeholders |
| JWT | `${JWT_SECRET:local-development-jwt-secret-change-me-32chars}` |

### Render

`application-render.yml`:

| Area | Current Value |
|---|---|
| Server port | `${PORT:8080}` |
| Database | PostgreSQL from `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` |
| JPA DDL | `update` |
| Mail | Gmail SMTP |
| Redis | Env-driven |
| Cloudinary | Env-driven |
| JWT | Env-driven |

### Prod

`application-prod.yml`:

| Area | Current Value |
|---|---|
| Server port | 8080 |
| Database | SQL Server JDBC URL and driver |
| JPA DDL | `update` |
| Hibernate dialect | SQL Server |
| Flyway | Disabled |
| Mail | Env-driven Gmail SMTP |
| JWT | Env-driven |

Maven currently includes PostgreSQL driver but no SQL Server JDBC dependency.

### Test

`application-test.yml`:

| Area | Current Value |
|---|---|
| Database | H2 in-memory with PostgreSQL mode |
| JPA DDL | `create-drop` |
| Mail | Localhost port 2525 |
| Redis | Localhost 6379 |
| Cloudinary | Test placeholders |
| JWT | Test placeholder secret |

## Environment Variables

`.env.example` lists:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
MAIL_HOST
MAIL_PORT
MAIL_USERNAME
MAIL_PASSWORD
REDIS_HOST
REDIS_PORT
REDIS_PASSWORD
REDIS_SSL
CLOUDINARY_CLOUD_NAME
CLOUDINARY_API_KEY
CLOUDINARY_API_SECRET
JWT_SECRET
```

`application-prod.yml` additionally expects:

```text
DB_HOST
DB_PORT
DB_NAME
```

## Database Migration Deployment State

Migration scripts are present under:

```text
src/main/resources/migration
```

Current deployment-relevant observations:

- `pom.xml` does not include Flyway dependency.
- Default Flyway location is not used.
- Production profile disables Flyway.
- Hibernate DDL update is enabled in local/render/prod profiles.

## Deployment Readiness Observations

| Area | Current State |
|---|---|
| Container image | Root Dockerfile can build a jar image |
| Runtime Java | Java 21 JRE Alpine |
| App port | 8080 by default |
| External database | Env-driven for local/render; SQL Server profile exists |
| Redis | Configured and used by auth token state |
| Mail | Configured through Spring Mail |
| Cloudinary | Configured through custom properties |
| Health endpoints | No actuator dependency observed |
| Metrics endpoints | No actuator/Prometheus dependency observed |
| CI/CD files | No workflow/pipeline files observed |
| Docker Compose DB | No database service defined |
| Swagger UI | Dependency present and permitted by security config |

