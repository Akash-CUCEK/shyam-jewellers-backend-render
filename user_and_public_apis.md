# User and Public APIs Documentation

This document lists all User APIs and Public APIs available in the Shyam Jewellers backend.

## User APIs
*These APIs require user authentication and authorization.*

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/login` | Initiate user login (send OTP) |
| POST | `/api/v1/auth/verify` | Verify OTP and complete user login |
| POST | `/api/v1/auth/logout` | User logout |

### Payment Processing
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/payments/initiate` | Initiate a new payment |
| GET | `/api/v1/payments/status/{paymentId}` | Get payment status by ID |
| POST | `/api/v1/payments/verify` | Verify payment completion |
| GET | `/api/v1/payments/history` | Get payment history for user |
| GET | `/api/v1/payments/receipt/{paymentId}` | Get payment receipt by ID |

### Order Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/orders` | Get user's order history |
| GET | `/api/v1/orders/{orderId}` | Get order details by ID |
| POST | `/api/v1/orders` | Create a new order |
| PUT | `/api/v1/orders/{orderId}` | Update order by ID |
| DELETE | `/api/v1/orders/{orderId}` | Cancel order by ID |
| GET | `/api/v1/orders/{orderId}/payment` | Get payment details for order |

## Public APIs
*These APIs are accessible without authentication.*

### Offer Photos
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/public/offer-photos` | Get all active offer photos |

### Products
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/public/products` | Get all products (with optional filters) |
| GET | `/api/v1/public/product/{id}` | Get product by ID |
| GET | `/api/v1/public/products/category/{categoryId}` | Get products by category |
| GET | `/api/v1/public/products/search` | Search products by query |

### Categories
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/public/categories` | Get all active categories |
| GET | `/api/v1/public/category/{id}` | Get category by ID |

### Jewelry Pricing
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/jewelry/calculate-price` | Calculate jewelry price based on material, weight, and purity |

### Repair Requests
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/common/repair-request` | Submit a new repair request |
| GET | `/api/common/repair-request/{id}` | Get repair request status by ID |
| GET | `/api/common/repair-requests` | Get all repair requests (with optional filters) |

### Home Service Requests
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/homeService/home-service-request` | Submit a new home service request |
| GET | `/api/homeService/home-service-request/{id}` | Get home service request status by ID |
| GET | `/api/homeService/home-service-requests` | Get all home service requests (with optional filters) |