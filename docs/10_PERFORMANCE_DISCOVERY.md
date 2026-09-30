# Shyam Jewellers Backend - Performance Discovery

## Pagination

| API / Flow | Pagination Status |
|---|---|
| Admin product list `/auth/api/v1/admin/getAllProduct` | Uses `page` and `size` params |
| Admin category list `/auth/api/v1/admin/getAllCategory` | Uses `page` and `size` params |
| Public all products `/api/v1/public/getAllProducts` | Uses Spring `Pageable` |
| Public products by category/material/price/filter | Uses Spring `Pageable` |
| Admin order list `/admin/orders` | Uses `page` and `size` params |
| Admin list `/getAllAdmin` | No pagination |
| Home service list/search | No pagination |
| Repair request list/search | No pagination |
| Public category list | Endpoint active, service currently returns `null` |

## Sorting

| Flow | Current Sort |
|---|---|
| Admin category list | `updatedAt` descending with nulls last |
| Admin product list | `updatedAt` descending with nulls last |
| Admin order list | `updatedAt` descending |
| Home service list | Repository method orders by `createdAt` descending |
| Repair request list | Repository method orders by `createdAt` descending |

## Filtering

Product filtering is the most developed filtering area.

`ProductRepository.findProductsByFilters` supports:

- Category name
- Minimum price
- Maximum price
- Minimum weight
- Maximum weight
- Material type
- Gender
- Availability
- Minimum available stock
- Maximum available stock

Other filters:

- Product by gender.
- Product by category.
- Product by material type.
- Product price under/above.
- Home service keyword search.
- Repair request keyword search.

## Repository Query Observations

### Product Queries

Product queries are page-aware for public read paths. Category query enforces `isAvailable=true`; material and price queries do not include availability constraints in the repository query.

`findProductsByFilters` uses optional-parameter JPQL:

```text
(:param IS NULL OR field comparison)
```

This supports flexible filters from one endpoint.

### Home and Repair Search Queries

Home and repair search use:

```text
(:serviceId IS NULL OR s.serviceId = :serviceId)
OR (:name IS NULL OR LOWER(s.name) LIKE ...)
```

With one parameter null, one side of the `OR` can become true for every row. This can produce full table results during keyword search.

### Order Queries

Order list uses `findAll(Pageable)`, which loads `Order` entities. Mapping uses `order.getItems().size()` for each order.

Since `Order.items` is a one-to-many collection with default lazy loading, order list mapping can trigger additional collection loads inside the read-only transaction.

## Lazy Loading and Entity Exposure

| Area | Current Behavior |
|---|---|
| `Products.category` | `@ManyToOne` defaults to eager loading |
| `Order.items` | `@OneToMany` defaults to lazy loading |
| `GetAllProductsResponseDTO.category` | Exposes `Category` entity directly |
| Product public DTO | Uses scalar product fields and does not expose full category object |

No `@EntityGraph` or fetch-join repository methods were observed.

## Caching

Redis is used for authentication token state:

- Refresh token hashes.
- Access token blacklist entries.

No application data caching was observed for:

- Product list/search.
- Category list.
- Offer photos.
- Dashboard/order summaries.
- Service request searches.

No Spring Cache annotations were observed.

## File and Memory Operations

| Flow | Current Behavior |
|---|---|
| Product image upload | Reads multipart file bytes into memory before Cloudinary upload |
| Category Excel upload | Reads workbook from multipart input stream and validates sheet 0 |
| Excel error report | Builds workbook in memory and returns byte array |
| Invoice PDF | Builds PDF in memory and returns byte array inside JSON response |

## Index Support Observed

No explicit JPA `@Index` annotations were found.

Migration-level constraints/indexes observed:

- Primary keys.
- Unique admin email.
- Unique product name and SKU.
- Join table primary key and foreign keys in `order_product_ids`.

Common query fields without explicit observed indexes include:

- Product category.
- Product material type.
- Product gender.
- Product price.
- Product availability.
- Product stock.
- Order date/status.
- Service request name/status.
- Repair request name/status.

## Transaction and Persistence Performance

Service methods use read-only transactions for most read operations. Write operations are generally transactional.

Potential persistence load points observed:

- Unpaginated home service list.
- Unpaginated repair request list.
- Unpaginated admin list.
- Order list item count access on lazy collection.
- Search queries that can return all rows.
- Excel upload saves rows one at a time.

## External Dependency Performance

| Integration | Current Runtime Coupling |
|---|---|
| Email | Synchronous send inside request flow |
| Cloudinary | Synchronous upload inside add-product request |
| Redis | Synchronous token validation on secured requests through blacklist check |
| PDF | Synchronous generation inside invoice request |
| Excel | Synchronous validation/import inside upload request |

