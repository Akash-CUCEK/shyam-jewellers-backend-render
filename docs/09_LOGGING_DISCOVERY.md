# Shyam Jewellers Backend - Logging Discovery

## Logging Framework

The project uses SLF4J through Lombok `@Slf4j` and explicit `LoggerFactory` loggers.

No custom `logback-spring.xml`, structured logging configuration, request correlation filter, or audit logging subsystem was observed.

`ApplicationConstants.REQUEST_ID` exists as `x-request-id`, but no active request ID filter usage was observed.

## Existing Logs by Layer

### Controllers

Controllers log receipt of most requests:

| Controller | Examples |
|---|---|
| `AdminController` | login, password reset, logout, admin edit/register/list/delete, product/category requests |
| `UserController` | sign-in and verify requests |
| `ProductController` | material type, price, gender requests |
| `CategoryController` | category list and category by ID |
| `PublicController` | offer photo read |
| `OrderController` | create/update/get/invoice requests |
| `HomeServiceController` | service request CRUD/search/list |
| `RepairRequestController` | repair request CRUD/search/list |
| `RefreshTokenController` | refresh token request |

### Services

Service logs include:

- Admin login steps and token generation.
- Forget password and OTP verification processing.
- Admin logout and token blacklist operation.
- Category add/update/delete/upload processing.
- Product add/update/delete/filter operations.
- Order create/update/list/invoice operations.
- Home/repair service request operations.

### DAOs and Repositories

DAO logs are mostly debug or error logs around persistence:

- Finding/saving/deleting admins.
- Saving users.
- Saving/finding categories.
- Saving/finding/deleting service requests.
- Saving/finding orders.

Repositories do not log directly.

### Integrations

| Integration | Logs |
|---|---|
| Email | Sending mail, mail sent successfully, failed email send |
| Cloudinary | Product service logs before upload; Cloudinary service throws runtime exception on failure |
| Redis blacklist | Blacklist set and blacklist lookup debug logs |

## Authentication Logs

Observed authentication logs:

- Admin login processing.
- Fetched admin email.
- Admin role.
- Access/refresh token generated.
- Refresh token stored.
- User OTP verification processing.
- JWT filter logs request URI and Authorization header.
- JWT filter logs valid username and role.
- JWT filter logs when authentication is set.
- JWT filter logs invalid/expired JWT.
- Token blacklist logs blacklist TTL.

## Security Logs

Security-specific logs exist but are not separated into a dedicated audit/security event model.

Sensitive logging observed:

```text
JwtAuthFilter logs Authorization header
JwtUtil logs blacklisted token value
TokenBlacklistService debug logs token value
```

These are current implementation facts, not remediation instructions.

## Business Logs

Business event logs are mostly request lifecycle logs, for example:

- Product add/update/delete/list/filter.
- Category add/update/delete/upload.
- Order create/update/get/list/invoice.
- Service request create/edit/delete/search/list.

No persistent business audit log table was observed.

## Exception Logs

Exception logging exists mainly in service/DAO/integration layers:

| Area | Behavior |
|---|---|
| Admin login | Logs full stack trace and rethrows `SYMException` |
| DAO save/delete/fetch failures | Logs error and throws `SYMException` |
| Invoice generation | Logs error and throws runtime exception |
| Email send failure | Logs error and throws runtime exception |
| JWT filter failure | Logs error and writes unauthorized response |

`GlobalException` builds error responses but does not log exceptions directly.

## Missing Logs Observed

The following log categories were not observed as dedicated structured logs:

- Request correlation ID.
- Request/response access logs.
- Admin action audit logs.
- User login audit logs.
- OTP attempt logs with counters.
- Refresh token rotation audit.
- Authorization denial audit.
- Product/category/order mutation audit.
- Service request lifecycle audit.
- External integration latency/failure metrics.

## Current Logging Risks

| Risk Area | Current Observation |
|---|---|
| Sensitive token exposure | Authorization header and token strings can be logged |
| Traceability | No active correlation ID propagation observed |
| Auditability | No persistent audit log model observed |
| Operational diagnosis | Logs are free-form strings, not structured event records |
| Consistency | Some classes use Lombok `@Slf4j`; some use explicit `LoggerFactory`; one `System.out.println()` exists in application main |

