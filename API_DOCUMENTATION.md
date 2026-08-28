# Shyam Jewellers Backend - API Documentation

This document provides a comprehensive overview of all available APIs in the Shyam Jewellers backend system.

## Table of Contents
1. [Admin APIs](#admin-apis)
2. [User APIs](#user-apis)
3. [Public APIs](#public-apis)
4. [Order APIs](#order-apis)
5. [Home Service APIs](#home-service-apis)
6. [Repair Request APIs](#repair-request-apis)
7. [Payment APIs](#payment-apis)

---

## Admin APIs

*Base Path: `/auth/api/v1/admin`*

| Endpoint | Method | Description | Request | Response | Auth Required |
|----------|--------|-------------|---------|----------|---------------|
| `/logIn` | POST | Login an admin user | `AdminLogInRequestDTO` | `BaseResponseDTO<VerifyAdminResponseDTO>` | No |
| `/forgetPassword` | POST | Reset password (initiate OTP) | `ForgetPasswordRequestDTO` | `BaseResponseDTO<ForgetPasswordResponseDTO>` | No |
| `/verifyPasswordOtp` | POST | Verify OTP for password reset | `VerifyAdminRequestDTO` | `BaseResponseDTO<VerifyForgetPasswordResponseDTO>` | No |
| `/addOfferPhoto` | POST | Add/update offer photo (SUPER_ADMIN only) | `EditPhotoRequestDTO` | `BaseResponseDTO<EditPhotoResponseDTO>` | SUPER_ADMIN |
| `/logout` | POST | Logout admin user | Headers: Authorization, Cookie: refreshToken, Header: X-Device-Id | `BaseResponseDTO<AdminLogoutResponseDTO>` | Yes |
| `/editAdmin` | POST | Edit admin profile (ADMIN/SUPER_ADMIN) | `EditAdminRequestDTO` | `BaseResponseDTO<EditAdminResponseDTO>` | ADMIN/SUPER_ADMIN |
| `/changePassword` | POST | Change admin password (ADMIN/SUPER_ADMIN) | `ChangePasswordRequestDTO` | `BaseResponseDTO<ChangePasswordResponseDTO>` | ADMIN/SUPER_ADMIN |
| `/registerAdmin` | POST | Register new admin (SUPER_ADMIN only) | `RegisterRequestDTO` + Header: Authorization | `BaseResponseDTO<RegisterResponseDTO>` | SUPER_ADMIN |
| `/getAdminByEmail` | POST | Get admin by email (SUPER_ADMIN only) | `GetAdminRequestDTO` | `BaseResponseDTO<GetAdminResponseDTO>` | SUPER_ADMIN |
| `/getAllAdmin` | POST | Get all admins (SUPER_ADMIN only) | None | `BaseResponseDTO<GetAdminListResponseDTO>` | SUPER_ADMIN |
| `/deleteAdmin` | POST | Delete admin (SUPER_ADMIN only) | `DeleteAdminRequestDTO` | `BaseResponseDTO<DeleteAdminResponseDTO>` | SUPER_ADMIN |
| `/getAllProduct` | POST | Get all products (paginated) | Query: page, size | `Page<BaseResponseDTO<GetAllProductsResponseDTO>>` | Yes |
| `/getAllCategory` | POST | Get all categories (paginated) | Query: page, size | `Page<BaseResponseDTO<GetCategoriesResponseDTO>>` | Yes |
| `/addProduct` | POST | Add new product (multipart/form-data) | Form: data (JSON), image (file) | `BaseResponseDTO<ProductAddResponseDTO>` | Yes |
| `/updateProduct` | PUT | Update product (ADMIN/SUPER_ADMIN) | `UpdateRequestDTO` | `BaseResponseDTO<UpdateResponseDTO>` | ADMIN/SUPER_ADMIN |
| `/getProductById/{productId}` | GET | Get product by ID | Path: productId | `BaseResponseDTO<AllProductResponseDTO>` | Yes |
| `/deleteProduct` | DELETE | Delete product (ADMIN/SUPER_ADMIN) | `DeleteProductRequestDTO` | `BaseResponseDTO<DeleteResponseDTO>` | ADMIN/SUPER_ADMIN |
| `/getCategory` | POST | Get category by ID | `GetCategoryByIdRequestDTO` | `BaseResponseDTO<GetCategoryByIdResponseDTO>` | Yes |
| `/addCategory` | POST | Add new category | `AddCategoryRequestDTO` | `BaseResponseDTO<AddCategoryResponseDTO>` | Yes |
| `/updateCategory` | PUT | Update category | `AddCategoryRequestDTO` | `BaseResponseDTO<UpdateCategoryResponseDTO>` | Yes |
| `/deleteCategory` | DELETE | Delete category | `GetCategoryByIdRequestDTO` | `BaseResponseDTO<UpdateCategoryResponseDTO>` | Yes |
| `/uploadExcel` | POST | Upload Excel file for categories | Form: file (MultipartFile), createdBy (String) | `ResponseEntity<?>` | Yes |

---

## User APIs

*Base Path: `/api/v1/auth`*

| Endpoint | Method | Description | Request | Response | Auth Required |
|----------|--------|-------------|---------|----------|---------------|
| `/logIn` | POST | Login a user | `logInRequestDTO` | `BaseResponseDTO<LogInResponseDTO>` | No |
| `/verify` | POST | Verify OTP for login | `OtpRequestDTO` | `ResponseEntity<BaseResponseDTO<OtpResponseDTO>>` | No |
| `/logout` | POST | Logout user | Headers: Authorization, Cookie: refreshToken, Header: X-Device-Id | `BaseResponseDTO<LogoutResponseDTO>` | Yes |

---

## Public APIs

*Base Path: `/api/v1/public` (unless specified otherwise)*

### Product Controller
| Endpoint | Method | Description | Request | Response | Auth Required |
|----------|--------|-------------|---------|----------|---------------|
| `/getAllProducts` | GET | Get all products (paginated) | Pageable | `BaseResponseDTO<Page<AllProductResponseDTO>>` | No |
| `/getProductById/{productId}` | GET | Get product by ID | Path: productId | `BaseResponseDTO<AllProductResponseDTO>` | No |
| `/category/{category}` | GET | Get products by category | Path: category, Pageable | `BaseResponseDTO<PageResponseDTO<AllProductResponseDTO>>` | No |
| `/materialType/{materialType}` | GET | Get products by material type | Path: materialType, Pageable | `BaseResponseDTO<PageResponseDTO<AllProductResponseDTO>>` | No |
| `/price/under` | GET | Get products under given price | Query: price (BigDecimal), Pageable | `BaseResponseDTO<PageResponseDTO<AllProductResponseDTO>>` | No |
| `/price/above` | GET | Get products above given price | Query: price (BigDecimal), Pageable | `BaseResponseDTO<PageResponseDTO<AllProductResponseDTO>>` | No |
| `/getProductsByGender` | POST | Get products by gender | `GenderRequestDTO` | `BaseResponseDTO<GenderResponseDTO>` | No |
| `/getProductsByFilter` | POST | Get filtered products (paginated) | `ProductFilterRequestDTO`, Pageable | `BaseResponseDTO<Page<AllProductResponseDTO>>` | No |

### Category Controller
| Endpoint | Method | Description | Request | Response | Auth Required |
|----------|--------|-------------|---------|----------|---------------|
| `/getAllCategory` | POST | Get all categories | None | `BaseResponseDTO<GetAllCategoryUserResponseDTO>` | No |
| `/getCategory` | POST | Get category by ID | `GetCategoryByIdRequestDTO` | `BaseResponseDTO<GetCategoryUserResponseDTO>` | No |

### Public Controller
| Endpoint | Method | Description | Request | Response | Auth Required |
|----------|--------|-------------|---------|----------|---------------|
| `/getOfferPhoto` | POST | Get offer photo/banner | None | `BaseResponseDTO<GetOfferPhotoResponseDTO>` | No |

---

## Order APIs

*Base Path: `/api/v1` (unless specified otherwise)*

| Endpoint | Method | Description | Request | Response | Auth Required |
|----------|--------|-------------|---------|----------|---------------|
| `/createOrder` | POST | Create a new order (ADMIN/SUPER_ADMIN) | `AddOrderRequestDTO` | `BaseResponseDTO<AddOrderResponseDTO>` | ADMIN/SUPER_ADMIN |
| `/updateOrder` | POST | Update existing order (ADMIN/SUPER_ADMIN) | `UpdateOrderRequestDTO` | `BaseResponseDTO<AddOrderResponseDTO>` | ADMIN/SUPER_ADMIN |
| `/getOrderById` | POST | Get order details by ID | `GetOrderByIdRequestDTO` | `BaseResponseDTO<GetOrderByIdResponseDTO>` | Yes |
| `/admin/orders` | GET | Get all orders (paginated) (ADMIN/SUPER_ADMIN) | Query: page, size | `BaseResponseDTO<OrderListPageResponseDTO>` | ADMIN/SUPER_ADMIN |
| `/getOrderInvoiceById` | POST | Generate PDF invoice for order | `GetOrderInvoiceRequest` | `BaseResponseDTO<GetOrderInvoiceResponse>` | Yes |

---

## Home Service APIs

*Base Path: `/api/homeService`*

| Endpoint | Method | Description | Request | Response | Auth Required |
|----------|--------|-------------|---------|----------|---------------|
| `/getAllServiceRequests` | POST | Get all home service requests | None | `BaseResponseDTO<GetAllHomeServiceResponseDTO>` | Yes |
| `/getHomeServiceRequestById` | POST | Get home service request by ID | `HomeServiceRequestDTO` | `BaseResponseDTO<HomeServiceResponseDTO>` | Yes |
| `/createHomeServiceRequest` | POST | Create new home service request | `CreateHomeServiceRequestDTO` | `BaseResponseDTO<CreateHomeServiceResponseDTO>` | Yes |
| `/editHomeServiceRequest` | PUT | Edit home service request | `EditHomeServiceRequestDTO` | `BaseResponseDTO<EditHomeServiceResponseDTO>` | Yes |
| `/searchHomeServiceRequest` | POST | Search home service requests | `SearchHomeServiceRequestDTO` | `BaseResponseDTO<GetAllHomeServiceResponseDTO>` | Yes |
| `/deleteHomeServiceRequest` | DELETE | Delete home service request | `DeleteHomeServiceRequestDTO` | `BaseResponseDTO<DeleteHomeServiceResponseDTO>` | Yes |
| `/getAllUserServiceRequests` | POST | Get all home service requests for user | None | `BaseResponseDTO<GetAllHomeServiceResponseDTO>` | Yes |

---

## Repair Request APIs

*Base Path: `/api/common`*

| Endpoint | Method | Description | Request | Response | Auth Required |
|----------|--------|-------------|---------|----------|---------------|
| `/getAllRepairRequests` | POST | Get all repair requests | None | `BaseResponseDTO<GetAllRepairResponseDTO>` | Yes |
| `/searchRepairRequest` | POST | Search repair requests | `SearchRepairRequestDTO` | `BaseResponseDTO<GetAllRepairResponseDTO>` | Yes |
| `/getRepairRequestById` | POST | Get repair request by ID | `RepairRequestRequestDTO` | `BaseResponseDTO<RepairRequestResponseDTO>` | Yes |
| `/createRepairRequest` | POST | Create new repair request | `CreateRepairRequestDTO` | `BaseResponseDTO<CreateRepairResponseDTO>` | Yes |
| `/editRepairRequest` | PUT | Edit repair request | `EditRepairRequestDTO` | `BaseResponseDTO<EditRepairResponseDTO>` | Yes |
| `/deleteRepairRequest` | DELETE | Delete repair request | `DeleteRepairRequestDTO` | `BaseResponseDTO<DeleteRepairResponseDTO>` | Yes |

---

## Payment APIs

*Base Path: `/api/v1/payments`*

| Endpoint | Method | Description | Request | Response | Auth Required |
|----------|--------|-------------|---------|----------|---------------|
| `/create` | POST | Create payment request for an order | Header: Idempotency-Key (optional), Body: CreatePaymentRequestDTO | `BaseResponseDTO<PaymentResponseDTO>` | Yes |
| `/verify` | POST | Verify payment signature and update status | Body: VerifyPaymentRequestDTO | `BaseResponseDTO<PaymentVerificationResponseDTO>` | Yes |
| `/failure` | POST | Handle payment failure | Body: PaymentFailureRequestDTO | `BaseResponseDTO<PaymentResponseDTO>` | Yes |
| `/{paymentId}/retry` | POST | Retry payment for an order | Path: paymentId, Header: Idempotency-Key (optional), Body: RetryPaymentRequestDTO | `BaseResponseDTO<PaymentResponseDTO>` | Yes |
| `/{paymentId}/cancel` | POST | Cancel payment request | Path: paymentId, Body: CancelPaymentRequestDTO | `BaseResponseDTO<PaymentResponseDTO>` | Yes |
| `/refund` | POST | Refund successful payment (ADMIN/SUPER_ADMIN) | Header: Idempotency-Key (optional), Body: RefundPaymentRequestDTO | `BaseResponseDTO<PaymentResponseDTO>` | ADMIN/SUPER_ADMIN |
| `/{paymentId}` | GET | Get payment details by ID | Path: paymentId | `BaseResponseDTO<PaymentResponseDTO>` | Yes |
| `/orders/{orderId}/history` | GET | Get payment history for an order | Path: orderId | `BaseResponseDTO<PaymentHistoryResponseDTO>` | Yes |

---

## Authentication & Security Notes

### JWT Authentication
- Most endpoints require a valid JWT token in the `Authorization` header as `Bearer <token>`
- Tokens are obtained via login endpoints (`/auth/api/v1/admin/logIn` for admins, `/api/v1/auth/logIn` for users)
- Refresh tokens are handled via HTTP-only cookies named `refreshToken`

### Role-Based Access Control
- Roles used: `USER`, `ADMIN`, `SUPER_ADMIN`
- `@PreAuthorize` annotations enforce role-based access
- Some endpoints require specific roles (e.g., SUPER_ADMIN only for admin registration)

### Idempotency
- Payment creation, retry, and refund operations support idempotency via `Idempotency-Key` header
- This prevents duplicate operations when requests are retried

### Rate Limiting & Security
- Note: Current implementation does not include rate limiting
- Security headers (CSP, HSTS, etc.) are configured in SecurityConfig
- CORS is configured to allow specific origins

---

## Response Format

Most APIs follow a standardized response format using `BaseResponseDTO<T>`:

```json
{
  "body": { /* Actual response data */ },
  "error": null  /* or error details if applicable */
}
```

Some endpoints return `ResponseEntity<BaseResponseDTO<T>>` to allow custom HTTP status codes and headers.

## Error Handling
- Standardized exception handling via `@ControllerAdvice` (not shown in controllers but present in common package)
- Error responses follow the same BaseResponseDTO structure with error details populated

---

## Database & External Services
- Primary database: PostgreSQL
- Caching: Redis (for token blacklisting, sessions)
- File storage: Cloudinary (for product images, offer photos)
- Email: SMTP service (for OTP, notifications)
- Payment gateways: Abstracted via PaymentGatewayProvider (MOCK, RAZORPAY, PHONEPE, CASHFREE implementations)

---
*Documentation generated on: 2026-08-26*