# Shyam Jewellers Backend - Module Analysis Discovery

## Rating Scale

Enterprise rating is a discovery score for the current implementation only.

| Rating | Meaning |
|---|---|
| 1-3 | Early or incomplete implementation |
| 4-6 | Functional but with notable gaps or coupling |
| 7-8 | Mostly coherent implementation with limited gaps |
| 9-10 | Enterprise-ready implementation observed in code |

## Module Summary

| Module | Main Classes | Rating |
|---|---|---|
| Authentication and Tokens | `SecurityConfig`, `JwtAuthFilter`, `JwtUtil`, `RefreshTokenController`, `RefreshTokenService`, `TokenBlacklistService` | 6/10 |
| Admin | `AdminController`, `AdminServiceImp`, `AdminMapper`, `AdminDAO`, `AdminUsers`, `AdminRepository` | 6/10 |
| User | `UserController`, `UserServiceImp`, `UserMapper`, `UserDAO`, `Users`, `UsersRepo` | 5/10 |
| OTP Login | `UserMapper`, `UserServiceImp`, `AdminServiceImp` | 5/10 |
| Category | `CategoryController`, `CategoryServiceImp`, `CategoryMapper`, `CategoryDAO`, `Category`, `CategoryRepository` | 5/10 |
| Product | `ProductController`, `ProductServiceImp`, `ProductMapper`, `ProductDAO`, `Products`, `ProductRepository` | 5/10 |
| Orders | `OrderController`, `OrderServiceImpl`, `OrderMapper`, `OrderDAO`, `Order`, `OrderItem`, `OrderRepository` | 6/10 |
| Invoice | `OrderServiceImpl`, `GetOrderInvoiceResponse` | 4/10 |
| Offers | `PublicController`, `AdminController`, `AdminMapper`, `OfferPhoto`, `OfferPhotoRepository` | 5/10 |
| Home Service | `HomeServiceController`, `HomeServiceImp`, `HomeServiceMapper`, `HomeServiceDAO`, `ServiceHome`, `HomeServiceRepository` | 5/10 |
| Repair Service | `RepairRequestController`, `RepairRequestServiceImp`, `RepairRequestMapper`, `RepairRequestDAO`, `RepairService`, `RepairRequestRepository` | 5/10 |
| Email | `EmailService`, user/admin mapper/service flows | 5/10 |
| Cloudinary | `CloudinaryService`, `ProductServiceImp` | 5/10 |
| Excel Category Upload | `CategoryServiceImp`, `CategoryExcelValidation`, `RowValidationError` | 6/10 |

## Authentication and Tokens

| Item | Discovery |
|---|---|
| Purpose | Authenticate admin and user clients, issue JWT access tokens, manage refresh tokens and logout blacklist. |
| Current Flow | JWT access tokens are generated with subject=email and `role` claim. Refresh tokens are UUID strings stored in Redis as BCrypt hashes under `refresh:{role}:{email}:{deviceId}` for one day. Refresh rotates by deleting the old Redis key and storing a new token. Admin logout blacklists the access token until expiry. |
| Existing APIs | `POST /auth/api/v1/admin/logIn`, `POST /api/v1/auth/logIn`, `POST /api/v1/auth/verify`, `POST /refreshToken`, `POST /auth/api/v1/admin/logout`. |
| Database Tables | `admin_users`, `users`; Redis keys for refresh tokens and blacklisted access tokens. |
| Strengths | Stateless Spring Security setup, BCrypt password hashing for admins, Redis-backed refresh token validation, refresh-token hashing instead of storing raw refresh tokens, access-token blacklist support. |
| Weaknesses | JWT filter logs Authorization header value; public routes are duplicated in `SecurityConfig` and `JwtAuthFilter`; refresh endpoint comment says cookie/mobile support but actual code requires email/role/deviceId in request body; user logout is commented out; normal user authorities are `USERS` instead of `ROLE_USER`. |
| Technical Debt | Static token generation methods depend on a static initialized secret key; token claims are minimal; cookie security settings differ by flow; token and cookie behavior is partly in controllers and partly in services. |
| Missing Features Observed | Rate limiting, failed-login lockout, token `jti`, issuer/audience claims, device/session inventory, refresh-token reuse detection, user logout endpoint. |
| Enterprise Rating | 6/10 |

