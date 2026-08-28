# Shyam Jewellers Backend - API Discovery

## Response Envelope

Most APIs return:

```text
BaseResponseDTO<T>
  response: T
  errors: ErrorResponseDTO
```

Some endpoints return `ResponseEntity<BaseResponseDTO<T>>` to include cookies, headers, or explicit HTTP status. Category Excel upload returns `ResponseEntity<?>` and may return an Excel file byte array on validation failure.

## Authentication Legend

| Value | Meaning |
|---|---|
| Public | Permitted by `SecurityConfig` without JWT |
| JWT | Requires authenticated JWT by default security rule |
| JWT + Role | Requires JWT and `@PreAuthorize` role expression |

## AdminController - `/auth/api/v1/admin`

| Endpoint | Method | Purpose | Authentication | Roles | Request DTO | Response DTO | Validation | Business Flow | Repository Calls | Current Status |
|---|---|---|---|---|---|---|---|---|---|---|
| `/logIn` | POST | Admin password login | Public | None | `AdminLogInRequestDTO` | `VerifyAdminResponseDTO` wrapped in `BaseResponseDTO` with refresh cookie | No Bean Validation annotations | Find admin by email, BCrypt password match, generate access/refresh token, store refresh token in Redis | `AdminRepository.findByEmail`; Redis `SET refresh:{role}:{email}:{deviceId}` | Active |
| `/forgetPassword` | POST | Send admin password reset OTP | Public | None | `ForgetPasswordRequestDTO` | `ForgetPasswordResponseDTO` | No Bean Validation annotations | Find admin, generate OTP, set OTP timestamp, save, send email | `AdminRepository.findByEmail`, `AdminRepository.save` | Active |
| `/verifyPasswordOtp` | POST | Verify admin password reset OTP and set new password | Public | None | `VerifyAdminRequestDTO` | `VerifyForgetPasswordResponseDTO` | No Bean Validation annotations; service checks expiry and OTP equality | Find admin, validate OTP age and value, encode new password, clear OTP, save | `AdminRepository.findByEmail`, `AdminRepository.save` | Active |
| `/addOfferPhoto` | POST | Update offer photo URL at a fixed position | JWT + Role | `SUPER_ADMIN` | `EditPhotoRequestDTO` | `EditPhotoResponseDTO` | No Bean Validation annotations; mapper throws for invalid position | Load latest offer row or create new, update one of five URL fields, save | `OfferPhotoRepository.findTopByOrderByCreatedAtDesc`, `OfferPhotoRepository.save` | Active |
| `/logout` | POST | Admin logout | JWT | Any authenticated principal | Authorization header, refresh cookie, `X-Device-Id` header | `AdminLogoutResponseDTO` with delete-cookie header | Header parsing only | Strip Bearer token, blacklist access token until expiry, delete refresh-token Redis key when refresh cookie exists | Redis blacklist `SET blacklisted_token:{token}`; Redis `DEL refresh:{role}:{email}:{deviceId}` | Active |
| `/editAdmin` | POST | Edit admin profile fields | JWT + Role | `ADMIN`, `SUPER_ADMIN` | `EditAdminRequestDTO` | `EditAdminResponseDTO` | No Bean Validation annotations | Find admin by email, set name/phone/image, save | `AdminRepository.findByEmail`, `AdminRepository.save` | Active |
| `/changePassword` | POST | Change admin password | JWT + Role | `ADMIN`, `SUPER_ADMIN` | `ChangePasswordRequestDTO` | `ChangePasswordResponseDTO` | No Bean Validation annotations; service checks old password and different new password | Find admin, BCrypt old password check, encode/save new password | `AdminRepository.findByEmail`, `AdminRepository.save` | Active |
| `/registerAdmin` | POST | Register a new admin | JWT + Role | `SUPER_ADMIN` | `RegisterRequestDTO`; Authorization header accepted but not used | `RegisterResponseDTO` | No Bean Validation annotations; service checks duplicate email | Check duplicate, create `AdminUsers`, encode password, assign `ADMIN`, save, send registration email | `AdminRepository.findByEmail`, `AdminRepository.save` | Active |
| `/getAdminByEmail` | POST | Get admin details by email | JWT + Role | `SUPER_ADMIN` | `GetAdminRequestDTO` | `GetAdminResponseDTO` | No Bean Validation annotations | Find admin and map name/phone/image | `AdminRepository.findByEmail` | Active |
| `/getAllAdmin` | POST | List admins with role `ADMIN` | JWT + Role | `SUPER_ADMIN` | None | `GetAdminListResponseDTO` | None | Find admins by role and map list | `AdminRepository.findByRole(Role.ADMIN)` | Active |
| `/deleteAdmin` | POST | Delete admin by email | JWT + Role | `SUPER_ADMIN` | `DeleteAdminRequestDTO` | `DeleteAdminResponseDTO` | No Bean Validation annotations | Find admin by email and delete | `AdminRepository.findByEmail`, `AdminRepository.delete` | Active |
| `/getAllProduct` | POST | Admin paginated product list | JWT | Any authenticated principal | Query params `page`, `size` | `Page<BaseResponseDTO<GetAllProductsResponseDTO>>` | Query param defaults only | Page products sorted by updatedAt desc and map response | `ProductRepository.findAll(Pageable)` | Active |
| `/getAllCategory` | POST | Admin paginated category list | JWT | Any authenticated principal | Query params `page`, `size` | `Page<BaseResponseDTO<GetCategoriesResponseDTO>>` | Query param defaults only | Page categories sorted by updatedAt desc and map response | `CategoryRepository.findAll(Pageable)` | Active |
| `/addProduct` | POST | Add product with image upload | JWT | Any authenticated principal | Multipart `data` JSON as `ProductAddRequestDTO`, `image` file | `ProductAddResponseDTO` | JSON parsing; Cloudinary service checks image content type starts with `image/` | Parse DTO, upload image, find category by name, map and save product | `CategoryRepository.findByName`, `ProductRepository.save`; Cloudinary upload | Active |
| `/updateProduct` | PUT | Update product fields | JWT + Role | `ADMIN`, `SUPER_ADMIN` | `UpdateRequestDTO` | `UpdateResponseDTO` | Controller uses `@Valid`, DTO has no constraints | Find product by name, find category by ID, update entity, save | `ProductRepository.findByName`, `CategoryRepository.findById`, `ProductRepository.save` | Active |
| `/getProductById/{productId}` | GET | Get product by ID in admin route | JWT | Any authenticated principal | Path variable `productId` | `AllProductResponseDTO` | Path variable type binding | Find product by ID and map public-style response | `ProductRepository.findById` | Active |
| `/deleteProduct` | DELETE | Delete product by name | JWT + Role | `ADMIN`, `SUPER_ADMIN` | `DeleteProductRequestDTO` | `DeleteResponseDTO` | Controller uses `@Valid`, DTO has no constraints | Find product by name and delete | `ProductRepository.findByName`, `ProductRepository.delete` | Active |
| `/getCategory` | POST | Admin get category by ID | JWT | Any authenticated principal | `GetCategoryByIdRequestDTO` | `GetCategoryByIdResponseDTO` | No Bean Validation annotations | Find category by ID and map admin response | `CategoryRepository.findById` | Active |
| `/addCategory` | POST | Add category | JWT | Any authenticated principal | `AddCategoryRequestDTO` | `AddCategoryResponseDTO` | No Bean Validation annotations; service checks duplicate name and home display limit | Check name availability, optionally enforce show-on-home count, map and save | `CategoryRepository.existsByName`, `CategoryRepository.canEnableShowOnHome`, `CategoryRepository.save` | Active |
| `/updateCategory` | PUT | Update category | JWT | Any authenticated principal | `AddCategoryRequestDTO` | `UpdateCategoryResponseDTO` | No Bean Validation annotations | Find category by name, update fields, save | `CategoryRepository.findByName`, `CategoryRepository.save` | Active |
| `/deleteCategory` | DELETE | Delete category by ID | JWT | Any authenticated principal | `GetCategoryByIdRequestDTO` | `UpdateCategoryResponseDTO` | No Bean Validation annotations | Find category by ID, delete by ID | `CategoryRepository.findById`, `CategoryRepository.deleteById` | Active |
| `/uploadExcel` | POST | Bulk category upload from Excel | JWT | Any authenticated principal | Multipart `file`, request param `createdBy` | `AddCategoryResponseDTO` on success or error workbook bytes on validation failure | Custom row validation for name/status/duplicates | Read workbook sheet 0, validate rows, return workbook on errors, save categories row-by-row | `CategoryRepository.existsByName`, `CategoryRepository.save` | Active |

