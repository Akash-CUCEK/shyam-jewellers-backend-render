# Shyam Jewellers Backend - System Architecture Discovery

## Architecture Style

The application is currently a single Spring Boot monolith. It is organized primarily by technical layer rather than by business module.

```text
HTTP Request
  -> Controller
    -> Service interface
      -> Service implementation
        -> Mapper and/or DAO
          -> Repository
            -> JPA Entity
              -> Database
```

Cross-cutting concerns are grouped in `com.shyam.common` and `com.shyam.config`.

## Runtime Components

| Component | Current Classes / Files |
|---|---|
| Application bootstrap | `ShyamApplication` |
| REST controllers | `AdminController`, `UserController`, `ProductController`, `CategoryController`, `PublicController`, `OrderController`, `HomeServiceController`, `RepairRequestController`, `RefreshTokenController` |
| Services | Interfaces in `com.shyam.service`, implementations in `com.shyam.service.Imp`, `RefreshTokenService`, `TokenBlacklistService`, `EmailService`, `CloudinaryService` |
| Persistence wrappers | DAO classes in `com.shyam.dao` |
| Persistence adapters | Spring Data JPA repositories in `com.shyam.repository` |
| Domain persistence model | JPA entities in `com.shyam.entity` |
| DTOs | Request and response DTOs in `com.shyam.dto` and `com.shyam.common.dto` |
| Security | `SecurityConfig`, `JwtAuthFilter`, `JwtUtil`, user details services, `JwtAuthEntryPoint` |
| Redis | `RedisConfig`, `RefreshTokenService`, `TokenBlacklistService` |
| Validation | `CategoryExcelValidation`, limited `@Valid` usage in controllers |
| Exception handling | `GlobalException`, `SYMException`, `SYMErrorType`, error DTOs |
| External integrations | SMTP via `EmailService`, Cloudinary via `CloudinaryService` |
| File generation | iText PDF invoice generation in `OrderServiceImpl`, Excel validation/error workbook in `CategoryServiceImp` |

## Layer Responsibilities Observed

| Layer | Current Responsibility |
|---|---|
| Controller | HTTP route mapping, request body/header/cookie extraction, response envelope creation, selected cookie creation/deletion |
| Service | Main business workflow, transaction boundaries, authentication decisions, token generation, orchestration of mapper/DAO/repository/integration calls |
| Mapper | DTO/entity conversion; also contains side effects in some modules, including admin edit/delete/offer persistence and user OTP/email flow |
| DAO | Repository wrapper, exception translation, simple query preparation |
| Repository | Spring Data JPA CRUD and JPQL queries |
| Entity | JPA table mappings and limited lifecycle hooks |
| Common | JWT, Redis, email, constants, response/error envelope, message lookup |

## Authentication Architecture

Current admin login flow:

```text
POST /auth/api/v1/admin/logIn
  -> AdminServiceImp.logIn
    -> AdminDAO.findUserByEmail
      -> AdminRepository.findByEmail
    -> BCrypt password match
    -> JwtUtil.generateAccessToken(email, role)
    -> JwtUtil.generateRefreshToken()
    -> RefreshTokenService.store(email, role, refreshToken, deviceId)
      -> Redis key refresh:{role}:{email}:{deviceId}
    -> HTTP-only refreshToken cookie + token response body
```

Current user login flow:

```text
POST /api/v1/auth/logIn
  -> UserServiceImp.logIn
    -> UserMapper.logInMapper
      -> UsersRepo.findByEmail
      -> create/update Users.otp and otpGeneratedTime
      -> EmailService.sendEmail

POST /api/v1/auth/verify
  -> UserServiceImp.verify
    -> UserDAO.findUser
    -> 5-minute OTP check
    -> JwtUtil.generateAccessToken(email, USER)
    -> JwtUtil.generateRefreshToken()
    -> RefreshTokenService.store(email, USER, refreshToken, deviceId)
    -> HTTP-only refreshToken cookie + token response body
```

Current refresh flow:

```text
POST /refreshToken
  -> RefreshTokenController.refresh
    -> requires refreshToken, email, role, deviceId from request body
    -> RefreshTokenService.validate
      -> Redis hash lookup and BCrypt match
    -> RefreshTokenService.delete
    -> JwtUtil.generateAccessToken
    -> JwtUtil.generateRefreshToken
    -> RefreshTokenService.store
    -> new refreshToken cookie + response body
```

Current logout flow:

```text
POST /auth/api/v1/admin/logout
  -> AdminServiceImp.logout
    -> JwtUtil.getExpiry
    -> TokenBlacklistService.blacklistToken(accessToken, remainingSeconds)
    -> RefreshTokenService.delete(username, role, deviceId) when refreshToken cookie is present
    -> delete refreshToken cookie
```

User logout is present only as commented-out code in `UserController` and `UserServiceImp`.

## Authorization Architecture

Authorization is split across:

- `SecurityConfig` route rules
- `JwtAuthFilter` public-route bypass logic
- `@PreAuthorize` annotations on selected controller methods

Security defaults:

- CSRF disabled
- Stateless session management
- JWT filter before `UsernamePasswordAuthenticationFilter`
- Method security enabled with `@EnableMethodSecurity`
- All non-permitted routes require authentication

Permitted route groups in `SecurityConfig`:

```text
OPTIONS /**
/auth/api/v1/admin/logIn
/auth/api/v1/admin/verifyOtp
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

## Data Architecture

JPA entities model these tables:

| Entity | Table |
|---|---|
| `AdminUsers` | `admin_users` |
| `Users` | `users` |
| `Category` | `category` |
| `Products` | `products` |
| `OfferPhoto` | `offer_photo` |
| `Order` | `orders` |
| `OrderItem` | `order_items` |
| `ServiceHome` | `service_home` |
| `RepairService` | `repair_service` |

The most important relationship currently modeled is:

```text
Category 1 -> N Products
Order 1 -> N OrderItem
```

`OrderItem.productId` is a scalar `Long`, not a JPA relationship to `Products`.

## External Integrations

| Integration | Current Use |
|---|---|
| Redis | Refresh-token hash storage and access-token blacklist |
| SMTP | User OTP, admin password reset OTP, admin registration email |
| Cloudinary | Product image upload in admin add-product flow |
| iText | Invoice PDF byte generation |
| Apache POI | Category Excel validation and error workbook creation |

## Transaction Boundaries

Most service methods are annotated with transaction boundaries:

- Write flows generally use `@Transactional` or `@jakarta.transaction.Transactional`.
- Read flows generally use `@Transactional(readOnly = true)`.
- Controllers, DAOs, repositories, and mappers do not own declared transaction boundaries.

Observed exception: side effects inside mappers still execute within service transactions when invoked from transactional service methods.

## Response Architecture

Most controllers wrap successful results in:

```text
new BaseResponseDTO<>(response, null)
```

Error responses contain:

```text
messages: List<ErrorMessagesDTO>
timestamp: LocalDateTime
errorType: SYMErrorType
```

`JwtAuthEntryPoint` and `JwtAuthFilter` can also write raw JSON strings directly for unauthorized requests.