## Admin

| Item | Discovery |
|---|---|
| Purpose | Admin login, logout, password reset, profile edit, admin registration/list/read/delete, offer photo administration, admin product/category operations. |
| Current Flow | `AdminController` delegates to `AdminServiceImp`. Admin lookup uses `AdminDAO` and `AdminRepository`. Password login uses BCrypt. Admin registration saves an `AdminUsers` row with role `ADMIN`. Edit/delete and offer updates execute persistence work inside `AdminMapper`. |
| Existing APIs | `/auth/api/v1/admin/logIn`, `/forgetPassword`, `/verifyPasswordOtp`, `/logout`, `/editAdmin`, `/changePassword`, `/registerAdmin`, `/getAdminByEmail`, `/getAllAdmin`, `/deleteAdmin`, `/addOfferPhoto`, plus admin product/category endpoints. |
| Database Tables | `admin_users`, `offer_photo`, plus product/category tables for admin catalog operations. |
| Strengths | Role enum supports `ADMIN` and `SUPER_ADMIN`; selected endpoints use `@PreAuthorize`; password hashing is present; admin registration persists current entity; admin list filters by `Role.ADMIN`. |
| Weaknesses | Some admin product/category endpoints have no method-level role annotation and rely only on authenticated default; admin mapper performs database writes; password reset same-password check appears reversed in `forgetVerifyOtp`; admin login catches all exceptions and rethrows a generic internal-server exception, including expected auth failures. |
| Technical Debt | `AdminController` mixes admin account APIs with product/category APIs; mapper depends on DAO, password encoder, email, and token blacklist; role assignment for new admins is hardcoded to `ADMIN`. |
| Missing Features Observed | Admin account audit, lockout, profile ownership checks, admin status/active flag, password policy metadata, first-login password-change state. |
| Enterprise Rating | 6/10 |

## User

| Item | Discovery |
|---|---|
| Purpose | OTP-only user login and JWT/refresh-token issuance for normal users. |
| Current Flow | `POST /api/v1/auth/logIn` calls `UserMapper.logInMapper`, which creates or updates a `Users` row with OTP and OTP timestamp, then emails the OTP. `POST /api/v1/auth/verify` validates the OTP, generates tokens, stores refresh token in Redis, and returns tokens plus cookie. |
| Existing APIs | `POST /api/v1/auth/logIn`, `POST /api/v1/auth/verify`. |
| Database Tables | `users`; Redis refresh token key for verified sessions. |
| Strengths | Login auto-creates user record, OTP expiry is checked for 5 minutes, refresh tokens are stored as hashes in Redis. |
| Weaknesses | Login side effects live in mapper; user has only email/OTP fields; no active profile fields; user logout endpoint is commented out; user authorities do not follow Spring `ROLE_` convention. |
| Technical Debt | `logInRequestDTO` class name starts lowercase; `EmailService` is injected in service but email send occurs in mapper; token blacklist service is injected but unused in active user service code. |
| Missing Features Observed | User profile, resend cooldown, OTP retry counter, user logout, account status, device/session management. |
| Enterprise Rating | 5/10 |

## OTP Login

| Item | Discovery |
|---|---|
| Purpose | Support email OTP login for users and password reset OTP for admins. |
| Current Flow | User login OTP is generated in `UserMapper` and emailed. Admin password reset OTP is generated in `AdminServiceImp`, stored on `AdminUsers`, emailed, and verified in `forgetVerifyOtp`. Both flows use 6-digit random numbers and a 5-minute validity window. |
| Existing APIs | `POST /api/v1/auth/logIn`, `POST /api/v1/auth/verify`, `POST /auth/api/v1/admin/forgetPassword`, `POST /auth/api/v1/admin/verifyPasswordOtp`. |
| Database Tables | `users.otp`, `users.otp_generated_time`, `admin_users.otp`, `admin_users.otp_generated_time`. |
| Strengths | OTP timestamps are persisted; expired and invalid OTP cases throw `SYMException`; email delivery is centralized in `EmailService`. |
| Weaknesses | OTP values are stored as plain text; no retry count or resend cooldown; random generation uses `java.util.Random`; email body strings are embedded in Java code. |
| Technical Debt | OTP logic is split between service and mapper classes; user OTP and admin OTP implementations are duplicated rather than sharing a component. |
| Missing Features Observed | OTP hashing, attempt limits, resend throttling, IP/device/email rate limiting, OTP purpose separation. |
| Enterprise Rating | 5/10 |