## UserController - `/api/v1/auth`

| Endpoint | Method | Purpose | Authentication | Roles | Request DTO | Response DTO | Validation | Business Flow | Repository Calls | Current Status |
|---|---|---|---|---|---|---|---|---|---|---|
| `/logIn` | POST | Start user OTP login | Public | None | `logInRequestDTO` | `LogInResponseDTO` | No Bean Validation annotations | Find user by email, create or update OTP and timestamp, send OTP email | `UsersRepo.findByEmail`, `UsersRepo.save` | Active |
| `/verify` | POST | Verify user OTP and issue tokens | Public | None | `OtpRequestDTO` | `OtpResponseDTO` with refresh cookie | No Bean Validation annotations; service checks 5-minute expiry and OTP equality | Find user, validate OTP, generate access/refresh token, store refresh token in Redis | `UsersRepo.findByEmail`; Redis `SET refresh:USER:{email}:{deviceId}` | Active |
| `/logout` | POST | User logout | Not exposed | Not exposed | N/A | `LogoutResponseDTO` | N/A | Code is commented out in controller and service | N/A | Inactive/commented |

## RefreshTokenController

| Endpoint | Method | Purpose | Authentication | Roles | Request DTO | Response DTO | Validation | Business Flow | Repository Calls | Current Status |
|---|---|---|---|---|---|---|---|---|---|---|
| `/refreshToken` | POST | Validate and rotate refresh token | Public | None | `RefreshRequest` or cookie token; actual code requires token, email, role, deviceId | `RefreshTokenResponseDTO` with refresh cookie | Null checks for refreshToken/email/role/deviceId; Redis hash/BCrypt validation | Validate refresh token hash, delete old Redis key, generate new access/refresh tokens, store new hash, return cookie/body | Redis `GET`, `DEL`, `SET` on `refresh:{role}:{email}:{deviceId}` | Active |

