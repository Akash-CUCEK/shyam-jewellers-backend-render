# Shyam Jewellers Backend API Documentation

This document summarizes the API endpoints created for the Order, Payment, and Address modules, grouped by audience: **Admin**, **Public**, and **User**.

---  
## Base URL
All endpoints are relative to the deployed base URL (e.g., `https://api.shyamjewellers.com`).

---  
## 1. Admin APIs  
*Requires authentication with role `ADMIN` or `SUPER_ADMIN` (unless noted otherwise).*

### Product Management (AdminController)
| Method | Endpoint | Description | Request | Response |
|--------|----------|-------------|---------|----------|
| POST | `/auth/api/v1/admin/addProduct` | Add a new product | `AddProductRequestDTO` (validated) | `BaseResponseDTO<AddProductResponseDTO>` |
| PUT | `/auth/api/v1/admin/updateProduct` | Update an existing product | `UpdateProductRequestDTO` (validated) | `BaseResponseDTO<AddProductResponseDTO>` |
| DELETE | `/auth/api/v1/admin/deleteProduct` | Delete a product (set status to INACTIVE) | `GetProductByIdRequestDTO` (validated) | `BaseResponseDTO<AddProductResponseDTO>` |
| POST | `/auth/api/v1/admin/getAllProductsAdmin` | Paginated list of all products (all statuses) | `page` (int, default 0), `size` (int, default 10), `category` (String, optional), `materialType` (String, optional), `status` (String, optional) | `BaseResponseDTO<Page<GetProductResponseDTO>>` |
| GET | `/auth/api/v1/admin/getProductById/{productId}` | Get product details by ID (any status) | `productId` (PathVariable) | `BaseResponseDTO<GetProductResponseDTO>` |

### Order Management (Admin) – AdminOrderController
| Method | Endpoint | Description | Request | Response |
|--------|----------|-------------|---------|----------|
| GET | `/admin/orders` | Paginated list of all orders (admin view) | `page` (int, default 0), `size` (int, default 10), `status` (String, optional), `startDate` (String, optional), `endDate` (String, optional) | `Page<Order>` |
| GET | `/admin/orders/{orderId}` | Get order details by ID (admin view) | `orderId` (PathVariable) | `Order` |
| PATCH | `/admin/orders/{orderId}/status` | Update order status manually | `orderId` (PathVariable), `status` (RequestParam) | `Order` |

---  
## 2. Public APIs  
*No authentication required.*

### Offer & Purity (PublicController)
| Method | Endpoint | Description | Request | Response |
|--------|----------|-------------|---------|----------|
| POST | `/api/v1/public/getOfferPhoto` | Get list of available offer photos | None | `BaseResponseDTO<List<GetOfferPhotoResponseDTO>>` |
| GET | `/api/v1/public/purities` | Get list of active purities, optionally filtered by material type | `materialTypeId` (Long, optional) | `BaseResponseDTO<List<GetPurityResponseDTO>>` |

### Product Public – ProductPublicController
| Method | Endpoint | Description | Request | Response |
|--------|----------|-------------|---------|----------|
| GET | `/api/v1/public/products` | Paginated list of **active** products (status = ACTIVE) | `page` (int, default 0), `size` (int, default 10), `categoryId` (Long, optional), `materialTypeId` (Long, optional) | `BaseResponseDTO<Page<GetProductResponseDTO>>` |
| GET | `/api/v1/public/products/{productId}` | Get product details by ID **only if status is ACTIVE** | `productId` (PathVariable) | `BaseResponseDTO<GetProductResponseDTO>` (returns error if not ACTIVE) |

---  
## 3. User APIs  
*Requires authentication (JWT token). Access limited to the user's own data.*

### Order Management – OrderController
| Method | Endpoint | Description | Request | Response |
|--------|----------|-------------|---------|----------|
| POST | `/orders/checkout` | Create a new order from cart items, reserve stock, create Razorpay order | **Body**: `List<CartItem>` (each: `{variantId, quantity}`)<br>**Params**: `addressId` (Long), `userId` (Long) | `OrderCheckoutResponse` (contains `orderId`, `orderNumber`, `totalAmount`, `razorpayOrderId`, `razorpayAmount`) |
| POST | `/orders/{orderId}/verify-payment` | Verify Razorpay payment and update order/payment status | **Params**: `orderId` (PathVariable), `razorpayPaymentId`, `razorpayOrderId`, `razorpaySignature` (all RequestParam) | `Payment` entity |
| GET | `/orders` | Get paginated list of orders for the authenticated user | **Params**: `userId` (Long), `page` (int, default 0), `size` (int, default 10) | `Page<Order>` |
| GET | `/orders/{orderId}` | Get details of a specific order (if belongs to user) | **Params**: `orderId` (PathVariable) | `Order` |
| POST | `/orders/{orderId}/cancel` | Cancel an order (only if status is PENDING or CONFIRMED) | **Params**: `orderId` (PathVariable) | `Order` |

### Address Management – AddressController
| Method | Endpoint | Description | Request | Response |
|--------|----------|-------------|---------|----------|
| POST | `/addresses` | Add a new address for the authenticated user | **Body**: `Address` object | `Address` |
| PUT | `/addresses/{id}` | Update an existing address | **Params**: `id` (PathVariable)<br>**Body**: `Address` object | `Address` |
| DELETE | `/addresses/{id}` | Delete an address | **Params**: `id` (PathVariable) | `204 No Content` |
| GET | `/addresses` | Get paginated list of all addresses for the authenticated user | **Params**: `page` (int, default 0), `size` (int, default 10) | `Page<Address>` |
| PATCH | `/addresses/{id}/default` | Set an address as the default for the user | **Params**: `id` (PathVariable), `userId` (RequestParam) | `Address` |

### Webhook (Public, signature‑verified) – WebhookController
| Method | Endpoint | Description | Request | Response |
|--------|----------|-------------|---------|----------|
| POST | `/webhooks/razorpay` | Razorpay webhook endpoint to handle payment events | **Header**: `X-Razorpay-Signature`<br>**Body**: Razorpay webhook payload (JSON) | `200 OK` on success; appropriate error codes otherwise |

---  
## Notes
- **Stock Management**: Uses `ProductVariant.quantity` (total stock) and `ProductVariant.reservedQuantity` (reserved in carts). On order creation, `reservedQuantity` is incremented; on payment success, `quantity` is decremented and `reservedQuantity` released; on cancellation/failure, only `reservedQuantity` is released.
- **Order Number Format**: Placeholder implementation generates `ORD-{year}-{00001}`; replace with proper sequence logic if needed.
- **Razorpay Integration**: Placeholder methods (`createRazorpayOrder`, `verifyRazorpaySignature`) must be replaced with actual Razorpay API calls using keys from `application.properties`.
- **Authentication**: User endpoints require a valid JWT token in the `Authorization` header (`Bearer <token>`). Admin endpoints are protected by Spring Security with `@PreAuthorize` roles.
- **Audit Fields**: All entities include `createdAt`, `createdBy`, `updatedAt`, `updatedBy` (set by service layer).

---  
*Document generated on 2026-09-14.*