# Shyam Jewellers Backend - Database Discovery

## Current Persistence Stack

| Area | Current Implementation |
|---|---|
| ORM | Spring Data JPA / Hibernate |
| Entity package | `com.shyam.entity` |
| Repository package | `com.shyam.repository` |
| DAO package | `com.shyam.dao` |
| Local database profile | PostgreSQL |
| Render database profile | PostgreSQL |
| Prod database profile | SQL Server configured in YAML |
| Test database profile | H2 in PostgreSQL compatibility mode |
| Schema generation | `spring.jpa.hibernate.ddl-auto=update` in local/render/prod, `create-drop` in test |
| Migration scripts | Present under `src/main/resources/migration` |
| Flyway runtime | No Flyway dependency found in `pom.xml`; prod profile sets `spring.flyway.enabled=false` |

## JPA Entities

| Entity | Table | Primary Key | Notes |
|---|---|---|---|
| `AdminUsers` | `admin_users` | `id` | Email unique and not null; role enum string |
| `Users` | `users` | `userId` | Email unique in entity; OTP login fields |
| `Category` | `category` | `categoryId` | Name unique and not null in entity; home display/status fields |
| `Products` | `products` | `productIds` | Many-to-one category, generated SKU, price/stock/image fields |
| `OfferPhoto` | `offer_photo` | `id` | Five URL columns |
| `Order` | `orders` | `id` | Customer, status, payment, created-by, timestamps |
| `OrderItem` | `order_items` | `id` | Many-to-one order; scalar `productId` |
| `ServiceHome` | `service_home` | `serviceId` | Home service request fields |
| `RepairService` | `repair_service` | `serviceId` | Repair service request fields |

## Relationships

| Relationship | Current JPA Model |
|---|---|
| Category to Products | `Products.category` uses `@ManyToOne` and `@JoinColumn(name = "category_id", nullable = false)` |
| Order to OrderItem | `Order.items` uses `@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)` |
| OrderItem to Order | `OrderItem.order` uses `@ManyToOne` and `@JoinColumn(name = "order_id")` |
| OrderItem to Product | No JPA relationship; `OrderItem.productId` is a scalar `Long` |
| Admin/User to Orders | No JPA relationship; `Order.createdBy` is a string |
| User to Service Requests | No JPA relationship; created-by/email values are strings |

## Entity Detail

### `admin_users`

| Field | Java Type | Mapping |
|---|---|---|
| `id` | `Long` | `@Id`, sequence generator `admin_users_seq` |
| `email` | `String` | `nullable=false`, `unique=true` |
| `password` | `String` | Plain column |
| `otp` | `String` | Plain column |
| `otp_generated_time` | `LocalDateTime` | Explicit column name |
| `name` | `String` | Plain column |
| `phone_number` | `String` | Explicit column name |
| `image_url` | `String` | Explicit column name |
| `role` | `Role` | Enum string |

### `users`

| Field | Java Type | Mapping |
|---|---|---|
| `userId` | `Long` | `@Id`, sequence generator `users_seq` |
| `email` | `String` | `unique=true` |
| `otp` | `String` | Plain column |
| `otp_generated_time` | `LocalDateTime` | Explicit column name |

### `category`

| Field | Java Type | Mapping |
|---|---|---|
| `categoryId` | `Long` | `@Id`, sequence generator `category_seq` |
| `name` | `String` | `nullable=false`, `unique=true` |
| `show_on_home` | `Boolean` | `nullable=false` |
| `image_url` | `String` | length 500 |
| `created_at` | `LocalDateTime` | not updated |
| `created_by` | `String` | not updated |
| `updated_at` | `LocalDateTime` | plain timestamp |
| `updated_by` | `String` | plain column |
| `status` | `Boolean` | `nullable=false` |

### `products`

| Field | Java Type | Mapping |
|---|---|---|
| `productIds` | `Long` | `@Id`, sequence generator `products_seq` |
| `name` | `String` | `unique=true` |
| `category_id` | `Category` | `@ManyToOne`, not null |
| `price` | `BigDecimal` | `nullable=false` |
| `discountPercentage` | `Integer` | Physical naming expected as `discount_percentage` |
| `weight` | `BigDecimal` | `nullable=false` |
| `materialType` | `String` | `nullable=false` |
| `skuCode` | `String` | `nullable=false`, `unique=true` |
| `shortDescription` | `String` | length 200 |
| `fullDescription` | `String` | `@Lob` |
| `gender` | `String` | Plain column |
| `averageRating` | `Double` | Plain column |
| `isAvailable` | `Boolean` | Plain column |
| `quantity` | `Integer` | Plain column |
| `availableStock` | `Integer` | Plain column |
| `image_url` | `String` | Explicit column name |
| `createdBy`, `updatedBy` | `String` | Physical naming expected as snake case |
| `created_at`, `updated_at` | `LocalDateTime` | Explicit timestamp columns |

Lifecycle hooks:

- `@PrePersist` sets `createdAt` and generates `skuCode` as `SKU-` plus eight uppercase UUID characters.
- `@PreUpdate` sets `updatedAt`.

### `orders` and `order_items`

`Order` fields include customer contact/address, order date/time, enum order status, delivery type, total/due amount, enum payment status, enum payment method, notes, created-by string, created-by role, timestamps, and child items.

`OrderItem` fields include order reference, scalar `productId`, quantity, and price.

Current order item cascade:

```text
Order.items -> cascade = CascadeType.ALL
```

`orphanRemoval` is not declared.

### `service_home` and `repair_service`