## ProductController - `/api/v1/public`

| Endpoint | Method | Purpose | Authentication | Roles | Request DTO | Response DTO | Validation | Business Flow | Repository Calls | Current Status |
|---|---|---|---|---|---|---|---|---|---|---|
| `/getAllProducts` | GET | Public paginated product list | Public | None | Spring `Pageable` query params | `Page<AllProductResponseDTO>` | Pageable binding only | Fetch all products and map final price fields | `ProductRepository.findAll(Pageable)` | Active |
| `/getProductById/{productId}` | GET | Public product detail by ID | Public | None | Path variable `productId` | `AllProductResponseDTO` | Path variable type binding | Find product by ID or throw not found | `ProductRepository.findById` | Active |
| `/category/{category}` | GET | Public products by category name | Public | None | Path variable `category`, `Pageable` | `PageResponseDTO<AllProductResponseDTO>` | Path variable and Pageable binding | Query products by category name ignoring case and available true | `ProductRepository.findByCategory_NameIgnoreCaseAndIsAvailableTrue` | Active |
| `/materialType/{materialType}` | GET | Public products by material type | Public | None | Path variable `materialType`, `Pageable` | `PageResponseDTO<AllProductResponseDTO>` | Path variable and Pageable binding | Query products by material type ignoring case | `ProductRepository.getProductsByMaterialType` | Active |
| `/price/under` | GET | Public products with price <= value | Public | None | Request param `price`, `Pageable` | `PageResponseDTO<AllProductResponseDTO>` | BigDecimal binding; service throws not found on empty page | Query price upper bound and map page | `ProductRepository.findProductsUnderPrice` | Active |
| `/price/above` | GET | Public products with price >= value | Public | None | Request param `price`, `Pageable` | `PageResponseDTO<AllProductResponseDTO>` | BigDecimal binding; service throws not found on empty page | Query price lower bound and map page | `ProductRepository.findProductsAbovePrice` | Active |
| `/getProductsByGender` | POST | Public products by gender | Public | None | `GenderRequestDTO` | `GenderResponseDTO` | Controller uses `@Valid`, DTO has no constraints | Query gender ignoring case and map list | `ProductRepository.findProductByGender` | Active |
| `/getProductsByFilter` | POST | Public product filter search | Public | None | `ProductFilterRequestDTO`, `Pageable` | `Page<AllProductResponseDTO>` | Controller uses `@Valid`, DTO has no constraints | Optional JPQL filters for category, price, weight, material, gender, availability, stock | `ProductRepository.findProductsByFilters` | Active |

