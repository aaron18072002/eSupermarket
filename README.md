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
│   ├── catalog-service/          # Product, category, brand, supplier, and tag management
│   └── inventory-service/        # Stock and location management
├── database/                     # Database infrastructure and migrations
│   ├── docker-compose.yml        # PostgreSQL 16 container setup
│   ├── README.md                 # Database and multi-domain migration instructions
│   └── migrations/               # Domain-partitioned Flyway migration scripts
│       └── catalog/              # Catalog domain migrations (V1, V2, V3)
├── frontend/                     # Next.js web application
├── .gitignore                    # Global git ignore rules (Java & Node)
└── README.md                     # Project documentation
```

---

## 🛠️ Technology Stack

**Frontend:**
- **Framework:** Next.js (App Router) & React
- **Language:** TypeScript
- **Styling & Formatting:** Tailwind CSS, Prettier

**Backend (Spring Boot Microservices):**
- **Framework:** Spring Boot (v4.0.8)
- **Language:** Java 17
- **Database:** PostgreSQL 16 (via Spring Data JPA)
- **Database Migration:** Flyway (v11.14.1)
- **Communication:** Spring Cloud OpenFeign (v4.2.0)
- **Utilities:** Lombok (v1.18.32), MapStruct (v1.6.3) for DTO mapping
- **Build Tool:** Maven

---

## 🏗️ Core Business Domains & Services

### 1. Catalog Service (`backend/catalog-service`)
The **Catalog Service** manages the master catalog, categorization, branding, suppliers, marketing tags, and product specifications.

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

### 2. Inventory Service (`backend/inventory-service`)
Tracks physical warehouse/store stock across locations and handles reservations during checkout.

### 3. Frontend Web App (`frontend/`)
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