Both service request entities use generated sequence IDs, enum status values, created/updated metadata, and basic contact/request fields.

`ServiceHome` also has `ServiceType`.

## Migration Files

| Migration | Creates |
|---|---|
| `V1__create_admin_users_table.sql` | `admin_users` |
| `V2__create_users_table.sql` | `users` |
| `V3__create_category_table.sql` | `category` |
| `V4__create_products_table.sql` | `products` |
| `V5__create_offer_photo_table.sql` | `offer_photo` |
| `V6__create_order_and_order_product_ids_table.sql` | `orders`, `order_product_ids` |
| `V7__create_service_home_table.sql` | `service_home` |
| `V8__create_repair_service_table.sql` | `repair_service` |

## Migration and Entity Drift Observed

| Area | Migration Shape | Entity Shape |
|---|---|---|
| Products primary key | `products.id` | Entity ID field is `productIds`; physical column likely `product_ids` |
| Product category | `category VARCHAR(255)` | `@ManyToOne Category` with `category_id` |
| Product SKU/name | Migration has `sku_code NOT NULL UNIQUE`, `name UNIQUE` | Entity has `skuCode` unique/not-null and `name` unique |
| Orders created-by | Migration has `created_by_id BIGINT` and `created_by_role` | Entity has `createdBy String` and enum `createdByRole` |
| Order items | Migration creates join table `order_product_ids` | Entity maps child table `order_items` with quantity and price |
| Users email uniqueness | Migration does not mark email unique | Entity marks email unique |
| Category name uniqueness | Migration does not mark name unique | Entity marks name unique |
| Sequences | Migrations use `BIGSERIAL` or `SERIAL` | Entities declare named sequences such as `products_seq`, `orders_seq` |

## Repositories and Queries

### Admin and User

| Repository | Queries |
|---|---|
| `AdminRepository` | `findByEmail`, `findByRole` |
| `UsersRepo` | `findByEmail` |

### Category

| Repository Method | Query Behavior |
|---|---|
| `findByName` | Lookup by category name |
| `existsByName` | Duplicate detection |
| `canEnableShowOnHome` | Returns true when count of `showOnHome=true` categories is below 7 |

### Product

| Repository Method | Query Behavior |
|---|---|
| `findByName` | Lookup product by name |
| `findProductByGender` | JPQL lower-case gender equality |
| `findByCategory_NameIgnoreCaseAndIsAvailableTrue` | Category name equality and available=true |
| `findProductsByFilters` | Optional filters for category, price range, weight range, material type, gender, availability, available stock range |
| `getProductsByMaterialType` | JPQL lower-case material type equality |
| `findProductsUnderPrice` | `p.price <= :price` |
| `findProductsAbovePrice` | `p.price >= :price` |

### Orders

| Repository Method | Query Behavior |
|---|---|
| `findAllByOrderDate` | Orders for one date |
| `countOrdersByOrderDateBetween` | Monthly count query |
| `findAll(Pageable)` | Paginated list used for admin order list |

### Home and Repair Service

| Repository | Queries |
|---|---|
| `HomeServiceRepository` | `findAllByOrderByCreatedAtDesc`, `searchHomeServices` |
| `RepairRequestRepository` | `findAllByOrderByCreatedAtDesc`, `searchRepairRequests` |

The search queries use an `OR` pattern with null parameters:

```text
(:serviceId IS NULL OR s.serviceId = :serviceId)
OR (:name IS NULL OR LOWER(s.name) LIKE ...)
```

Because one side becomes true when its parameter is null, the query can match all rows for either numeric-only or name-only searches.

## Indexes and Constraints

Indexes explicitly observed in SQL migrations:

- Primary keys on all migration-created tables.
- Unique constraint on `admin_users.email`.
- Unique constraints on `products.name` and `products.sku_code`.
- Composite primary key on migration table `order_product_ids(order_id, product_id)`.
- Foreign keys from `order_product_ids` to `orders(id)` and `products(id)`.

Constraints explicitly observed in JPA annotations:

- `AdminUsers.email` unique and not null.
- `Users.email` unique.
- `Category.name` unique and not null.
- `Category.showOnHome` not null.
- `Category.status` not null.
- `Products.name` unique.
- `Products.category` not null.
- `Products.price`, `weight`, `materialType`, `skuCode` not null.
- `Products.skuCode` unique.

No explicit JPA `@Index` annotations were observed.

## Normalization Observations

| Area | Current Design |
|---|---|
| Product/category | Normalized in JPA through `category_id`; migration stores category as text |
| Orders/products | Order item stores scalar product ID and does not enforce a JPA relationship |
| Admin/user identity | Admins and users are separate tables with separate auth models |
| Services/users | Service requests store creator/email values as strings, not foreign keys |
| Offer photos | Fixed five URL columns in one table rather than separate rows |

## Transaction Boundaries

Transaction annotations are declared mostly at service layer:

| Service | Transaction Usage |
|---|---|
| `AdminServiceImp` | Login, password reset, edit, change password, register, offer update, delete use write transactions; reads use read-only transactions |
| `UserServiceImp` | Login and verify use write transactions |
| `CategoryServiceImp` | Reads use read-only; add/update/delete/upload use write transactions |
| `ProductServiceImp` | Reads use read-only; add/update/delete use write transactions |
| `OrderServiceImpl` | Create/update use write transactions; reads/list/count/invoice use read-only |
| `HomeServiceImp` | Reads use read-only; create/edit/delete use write transactions |
| `RepairRequestServiceImp` | Reads use read-only; create/edit/delete use write transactions |

DAO and repository classes do not declare their own transaction boundaries.