## CategoryController - `/api/v1/public`

| Endpoint | Method | Purpose | Authentication | Roles | Request DTO | Response DTO | Validation | Business Flow | Repository Calls | Current Status |
|---|---|---|---|---|---|---|---|---|---|---|
| `/getAllCategory` | POST | Public category list | Public | None | None | `GetAllCategoryUserResponseDTO` | None | Calls `CategoryServiceImp.getAllCategoriesUser` | None in active implementation | Active endpoint, service returns `null` |
| `/getCategory` | POST | Public category by ID | Public | None | `GetCategoryByIdRequestDTO` | `GetCategoryUserResponseDTO` | No Bean Validation annotations | Find category by ID and map user-facing fields | `CategoryRepository.findById` | Active |

## PublicController - `/api/v1/public`

| Endpoint | Method | Purpose | Authentication | Roles | Request DTO | Response DTO | Validation | Business Flow | Repository Calls | Current Status |
|---|---|---|---|---|---|---|---|---|---|---|
| `/getOfferPhoto` | POST | Public latest offer photo URLs | Public | None | None | `GetOfferPhotoResponseDTO` | None | Load latest offer photo; return five URL fields or nulls | `OfferPhotoRepository.findTopByOrderByCreatedAtDesc` | Active |

## OrderController

| Endpoint | Method | Purpose | Authentication | Roles | Request DTO | Response DTO | Validation | Business Flow | Repository Calls | Current Status |
|---|---|---|---|---|---|---|---|---|---|---|
| `/createOrder` | POST | Create order | JWT + Role | `ADMIN`, `SUPER_ADMIN` | `AddOrderRequestDTO` | `AddOrderResponseDTO` | No Bean Validation annotations; mapper rejects empty products list | Map order and order items, calculate payment status, save aggregate | `OrderRepository.save` | Active |
| `/updateOrder` | POST | Update order | JWT + Role | `ADMIN`, `SUPER_ADMIN` | `UpdateOrderRequestDTO` | `AddOrderResponseDTO` | No Bean Validation annotations | Find order, update nullable fields, optionally replace items, save | `OrderRepository.findById`, `OrderRepository.save` | Active |
| `/getOrderById` | POST | Get order by ID | JWT | Any authenticated principal | `GetOrderByIdRequestDTO` | `GetOrderByIdResponseDTO` | No Bean Validation annotations | Find order and map scalar fields/product IDs | `OrderRepository.findById` | Active |
| `/admin/orders` | GET | Paginated admin order list | JWT + Role | `ADMIN`, `SUPER_ADMIN` | Query params `page`, `size` | `OrderListPageResponseDTO` | Query param defaults only | Page all orders sorted by updatedAt desc and map summary rows | `OrderRepository.findAll(Pageable)` | Active |
| `/getOrderInvoiceById` | POST | Generate invoice PDF bytes | JWT | Any authenticated principal | `GetOrderInvoiceRequest` | `GetOrderInvoiceResponse` | No Bean Validation annotations | Find order, generate iText PDF byte array, return file name and bytes | `OrderRepository.findById` | Active |
| `/getTotalOrderMonth` | POST | Monthly order count | Not exposed | Not exposed | None | `GetTotalOrderMonthResponse` | N/A | Controller method commented out; service method exists | `OrderRepository.countOrdersByOrderDateBetween` | Inactive/commented |

## HomeServiceController - `/api/homeService`

