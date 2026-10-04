# 🛒 eSupermarket Microservices Platform

Welcome to the **eSupermarket** project! This is a modern, scalable e-commerce platform built to explore a polyglot microservices architecture. It combines a fast, modern frontend with a robust, enterprise-grade Java backend.

---

## 🎯 Project Purpose

The main goal of this project is to build a complex supermarket system, inspired by real-world enterprise retail platforms like Lidl.

Instead of building one giant application (a monolith), this project uses a **Polyglot Monorepo** approach. The user interface is built with modern JavaScript/TypeScript tools, while the core business logic is divided into small, independent Java Spring Boot microservices.

---

## 📁 Workspace Structure

This project keeps frontend, backend microservices, and database infrastructure in the same repository:

```text
eSupermarket/
├── backend/                      # Java Spring Boot Microservices
│   ├── api-gateway/              # Unified entry point, reverse proxy, CORS, and routing
│   ├── eureka-server/            # Netflix Eureka Service Registry & Discovery
│   ├── catalog-service/          # Product, category, brand, supplier, and tag management
│   ├── inventory-service/        # Stock and location management
│   ├── user-service/             # Authentication, user profiles, and JWT tokens
│   └── order-service/            # Orders, SePay VietQR payments, and webhook processing
├── database/                     # Database infrastructure and migrations
│   ├── docker-compose.yml        # PostgreSQL 16 container setup
│   ├── README.md                 # Database and multi-domain migration instructions
│   └── migrations/               # Domain-partitioned Flyway migration scripts
│       ├── catalog/              # Catalog domain migrations (V1, V2, V3)
│       └── user/                 # User domain migrations (V1, V2)
├── frontend/                     # Next.js web application
├── .gitignore                    # Global git ignore rules (Java & Node)
├── FLOW.md                       # Visual end-to-end request flow & architecture guide
└── README.md                     # Project documentation
```

---

## 🛠️ Technology Stack

**Frontend:**
- **Framework:** Next.js (App Router) & React
- **Language:** TypeScript
- **Styling & Formatting:** Tailwind CSS, Prettier

**Backend (Spring Boot Microservices):**
- **Framework:** Spring Boot (v3.3.4 & v4.0.8)
- **Language:** Java 17
- **API Gateway:** Spring Cloud Gateway (v2023.0.3, Reactive / Netty)
- **Database:** PostgreSQL 16 (via Spring Data JPA)
- **Database Migration:** Flyway (v11.14.1)
- **Communication:** Spring Cloud OpenFeign (v4.2.0)
- **Utilities:** Lombok (v1.18.32), MapStruct (v1.6.3) for DTO mapping
- **API Documentation:** Springdoc OpenAPI v3 (v2.8.5) & Swagger UI
- **Build Tool:** Maven

---

## 📖 API Documentation & Swagger UI

Interactive Swagger UI and OpenAPI v3 specifications are available for each downstream microservice directly on their assigned ports:

