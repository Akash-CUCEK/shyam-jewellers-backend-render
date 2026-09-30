# Admin and Public APIs Documentation

This document lists all Admin APIs and Public APIs available in the Shyam Jewellers backend.

## Admin APIs
*These APIs require admin authentication and authorization.*

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/api/v1/admin/login` | Initiate admin login (send OTP) |
| POST | `/auth/api/v1/admin/verify` | Verify OTP and complete admin login |
| POST | `/auth/api/v1/admin/logout` | Admin logout |

### Admin Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/auth/api/v1/admin/admins` | Get all admins |
| GET | `/auth/api/v1/admin/admin/{id}` | Get admin by ID |
| POST | `/auth/api/v1/admin/admin` | Create new admin |
| PUT | `/auth/api/v1/admin/admin/{id}` | Update admin by ID |
| DELETE | `/auth/api/v1/admin/admin/{id}` | Delete admin by ID |

### Offer Photo Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/auth/api/v1/admin/offer-photos` | Get all offer photos |
| POST | `/auth/api/v1/admin/offer-photo` | Create or update offer photo |
| DELETE | `/auth/api/v1/admin/offer-photo/{id}` | Delete offer photo by ID |

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

### Metals Dev API (Internal - for scheduler only)
*Note: This API is used internally by the MetalRateFetchScheduler and not exposed for direct frontend consumption.*
- Internal: Fetches metal rates from https://api.metals.dev/v1/metal/authority?api_key=XXX&authority=ibja&currency=INR&unit=g