| Endpoint | Method | Purpose | Authentication | Roles | Request DTO | Response DTO | Validation | Business Flow | Repository Calls | Current Status |
|---|---|---|---|---|---|---|---|---|---|---|
| `/getAllServiceRequests` | POST | List all home service requests | JWT | Any authenticated principal | None | `GetAllHomeServiceResponseDTO` | None | Fetch all rows ordered by createdAt desc and map list | `HomeServiceRepository.findAllByOrderByCreatedAtDesc` | Active |
| `/getHomeServiceRequestById` | POST | Get home service by ID | JWT | Any authenticated principal | `HomeServiceRequestDTO` | `HomeServiceResponseDTO` | No Bean Validation annotations | Find service by ID and map response | `HomeServiceRepository.findById` | Active |
| `/createHomeServiceRequest` | POST | Create home service request | JWT | Any authenticated principal | `CreateHomeServiceRequestDTO` | `CreateHomeServiceResponseDTO` | No Bean Validation annotations; enum value parsing can throw | Map request to `ServiceHome` with `REQUESTED` status and save | `HomeServiceRepository.save` | Active |
| `/editHomeServiceRequest` | PUT | Edit home service request | JWT | Any authenticated principal | `EditHomeServiceRequestDTO` | `EditHomeServiceResponseDTO` | No Bean Validation annotations | Find service, update fields/status/type, save | `HomeServiceRepository.findById`, `HomeServiceRepository.save` | Active |
| `/searchHomeServiceRequest` | POST | Search home service by ID or name | JWT | Any authenticated principal | `SearchHomeServiceRequestDTO` | `GetAllHomeServiceResponseDTO` | Keyword numeric check only | Convert keyword to service ID or name, run JPQL search, map list | `HomeServiceRepository.searchHomeServices` | Active |
| `/deleteHomeServiceRequest` | DELETE | Delete home service request | JWT | Any authenticated principal | `DeleteHomeServiceRequestDTO` | `DeleteHomeServiceResponseDTO` | No Bean Validation annotations | Find service, delete by ID | `HomeServiceRepository.findById`, `HomeServiceRepository.deleteById` | Active |
| `/getAllUserServiceRequests` | POST | User-facing home service list | JWT | Any authenticated principal | None | `GetAllHomeServiceResponseDTO` | None | Calls `HomeServiceImp.getAllUserServiceRequests` | None in active implementation | Active endpoint, service returns `null` |

## RepairRequestController - `/api/common`

| Endpoint | Method | Purpose | Authentication | Roles | Request DTO | Response DTO | Validation | Business Flow | Repository Calls | Current Status |
|---|---|---|---|---|---|---|---|---|---|---|
| `/getAllRepairRequests` | POST | List all repair requests | JWT | Any authenticated principal | None | `GetAllRepairResponseDTO` | None | Fetch all rows ordered by createdAt desc and map list | `RepairRequestRepository.findAllByOrderByCreatedAtDesc` | Active |
| `/searchRepairRequest` | POST | Search repair request by ID or name | JWT | Any authenticated principal | `SearchRepairRequestDTO` | `GetAllRepairResponseDTO` | Keyword numeric check only | Convert keyword to service ID or name, run JPQL search, map list | `RepairRequestRepository.searchRepairRequests` | Active |
| `/getRepairRequestById` | POST | Get repair request by ID | JWT | Any authenticated principal | `RepairRequestRequestDTO` | `RepairRequestResponseDTO` | No Bean Validation annotations | Find repair request by ID and map response | `RepairRequestRepository.findById` | Active |
| `/createRepairRequest` | POST | Create repair request | JWT | Any authenticated principal | `CreateRepairRequestDTO` | `CreateRepairResponseDTO` | No Bean Validation annotations | Map request to `RepairService` with `REQUESTED` status and save | `RepairRequestRepository.save` | Active |
| `/editRepairRequest` | PUT | Edit repair request | JWT | Any authenticated principal | `EditRepairRequestDTO` | `EditRepairResponseDTO` | No Bean Validation annotations | Find repair request, update fields/status, save | `RepairRequestRepository.findById`, `RepairRequestRepository.save` | Active |
| `/deleteRepairRequest` | DELETE | Delete repair request | JWT | Any authenticated principal | `DeleteRepairRequestDTO` | `DeleteRepairResponseDTO` | No Bean Validation annotations | Find repair request, delete by ID | `RepairRequestRepository.findById`, `RepairRequestRepository.deleteById` | Active |

