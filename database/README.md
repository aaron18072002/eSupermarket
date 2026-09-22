# 🗄️ Database & Multi-Domain Migration Guide

This directory manages the local database infrastructure, Flyway migration scripts, and initialization tooling for all microservice domains across the **eSupermarket** platform.

---

## 📁 Directory Structure

```text
database/
├── docker-compose.yml          # Local PostgreSQL 16 container definition
├── local-init-data.sh          # Bash script to apply schema & seed data on demand
├── README.md                   # This developer guide
└── migrations/                 # Domain-partitioned Flyway migrations
    ├── catalog/                # Catalog Domain (catalog_db)
    │   ├── V1__init_catalog_schema.sql    # DDL: Tables, constraints, and indexes
    │   ├── V2__seed_reference_data.sql    # Seed: Categories, Brands, Suppliers, Tags, Groups
    │   └── V3__seed_sample_products.sql   # Seed: Sample Products, Attributes, Tags, Images
    ├── inventory/              # (Future) Inventory Domain (inventory_db)
    │   └── ...
    └── order/                  # (Future) Order Domain (order_db)
        └── ...
```

---

## ⚙️ Domain Connection Details

| Domain | Database | Port (Host) | Default User | Default Password | JDBC URL |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Catalog** | `catalog_db` | `5435` | `application` | `123456` | `jdbc:postgresql://localhost:5435/catalog_db` |
| **Inventory** *(Planned)* | `inventory_db` | TBD | `application` | `123456` | `jdbc:postgresql://localhost:5435/inventory_db` |

---

## 🚀 How to Run & Apply Migrations

### Step 1: Start the Database Container

From the `database/` directory:

```bash
docker compose up -d
```

Verify the container is healthy:

```bash
docker compose ps
```

---

### Step 2: Applying Migrations & Seeding Data (Manual On-Demand)

> **Important:** Domain applications do **NOT** automatically trigger database migrations on startup. Database initialization and migrations are triggered **manually on-demand** from this `database/` folder.

#### Primary Method: On-Demand via [`local-init-data.sh`](file:///D:/fe/eSupermarket/database/local-init-data.sh)
Run the script to initialize tables and seed data for the desired domain:

```bash
# 1. Initialize schema and seed data (V1 + V2 + V3) for catalog_db
./local-init-data.sh catalog

# 2. Apply only seed data (V2 + V3) to an existing database
./local-init-data.sh catalog --seed

# 3. Apply only schema DDL (V1)
./local-init-data.sh catalog --schema

# 4. Initialize all available domains at once
./local-init-data.sh all

# 5. Display help and available options
./local-init-data.sh --help
```

#### Alternative Method: Manual via `psql` / Docker Exec
If you prefer executing individual SQL files directly:

```bash
# Apply Catalog migrations to catalog_db
docker exec -i esupermarket-postgres-catalog-1 psql -U application -d catalog_db < migrations/catalog/V1__init_catalog_schema.sql
docker exec -i esupermarket-postgres-catalog-1 psql -U application -d catalog_db < migrations/catalog/V2__seed_reference_data.sql
docker exec -i esupermarket-postgres-catalog-1 psql -U application -d catalog_db < migrations/catalog/V3__seed_sample_products.sql
```

#### Alternative Method: Via Flyway CLI
If you use the standalone Flyway CLI:

```bash
flyway -url=jdbc:postgresql://localhost:5435/catalog_db \
       -user=application \
       -password=123456 \
       -locations=filesystem:./migrations/catalog \
       migrate
```

---

## 🦫 Testing & Inspecting Data with DBeaver

You can use **DBeaver** (or any database GUI like pgAdmin or DataGrip) to connect and inspect the data locally.

### 1. DBeaver Connection Settings
- **Connection Type:** `PostgreSQL`
- **Host:** `localhost`
- **Port:** `5435` *(mapped port in docker-compose.yml)*
- **Database:** `catalog_db`
- **Username:** `application`
- **Password:** `123456`
- **SSL:** `Disabled` (default for local dev)

> **Tip:** Click **"Test Connection ..."** in DBeaver to confirm connectivity before opening.

---

### 2. Verification SQL Queries for DBeaver

After running `./local-init-data.sh catalog`, open a new SQL editor in DBeaver and run these verification queries:

#### A. Master Catalog View (Connects All Catalog Information in 1 Query)
> **Tip:** You can also open [`test-catalog-queries.sql`](file:///D:/fe/eSupermarket/database/test-catalog-queries.sql) directly in DBeaver!

```sql
SELECT 
    p.id AS product_id,
    p.sku,
    p.barcode,
    p.name AS product_name,
    p.price,
    CONCAT(p.package_size, ' ', p.unit_of_measure) AS package,
    COALESCE(parent_cat.name || ' > ', '') || cat.name AS category_path,
    b.name AS brand,
    b.country AS brand_origin,
    s.name AS supplier,
    COALESCE(pg.name, 'None') AS product_group,
    COALESCE(STRING_AGG(DISTINCT t.name, ', '), 'None') AS tags,
    COALESCE(STRING_AGG(DISTINCT pa.attr_key || ': ' || pa.attr_value, ' | '), 'None') AS attributes,
    COALESCE(STRING_AGG(DISTINCT pi.image_url, ', '), 'None') AS images,
    p.created_at
FROM PRODUCTS p
LEFT JOIN CATEGORIES cat ON p.category_id = cat.id
LEFT JOIN CATEGORIES parent_cat ON cat.parent_id = parent_cat.id
LEFT JOIN BRANDS b ON p.brand_id = b.id
LEFT JOIN SUPPLIERS s ON p.supplier_id = s.id
LEFT JOIN PRODUCT_GROUPS pg ON p.group_id = pg.id
LEFT JOIN PRODUCT_TAGS pt ON p.id = pt.product_id
LEFT JOIN TAGS t ON pt.tag_id = t.id
LEFT JOIN PRODUCT_ATTRIBUTES pa ON p.id = pa.product_id
LEFT JOIN PRODUCT_IMAGES pi ON p.id = pi.product_id
GROUP BY 
    p.id, p.sku, p.barcode, p.name, p.price, p.package_size, p.unit_of_measure,
    cat.name, parent_cat.name, b.name, b.country, s.name, pg.name, p.created_at
ORDER BY p.sku;
```

#### B. Check Record Counts Across All Tables
```sql
SELECT 'BRANDS' AS table_name, count(*) AS total FROM BRANDS
UNION ALL
SELECT 'CATEGORIES', count(*) FROM CATEGORIES
UNION ALL
SELECT 'SUPPLIERS', count(*) FROM SUPPLIERS
UNION ALL
SELECT 'PRODUCT_GROUPS', count(*) FROM PRODUCT_GROUPS
UNION ALL
SELECT 'TAGS', count(*) FROM TAGS
UNION ALL
SELECT 'PRODUCTS', count(*) FROM PRODUCTS
UNION ALL
SELECT 'PRODUCT_TAGS', count(*) FROM PRODUCT_TAGS
UNION ALL
SELECT 'PRODUCT_ATTRIBUTES', count(*) FROM PRODUCT_ATTRIBUTES
UNION ALL
SELECT 'PRODUCT_IMAGES', count(*) FROM PRODUCT_IMAGES;
```

#### B. Inspect Products with Details (Category, Brand, Supplier)
```sql
SELECT 
    p.sku,
    p.barcode,
    p.name AS product_name,
    p.price,
    p.package_size,
    p.unit_of_measure,
    c.name AS category,
    b.name AS brand,
    s.name AS supplier,
    pg.name AS product_group
FROM PRODUCTS p
LEFT JOIN CATEGORIES c ON p.category_id = c.id
LEFT JOIN BRANDS b ON p.brand_id = b.id
LEFT JOIN SUPPLIERS s ON p.supplier_id = s.id
LEFT JOIN PRODUCT_GROUPS pg ON p.group_id = pg.id;
```

#### C. Inspect Category Tree (Parent $\rightarrow$ Children)
```sql
SELECT 
    parent.name AS parent_category,
    child.name AS subcategory
FROM CATEGORIES child
LEFT JOIN CATEGORIES parent ON child.parent_id = parent.id
ORDER BY parent_category NULLS FIRST, subcategory;
```

#### D. Inspect Product Tags & Attributes
```sql
-- View all tags assigned to products
SELECT 
    p.name AS product,
    t.name AS tag
FROM PRODUCT_TAGS pt
JOIN PRODUCTS p ON pt.product_id = p.id
JOIN TAGS t ON pt.tag_id = t.id;

-- View all custom attributes per product
SELECT 
    p.name AS product,
    pa.attr_key,
    pa.attr_value
FROM PRODUCT_ATTRIBUTES pa
JOIN PRODUCTS p ON pa.product_id = p.id;
```

---

## 📏 Standards for Adding Future Domains

When creating a new domain (e.g., `inventory-service`, `order-service`):

1. **Create a Dedicated Folder:**
   Add `database/migrations/<domain-name>/` (e.g., `database/migrations/inventory/`).
2. **Independent Versioning:**
   Each domain manages its own independent migration sequence starting from `V1`:
   - `V1__init_<domain>_schema.sql`
   - `V2__seed_<domain>_reference_data.sql`
3. **Copy to Microservice Resources:**
   Mirror the domain migration scripts into `backend/<service-name>/src/main/resources/db/migration/`.
4. **Database Separation:**
   Ensure each microservice connects to its own database (`catalog_db`, `inventory_db`, etc.) to enforce strict domain boundaries.
5. **Update `local-init-data.sh`:**
   Add a case for the new domain in the `local-init-data.sh` script.

---

## 🔄 Resetting the Database

To wipe all data and start completely fresh:

```bash
# Stop container and remove the persistent volume
docker compose down -v

# Recreate the container
docker compose up -d

# Re-apply migrations and seed data
./local-init-data.sh catalog
```