## Category

| Item | Discovery |
|---|---|
| Purpose | Manage product categories and expose public category reads. |
| Current Flow | Admin category endpoints call `CategoryServiceImp`, which checks duplicate names and home-page display limit, maps DTOs to `Category`, and saves through `CategoryDAO`. Excel upload validates rows and returns an error workbook when invalid rows exist. Public `getCategory` returns a user-facing DTO. Public `getAllCategory` currently returns `null`. |
| Existing APIs | Admin: `POST /auth/api/v1/admin/getAllCategory`, `POST /getCategory`, `POST /addCategory`, `PUT /updateCategory`, `DELETE /deleteCategory`, `POST /uploadExcel`. Public: `POST /api/v1/public/getAllCategory`, `POST /api/v1/public/getCategory`. |
| Database Tables | `category`; `products` references category via JPA `category_id`. |
| Strengths | Duplicate category-name check exists; home-page category limit query exists; admin list is paginated; Excel validation produces structured error workbook. |
| Weaknesses | `getAllCategoriesUser` returns `null`; update finds category by name rather than ID; delete physically deletes by ID; DTO validation annotations are absent. |
| Technical Debt | `CategoryMapper` has static and instance methods and depends on `CategoryDAO`; entity/migration constraints differ for unique category name. |
| Missing Features Observed | Soft delete/status-aware delete flow, active-only public list, category hierarchy, image upload provider integration for categories. |
| Enterprise Rating | 5/10 |

## Product

| Item | Discovery |
|---|---|
| Purpose | Product catalog management and public product browsing/filtering. |
| Current Flow | Admin add-product parses multipart `data` JSON and image file, uploads image to Cloudinary, looks up `Category` by name, maps request to `Products`, and saves. Public read/filter APIs call repository queries through `ProductDAO`. Final price is computed in `ProductMapper` response mapping. |
| Existing APIs | Admin: `POST /auth/api/v1/admin/addProduct`, `PUT /updateProduct`, `DELETE /deleteProduct`, `POST /getAllProduct`, `GET /getProductById/{productId}`. Public: `GET /api/v1/public/getAllProducts`, `GET /getProductById/{productId}`, `GET /category/{category}`, `GET /materialType/{materialType}`, `GET /price/under`, `GET /price/above`, `POST /getProductsByGender`, `POST /getProductsByFilter`. |
| Database Tables | `products`, `category`. |
| Strengths | Public list/filter APIs are pageable; category is modeled as a JPA relationship; SKU is generated on persist; Cloudinary secure URL is stored. |
| Weaknesses | `ProductAddRequestDTO` has no product name field and mapper does not set product name; update/delete locate products by name; some admin product endpoints rely only on authenticated default; `GetAllProductsResponseDTO` exposes `Category` entity directly. |
| Technical Debt | Service directly depends on `CloudinaryService`; mixed `jakarta.transaction.Transactional` and Spring `@Transactional`; migration table shape differs from entity model. |
| Missing Features Observed | Product code/name creation in add DTO, stock transaction history, product image validation metadata, rating source, active-only filtering consistency. |
| Enterprise Rating | 5/10 |

## Orders

