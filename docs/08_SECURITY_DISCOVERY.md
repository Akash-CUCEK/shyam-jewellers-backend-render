# Shyam Jewellers Backend - Security Discovery

## Spring Security Configuration

Security is configured in `SecurityConfig`.

Current settings:

| Setting | Current Value |
|---|---|
| CSRF | Disabled |
| Session management | Stateless |
| Method security | Enabled with `@EnableMethodSecurity` |
| Password encoder | `BCryptPasswordEncoder` bean |
| Auth filter | `JwtAuthFilter` before `UsernamePasswordAuthenticationFilter` |
| Auth entry point | `JwtAuthEntryPoint` |
| CORS | Custom `CorsConfigurationSource` |

## Public Routes

Routes permitted by `SecurityConfig`:

```text
OPTIONS /**
/auth/api/v1/admin/initiateLogin
/auth/api/v1/admin/verifyLoginOtp
/auth/api/v1/admin/forgetPassword
/auth/api/v1/admin/verifyPasswordOtp
/api/v1/public/**
/api/v1/auth/**
/v3/api-docs/**
/swagger-ui/**
/swagger-ui.html
/swagger-resources/**
/webjars/**
/refreshToken
```

`JwtAuthFilter` contains its own public-route bypass list, including:

```text
/refreshToken
/api/v1/auth/login
/api/v1/auth/verify
/verifyLoginOtp
/auth/api/v1/admin/initiateLogin
/auth/api/v1/admin/verifyLoginOtp
/auth/api/v1/admin/forgetPassword
/auth/api/v1/admin/verifyPasswordOtp
/api/v1/public/
```

Observed route-name mismatch:

- Actual user login route is `/api/v1/auth/logIn`.
- Filter checks `/api/v1/auth/login`.
- SecurityConfig permits `/api/v1/auth/**`, so the endpoint is still public.

## Authorization

Method-level authorization is used on selected admin/order endpoints:

| Role Expression | Endpoints |
|---|---|
| `hasRole('SUPER_ADMIN')` | Add offer photo, register admin, get admin by email, get all admins, delete admin |
| `hasAnyRole('ADMIN','SUPER_ADMIN')` | Edit admin, change password, update/delete product, create/update order, admin order list |

Endpoints without `@PreAuthorize` but outside public route groups require any authenticated JWT due to `.anyRequest().authenticated()`.

## JWT Security

| Area | Current Behavior |
|---|---|
| Signature | HS256 |
| Secret source | `jwt.secret` property |
| Secret minimum | 32 bytes enforced at startup |
| Claims | subject and role |
| Access token expiry | 24 hours |
| Blacklist | Redis key per token |
| Validation failure | Filter can return raw `{"message":"Unauthorized"}` |

Access token blacklist is checked in `JwtUtil.validateToken`.

## Refresh Token Security

Refresh tokens are not stored as raw Redis values. `RefreshTokenService.store` hashes refresh tokens using the application password encoder before storing them.

Validation uses:

```text
passwordEncoder.matches(refreshToken, storedHash)
```

Refresh token key includes role, email, and deviceId.

## CORS

Current CORS configuration:

```text
Allowed origin patterns:
  http://localhost:*
  https://*.azurestaticapps.net

Allowed methods:
  GET, POST, PUT, DELETE, OPTIONS

Allowed headers:
  *

Allow credentials:
  true
```

## CSRF

CSRF is disabled:

```text
http.csrf(csrf -> csrf.disable())
```

The application uses stateless JWT authentication while also setting refresh tokens in cookies.

## Secret Management

Secret/config inputs present in profile files:

| Secret | Property |
|---|---|
| Database URL/user/password | `spring.datasource.*` / `DB_*` variables |
| Mail username/password | `spring.mail.*` / `MAIL_*` variables |
| Redis password | `spring.redis.password` / `REDIS_PASSWORD` |
| Cloudinary credentials | `cloudinary.cloud_name`, `cloudinary.api_key`, `cloudinary.api_secret` |
| JWT secret | `jwt.secret` / `JWT_SECRET` |

`.env.example` contains placeholder values.

`application-local.yml` includes default fallback values for local database username/password and JWT secret.

## Input Validation

Current validation mechanisms:

| Area | Current Behavior |
|---|---|
| Bean Validation | Controllers use `@Valid` on some DTO parameters |
| DTO annotations | Request DTO fields generally do not declare constraints such as `@NotBlank`, `@Email`, `@NotNull`, `@Positive` |
| Service validation | OTP expiry/value checks, duplicate category name check, home category count check, password checks, product empty-page checks |
| Excel validation | Custom validation for category upload rows |
| Enum parsing | Some flows default silently; some call `Enum.valueOf` directly and can throw |

## Exception Handling

Global exception handling:

| Exception | Response |
|---|---|
| `SYMException` | Uses exception status and `SYMErrorType` |
| Generic `Exception` | HTTP 500 with generic message |

Security exceptions may bypass `GlobalException`:

- `JwtAuthEntryPoint` writes raw unauthorized JSON.
- `JwtAuthFilter` catches exceptions and writes raw unauthorized JSON.

## Security Logging Observed

Security-relevant logs include:

- Admin login attempt processing.
- JWT valid user/role logs.
- Token blacklist operations.
- Login errors with stack trace.

Sensitive logging observed:

- `JwtAuthFilter` logs the full `Authorization` header.
- `TokenBlacklistService` logs token value in debug path.

## Current Access-Control Surface

| Route Area | Current Protection |
|---|---|
| Public product/category/offer APIs | Public |
| User auth APIs | Public |
| Refresh token API | Public |
| Admin login/password reset APIs | Public |
| Admin management APIs | Role-restricted for most high-risk operations |
| Some admin product/category APIs | JWT required but no method-level role annotation |
| Order create/update/list | Role-restricted |
| Order get/invoice | JWT required but no method-level role annotation |
| Home service APIs | JWT required, no method-level role annotation |
| Repair service APIs | JWT required, no method-level role annotation |

