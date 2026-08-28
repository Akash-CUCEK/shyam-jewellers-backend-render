# Shyam Jewellers Backend - Package Structure Discovery

## Source Tree

```text
src/main/java/com/shyam
  ShyamApplication.java
  common
    constants
    controller
    dto
    email
    exception
      domain
      dto
    jwt
    redis
      service
    service
    util
  config
  constants
  controller
  dao
  dto
    request
    response
  entity
  mapper
  repository
  service
    Imp
  validation
```

## Package Inventory

| Package | Purpose |
|---|---|
| `com.shyam` | Spring Boot application bootstrap |
| `com.shyam.common.constants` | Shared enums for roles, order/payment statuses, service types |
| `com.shyam.common.controller` | Shared refresh-token controller |
| `com.shyam.common.dto` | Refresh-token request/response/details DTOs |
| `com.shyam.common.email` | SMTP email sending service |
| `com.shyam.common.exception.domain` | Global exception advice, domain exception, error types |
| `com.shyam.common.exception.dto` | Common response envelope and error DTOs |
| `com.shyam.common.jwt` | JWT utility, filter, entry point, user details services |
| `com.shyam.common.redis` | Redis connection/template configuration |
| `com.shyam.common.redis.service` | Access-token blacklist service |
| `com.shyam.common.service` | Refresh-token Redis service |
| `com.shyam.common.util` | Message source lookup and enum parser utility |
| `com.shyam.config` | Spring Security filter chain, password encoder, CORS config |
| `com.shyam.constants` | Message codes, error codes, request ID constant |
| `com.shyam.controller` | Business REST controllers |
| `com.shyam.dao` | Repository wrapper and exception translation layer |
| `com.shyam.dto.request` | Request DTOs for controllers |
| `com.shyam.dto.response` | Response DTOs for controllers |
| `com.shyam.entity` | JPA entity classes |
| `com.shyam.mapper` | DTO/entity mapping and selected workflow side effects |
| `com.shyam.repository` | Spring Data JPA repositories |
| `com.shyam.service` | Service interfaces |
| `com.shyam.service.Imp` | Service implementations and Cloudinary service |
| `com.shyam.validation` | Category Excel validation helpers |

## Controller Classes

| Class | Base Route | Responsibility |
|---|---|---|
| `AdminController` | `/auth/api/v1/admin` | Admin auth, admin management, offer updates, admin product/category actions |
| `UserController` | `/api/v1/auth` | User OTP login and verification |
| `ProductController` | `/api/v1/public` | Public product browsing/filtering |
| `CategoryController` | `/api/v1/public` | Public category APIs |
| `PublicController` | `/api/v1/public` | Public offer-photo API |
| `OrderController` | No class-level mapping | Order creation/update/read/list/invoice APIs |
| `HomeServiceController` | `/api/homeService` | Home service request APIs |
| `RepairRequestController` | `/api/common` | Repair request APIs |
| `RefreshTokenController` | No class-level mapping | Refresh token rotation API |

## Service Classes

| Interface | Implementation | Responsibility |
|---|---|---|
| `AdminService` | `AdminServiceImp` | Admin login, logout, password reset, admin CRUD, offer photo |
| `UserService` | `UserServiceImp` | User OTP login and verification |
| `ProductService` | `ProductServiceImp` | Product create/update/delete/read/filter, Cloudinary upload |
| `CategoryService` | `CategoryServiceImp` | Category create/update/delete/read, Excel upload |
| `OrderService` | `OrderServiceImpl` | Order create/update/read/list/count/invoice |
| `HomeService` | `HomeServiceImp` | Home service request workflow |
| `RepairRequestService` | `RepairRequestServiceImp` | Repair service request workflow |
| none | `CloudinaryService` | Cloudinary image upload |
| none | `RefreshTokenService` | Redis refresh-token store/validate/delete |
| none | `TokenBlacklistService` | Redis access-token blacklist |
| none | `EmailService` | SMTP email send |

## Repository Classes

| Repository | Entity | Custom Methods / Queries |
|---|---|---|
| `AdminRepository` | `AdminUsers` | `findByEmail`, `findByRole` |
| `UsersRepo` | `Users` | `findByEmail` |
| `CategoryRepository` | `Category` | `findByName`, `existsByName`, `canEnableShowOnHome` |
| `ProductRepository` | `Products` | gender query, category query, filter query, material query, price queries |
| `OfferPhotoRepository` | `OfferPhoto` | `findTopByOrderByCreatedAtDesc` |
| `OrderRepository` | `Order` | `findAllByOrderDate`, monthly count query |
| `HomeServiceRepository` | `ServiceHome` | descending list, keyword search query |
| `RepairRequestRepository` | `RepairService` | descending list, keyword search query |

## Resource Structure

```text
src/main/resources
  application.properties
  application.yml
  application-local.yml
  application-prod.yml
  application-render.yml
  messages.properties
  migration
    V1__create_admin_users_table.sql
    V2__create_users_table.sql
    V3__create_category_table.sql
    V4__create_products_table.sql
    V5__create_offer_photo_table.sql
    V6__create_order_and_order_product_ids_table.sql
    V7__create_service_home_table.sql
    V8__create_repair_service_table.sql
```

## Test Structure

```text
src/test/java/com/shyam/ShyamApplicationTests.java
src/test/resources/application-test.yml
```

Current test coverage contains one context-load test with the `test` profile.

## Build and Deployment Files

| File | Purpose |
|---|---|
| `pom.xml` | Maven project definition and dependencies |
| `mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties` | Maven wrapper |
| `Dockerfile` | Multi-stage Maven build and Java 21 runtime image |
| `.mvn/docker-compose.yml` | Compose file containing Redis and backend service definitions |
| `.env.example` | Environment variable placeholder examples |
| `.gitignore`, `.gitattributes` | Git metadata |