| Item | Discovery |
|---|---|
| Purpose | Admin order creation, update, read, list, monthly count, and invoice generation. |
| Current Flow | `OrderMapper` maps order DTOs into `Order` and `OrderItem` entities. Create saves the aggregate through `OrderDAO`. Update fetches order, updates scalar fields, replaces items when provided, and saves. List is paginated directly from `OrderRepository.findAll`. |
| Existing APIs | `POST /createOrder`, `POST /updateOrder`, `POST /getOrderById`, `GET /admin/orders`, `POST /getOrderInvoiceById`. `getTotalOrderMonth` exists in service but active controller method is commented out. |
| Database Tables | `orders`, `order_items` by JPA; migration creates `orders` and `order_product_ids`. |
| Strengths | Order items are modeled as child entities with cascade persist; payment status is calculated from total/due amount; list endpoint is paginated. |
| Weaknesses | Product references inside `OrderItem` are scalar product IDs, not enforced JPA product relationships; `getOrderById` and invoice endpoints rely on authenticated default but have no method-level role annotation; update replaces items with quantity `1` and price `0` when products list is supplied. |
| Technical Debt | Migration schema and JPA entity model diverge; PDF generation is embedded in order service; some enum parsing silently defaults to `CREATED`, `CASH`, or `ADMIN`. |
| Missing Features Observed | Order status history, inventory deduction, payment entity, customer/user linkage, delivery tracking, invoice template model. |
| Enterprise Rating | 6/10 |

## Invoice

| Item | Discovery |
|---|---|
| Purpose | Generate an invoice PDF for an order. |
| Current Flow | `POST /getOrderInvoiceById` fetches an order by ID and calls `OrderServiceImpl.generateInvoicePdf`, which writes plain paragraphs into an iText PDF byte array and returns bytes plus file name in JSON response DTO. |
| Existing APIs | `POST /getOrderInvoiceById`. |
| Database Tables | Reads `orders`; includes scalar order fields. |
| Strengths | Functional PDF byte generation exists; invoice file name is deterministic from order ID. |
| Weaknesses | Response returns PDF bytes inside JSON envelope rather than a binary `application/pdf` response; invoice does not include line-item details; formatting is minimal. |
| Technical Debt | PDF creation is private logic in `OrderServiceImpl`, coupled to order service and iText 5 API. |
| Missing Features Observed | Invoice numbering, tax fields, itemized product table, branding layout, persistent invoice record. |
| Enterprise Rating | 4/10 |

## Offers

| Item | Discovery |
|---|---|
| Purpose | Manage and retrieve up to five offer photo URLs. |
| Current Flow | Admin `addOfferPhoto` updates the latest `OfferPhoto` row or creates a new one, placing the provided URL in position 1-5. Public `getOfferPhoto` returns URLs from the latest offer photo row or nulls when no row exists. |
| Existing APIs | `POST /auth/api/v1/admin/addOfferPhoto`, `POST /api/v1/public/getOfferPhoto`. |
| Database Tables | `offer_photo`. |
| Strengths | Simple public read API; latest-row lookup is explicit; position-based update protects fixed five-photo response shape. |
| Weaknesses | Offer photo update stores URLs only and does not upload files; invalid position throws `IllegalArgumentException` handled as generic 500; every update resets `createdAt`. |
| Technical Debt | Offer persistence sits inside `AdminMapper`; no explicit offer service or validation DTO constraints. |
| Missing Features Observed | Offer activation date, expiry date, ordering beyond fixed positions, click target, alt text, image upload. |
| Enterprise Rating | 5/10 |

## Home Service

| Item | Discovery |
|---|---|
| Purpose | Capture and administer home service requests. |
| Current Flow | Controller calls `HomeServiceImp`; create maps request to `ServiceHome` with `REQUESTED` status and saves. Edit updates fields and status. Search converts numeric keyword to service ID, otherwise name, then calls JPQL search. `getAllUserServiceRequests` currently returns `null`. |
| Existing APIs | `POST /api/homeService/getAllServiceRequests`, `POST /getHomeServiceRequestById`, `POST /createHomeServiceRequest`, `PUT /editHomeServiceRequest`, `POST /searchHomeServiceRequest`, `DELETE /deleteHomeServiceRequest`, `POST /getAllUserServiceRequests`. |
| Database Tables | `service_home`. |
| Strengths | Status and service type are enums; create/edit/delete/read flows are implemented; list is ordered descending by created time. |
| Weaknesses | No method-level roles on endpoints; list endpoints are not paginated; `getAllUserServiceRequests` returns `null`; search JPQL uses `OR` conditions that can return all rows when one parameter is null. |
| Technical Debt | Static mapper methods; no request DTO validation; physical delete by ID. |
| Missing Features Observed | User-specific filtering, pagination, appointment slot, assignment to admin/staff, status history. |
| Enterprise Rating | 5/10 |