| Service | Port | Swagger UI URL | OpenAPI JSON Spec | Description |
| :--- | :--- | :--- | :--- | :--- |
| **Catalog Service** | `8081` | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) | [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs) | Products, categories, brands, suppliers, tags, and bundles |
| **User Service** | `8082` | [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html) | [http://localhost:8082/v3/api-docs](http://localhost:8082/v3/api-docs) | Authentication (JWT), user registration, and profile management |
| **Order Service** | `8083` | [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html) | [http://localhost:8083/v3/api-docs](http://localhost:8083/v3/api-docs) | Orders, SePay VietQR payment integration, webhook processing, and analytics |
| **API Gateway** | `8080` | *N/A (Reverse Proxy)* | *N/A* | Gateway routing edge; actuator: `/actuator/gateway/routes` |
| **Eureka Server** | `8761` | *N/A (Service Registry)* | *N/A* | Eureka dashboard: [http://localhost:8761](http://localhost:8761) |

> [!NOTE]
> Swagger UI endpoints must be accessed directly via the respective service ports (`8081`, `8082`, or `8083`). The API Gateway (`8080`) only routes business endpoints matching `/api/v1/**`.

---

## 🏗️ Core Business Domains & Services

### 1. Catalog Service (`backend/catalog-service`)
The **Catalog Service** manages the master catalog, categorization, branding, suppliers, marketing tags, and product specifications.

- **Port:** `8081`
- **Swagger UI:** [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **OpenAPI JSON Spec:** [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)

#### Managed Database Tables:
| Table | Description |
| :--- | :--- |
| `BRANDS` | Brands and manufacturers (e.g., Danone, Barilla, Coca-Cola) |
| `CATEGORIES` | Hierarchical department and category tree (parent-child hierarchy) |
| `SUPPLIERS` | Wholesale suppliers and distributors |
| `PRODUCT_GROUPS` | Bundles and variant collections (e.g., Breakfast Essentials) |
| `TAGS` | Marketing, dietary, and promotional tags (e.g., Organic, Vegan, Best Seller) |
| `PRODUCTS` | Core product items with SKU, barcode, price, and package sizing |
| `PRODUCT_TAGS` | Many-to-many join table between products and tags |
| `PRODUCT_ATTRIBUTES` | Dynamic key-value specifications per product (e.g., Fat Content, Storage) |
| `PRODUCT_IMAGES` | Storefront image URLs collection per product |

#### API Conventions & Method Prefix Standards:
All controllers and service interfaces adhere to strict prefix standards:
- **`create...`**: `createProduct`, `createCategory`, `createBrand`, `createSupplier`, `createTag`, `createProductGroup`
- **`read...`**: `readProductById`, `readProductBySku`, `readProductByBarcode`, `readAllProducts`, `readProductsByCategory`, `readBrandById`, `readAllBrands`, etc.
- **`search...`**: `searchProducts(query)`
- **`update...`**: `updateProduct`, `updateCategory`, `updateBrand`, etc.
- **`delete...`**: `deleteProductById`, `deleteCategoryById`, etc.

#### REST Endpoints:
- `/api/v1/products` - Product management and search
- `/api/v1/categories` - Category tree and hierarchy
- `/api/v1/brands` - Brand management
- `/api/v1/suppliers` - Supplier management
- `/api/v1/tags` - Marketing and dietary tags
- `/api/v1/product-groups` - Product groupings and bundles

#### Database Migrations (Flyway):
- `V1__init_catalog_schema.sql` - Full DDL for all 9 tables, indexes, and constraints.
- `V2__seed_reference_data.sql` - Master reference data for categories, brands, suppliers, tags, and groups.
- `V3__seed_sample_products.sql` - Realistic sample supermarket products with attributes, tags, and images.

---

### 2. User Service (`backend/user-service`)
The **User Service** manages customer authentication, registration, JWT token generation/validation, password hashing, and user profile management.

- **Port:** `8082`
- **Swagger UI:** [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
- **OpenAPI JSON Spec:** [http://localhost:8082/v3/api-docs](http://localhost:8082/v3/api-docs)
- **Security Scheme:** Bearer Authentication (JWT token)

#### Managed Database Tables:
| Table | Description |
| :--- | :--- |
| `USERS` | Customer credentials, profile info, and account status |
| `REFRESH_TOKENS` | Rotatable refresh tokens with expiry tracking |

#### REST Endpoints:
- `/api/v1/auth/register` - New user registration
- `/api/v1/auth/login` - User login and token issuance (Access & Refresh tokens)
- `/api/v1/auth/refresh-token` - Refresh expired access token
- `/api/v1/users/me` - Read current user profile (requires Bearer token)
- `/api/v1/users/me` (PUT) - Update profile info
- `/api/v1/users/me/change-password` - Change password

#### Database Migrations (Flyway):
- `V1__init_user_schema.sql` - Full DDL for USERS and REFRESH_TOKENS tables.
- `V2__seed_admin_user.sql` - Default administrator and test accounts.

---

### 3. Order Service (`backend/order-service`)
The **Order Service** manages order checkout, automated VietQR dynamic payment generation via **SePay**, real-time payment webhook verification with **HMAC-SHA256**, order status tracking, and revenue analytics for admin dashboards.

- **Port:** `8083`
- **Swagger UI:** [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)
- **OpenAPI JSON Spec:** [http://localhost:8083/v3/api-docs](http://localhost:8083/v3/api-docs)
- **Payment Gateway:** SePay Open Banking API (TPBank VietQR dynamic QR code with order code matching)
- **Webhook Security:** HMAC-SHA256 signature verification via request header or body parameter

#### Managed Database Tables:
| Table | Description |
| :--- | :--- |
| `ORDERS` | Master orders table storing customer reference, total amount, payment method, payment status, VietQR URL, paid timestamp, and shipping address |
| `ORDER_ITEMS` | Line items in each order recording product ID, SKU, product name, quantity, unit price, and subtotal |

#### REST Endpoints:
- `/api/v1/orders` (POST) - Create a new order (generates VietQR code automatically if payment method is `VIETQR`)
- `/api/v1/orders/{id}` (GET) - Get order details by UUID
- `/api/v1/orders/code/{orderCode}` (GET) - Get order details by unique order code (e.g. `ORD-ABC12345`)
- `/api/v1/orders/user/{userId}` (GET) - List order history by customer ID
- `/api/v1/orders/dashboard/summary` (GET) - Admin analytics: total revenue, order count by status, today's revenue & orders
- `/api/v1/orders/sepay-webhook` (POST) - Public SePay webhook receiver verifying HMAC-SHA256 signature and automatically marking matching order as `PAID`

---

### 4. API Gateway (`backend/api-gateway`)
The **API Gateway** serves as the single entry point for all client applications (such as Next.js web storefront and mobile apps). It handles traffic routing, global CORS policies, centralized logging, and acts as the reverse proxy for internal microservices.

- **Port:** `8080`
- **Core Technology:** Spring Cloud Gateway (reactive Netty)
- **Active Routes (Dynamic Load Balancing via Eureka):**
  - `/api/v1/products/**`, `/api/v1/categories/**`, `/api/v1/brands/**`, etc. ➔ `lb://catalog-service`
  - `/api/v1/auth/**`, `/api/v1/users/**` ➔ `lb://user-service`
  - `/api/v1/orders/**` ➔ `lb://order-service`
- **Features:**
  - Dynamic service discovery and client-side load balancing via Netflix Eureka.
  - Pure reverse proxy and routing without request header mutation.
  - Stateless edge JWT verification (validates signature and expiration, rejects invalid tokens with 401 early).
  - Open API whitelist including `/api/v1/orders/sepay-webhook` for secure direct webhook delivery.
  - Global CORS pre-configured for `http://localhost:3000` (Next.js).
  - Global reactive logging filter tracking method, path, status, and latency.
  - Health and route inspection endpoints via Spring Boot Actuator (`/actuator/gateway/routes`).

---

### 5. Eureka Server (`backend/eureka-server`)
Central Service Registry and Discovery server for the polyglot microservices platform. Microservices self-register on startup and send periodic heartbeats to maintain active routing tables.

- **Port:** `8761`
- **Dashboard:** `http://localhost:8761`
- **Core Technology:** Spring Cloud Netflix Eureka Server

---

### 6. Inventory Service (`backend/inventory-service`)
Tracks physical warehouse/store stock across locations and handles reservations during checkout.

### 7. Frontend Web App (`frontend/`)
Customer-facing web application built with Next.js App Router for browsing catalogs, searching products, and shopping cart operations.

---

## 🚀 Quick Start

1. **Start Database Container:**
   ```bash
   cd database
   docker compose up -d
   ```

2. **Initialize Database & Seed Data (Manual On-Demand):**
   ```bash
   ./local-init-data.sh catalog
   ```

3. **Run Catalog Service:**
   ```bash
   cd ../backend/catalog-service
   mvn spring-boot:run
   ```

4. **Run API Gateway:**
   ```bash
   cd ../backend/api-gateway
   mvn spring-boot:run
   ```

5. **Verify Routing via Gateway:**
   ```bash
   curl http://localhost:8080/api/v1/products
   ```