## Repair Service

| Item | Discovery |
|---|---|
| Purpose | Capture and administer repair service requests. |
| Current Flow | Controller calls `RepairRequestServiceImp`; create maps request to `RepairService` with `REQUESTED` status and saves. Edit updates fields and status. Search uses keyword-to-ID/name split and repository JPQL. |
| Existing APIs | `POST /api/common/getAllRepairRequests`, `POST /searchRepairRequest`, `POST /getRepairRequestById`, `POST /createRepairRequest`, `PUT /editRepairRequest`, `DELETE /deleteRepairRequest`. |
| Database Tables | `repair_service`. |
| Strengths | CRUD-style request lifecycle is present; status enum is used; list is ordered descending by created time. |
| Weaknesses | No method-level roles on endpoints; list is not paginated; search JPQL can return all rows due to `OR` with null parameters. |
| Technical Debt | Static mapper methods; physical delete by ID; duplicated service-request pattern with home service. |
| Missing Features Observed | User-specific filtering, item/service details, repair estimate, assignment, status history, pagination. |
| Enterprise Rating | 5/10 |

## Email

| Item | Discovery |
|---|---|
| Purpose | Send transactional emails for OTP, admin registration, and password reset. |
| Current Flow | `EmailService.sendEmail` creates a `MimeMessage`, sets recipient, subject, and HTML/text body, then sends through `JavaMailSender`. Calling flows build email subject/body strings inline. |
| Existing APIs | No direct email API; invoked by auth/admin flows. |
| Database Tables | None. |
| Strengths | Email sending is centralized in a single service; Spring Mail configuration is profile-driven. |
| Weaknesses | Email failure throws runtime exception and can fail the caller; email templates are Java string concatenations; body is passed as HTML enabled even though content is mostly plain text. |
| Technical Debt | Email send is called synchronously inside request flows; no retry record or notification abstraction. |
| Missing Features Observed | Template files, retry queue, delivery status, provider abstraction, audit trail. |
| Enterprise Rating | 5/10 |

## Cloudinary

| Item | Discovery |
|---|---|
| Purpose | Upload product images and return secure Cloudinary URLs. |
| Current Flow | `CloudinaryService` initializes a Cloudinary client from profile properties. `upload` validates `contentType` starts with `image/`, uploads bytes to folder `shyam-products`, and returns `secure_url`. |
| Existing APIs | Invoked by `POST /auth/api/v1/admin/addProduct`. |
| Database Tables | Stores URL in `products.image_url`. |
| Strengths | Image content type check exists; secure URL is used; configuration is property-driven. |
| Weaknesses | Null content type is not guarded; upload reads full file bytes into memory; Cloudinary-specific service is called directly by product service. |
| Technical Debt | No file-size, dimension, or extension validation in code; no media metadata table. |
| Missing Features Observed | Multiple images per product, image replacement/delete, upload audit, fallback image handling. |
| Enterprise Rating | 5/10 |

## Excel Category Upload

| Item | Discovery |
|---|---|
| Purpose | Bulk-create categories from Excel with row-level validation feedback. |
| Current Flow | `POST /auth/api/v1/admin/uploadExcel` reads the first workbook sheet. `CategoryExcelValidation` checks name blank/special characters/duplicates and status `active` or `inactive`. Invalid rows produce an `ErrorReport.xlsx`; valid rows are saved as categories. |
| Existing APIs | `POST /auth/api/v1/admin/uploadExcel`. |
| Database Tables | `category`. |
| Strengths | Row-level validation is explicit; invalid input returns a workbook artifact with reasons; valid flow maps status text to boolean. |
| Weaknesses | Endpoint has no method-level role annotation; duplicate check lowercases the name before lookup while normal category creation uses exact name; upload saves row-by-row. |
| Technical Debt | Excel layout is hardcoded to name/status columns; no batch import summary DTO for successful rows. |
| Missing Features Observed | Import job history, partial success report, uploaded-file type/size validation, duplicate detection within the same workbook. |
| Enterprise Rating | 6/10 |

