# 🗄️ Database & Multi-Domain Migration Guide (Điện Máy Xanh Edition)

This directory manages the local database infrastructure, Flyway migration scripts, and initialization tooling for all microservice domains across the **Điện Máy Xanh / eSupermarket** platform.

---

## 📁 Directory Structure

```text
database/
├── docker-compose.yml          # Local PostgreSQL 16 container definition (Ports 5435-5438)
├── local-init-data.ps1         # PowerShell script for local database initialization
├── test-catalog-queries.sql    # DBeaver / psql verification queries (Master catalog, tree, spec chips)
├── README.md                   # This developer guide
└── migrations/                 # Domain-partitioned Flyway migrations
    ├── catalog/                # Catalog Domain (catalog_db) - Điện Máy Xanh retail data
    │   ├── V1__init_catalog_schema.sql    # DDL: Tables, constraints, and indexes
    │   ├── V2__seed_reference_data.sql    # Seed: Electronics Brands, Category Hierarchy, Badges
    │   └── V3__seed_sample_products.sql   # Seed: 20 Flagship Products, Specs, Tags, Images (VND)
    ├── user/                   # User Domain (user_db)
    │   ├── V1__init_user_schema.sql       # DDL: Users, roles, refresh tokens
    │   └── V2__seed_admin_user.sql        # Seed: Default admin and user accounts
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
| **User** | `user_db` | `5436` | `application` | `123456` | `jdbc:postgresql://localhost:5436/user_db` |
| **Order** | `order_db` | `5437` | `application` | `123456` | `jdbc:postgresql://localhost:5437/order_db` |
| **Inventory** | `inventory_db` | `5438` | `application` | `123456` | `jdbc:postgresql://localhost:5438/inventory_db` |

---

## 🚀 How to Run & Apply Migrations

### Step 1: Start the Database Containers

From the `database/` directory:

```bash
docker compose up -d
```

Verify that all containers are healthy:

```bash
docker compose ps
```

---

### Step 2: Applying Migrations & Seeding Data (Manual On-Demand)

> **Important:** Backend microservices do **NOT** automatically wipe or migrate databases on startup. Database initialization and migrations are triggered **manually on-demand** from this `database/` folder.

#### PowerShell Initialization: [`local-init-data.ps1`](file:///C:/Projects/eSupermarket/database/local-init-data.ps1)
Run directly inside PowerShell:

```powershell
# 1. Fresh rebuild (reset schema and run all V1, V2, V3 migrations for catalog_db)
.\local-init-data.ps1 catalog
.\local-init-data.ps1 user

# 2. Safe update: apply new migrations WITHOUT wiping existing data
.\local-init-data.ps1 catalog --update
.\local-init-data.ps1 user --update

# 3. Apply only seed data to an existing database
.\local-init-data.ps1 catalog --seed

# 4. Apply only schema DDL files
.\local-init-data.ps1 catalog --schema

# 5. Initialize all available domains at once
.\local-init-data.ps1 all
```

#### Alternative Method: Manual via Docker Exec
```bash
docker exec -i esupermarket-postgres-catalog-1 psql -U application -d catalog_db < migrations/catalog/V1__init_catalog_schema.sql
docker exec -i esupermarket-postgres-catalog-1 psql -U application -d catalog_db < migrations/catalog/V2__seed_reference_data.sql
docker exec -i esupermarket-postgres-catalog-1 psql -U application -d catalog_db < migrations/catalog/V3__seed_sample_products.sql
```

---

## 🛍️ Điện Máy Xanh Retail Domain Specifications

The catalog domain has been tailored to reflect the exact product hierarchy and visual attributes of **Điện Máy Xanh**:

### 1. Categories Hierarchy
- **Tivi, Loa, Âm Thanh** (`Smart Tivi 4K`, `Loa Thanh & Dàn Karaoke`)
- **Tủ Lạnh, Tủ Đông, Tủ Mát** (`Tủ Lạnh Inverter`, `Tủ Lạnh Side By Side`, `Tủ Đông & Tủ Mát`)
- **Máy Giặt, Máy Sấy** (`Máy Giặt Cửa Ngang`, `Máy Giặt Cửa Trên`, `Máy Sấy Quần Áo`)
- **Máy Lạnh, Điều Hòa** (`Máy Lạnh 1 HP - 1.5 HP`, `Máy Lạnh 2 HP Trở Lên`)
- **Gia Dụng, Thiết Bị Bếp** (`Nồi Cơm Điện & Áp Suất`, `Nồi Chiên Không Dầu`, `Bếp Từ`, `Bình Đun Siêu Tốc`)
- **Máy Lọc Nước, Làm Mát** (`Máy Lọc Nước RO`, `Quạt Điều Hòa & Quạt Đứng`)
- **Điện Thoại, Laptop, Phụ Kiện** (`Điện Thoại Di Động`, `Laptop & Máy Tính Bảng`)

### 2. Major Brands
- **Samsung**, **Sony**, **LG**, **Panasonic**, **Toshiba**, **Daikin**, **Casper**, **Sunhouse**, **Kangaroo**, **Philips**, **Apple**, **Xiaomi**, **Electrolux**, **Sharp**, **Sanaky**, **Asus**.

### 3. Promotional Badges (Design System Tokens)
- `Trả góp 0%`, `Giá rẻ quá`, `Bán chạy`, `Giảm sốc`, `Mới ra mắt`, `Tiết kiệm điện`, `Miễn phí lắp đặt`, `Tặng quà VIP`.

### 4. Spec Attribute Chips
- Crucial technical specifications per product (e.g., `55 inch`, `4K (Ultra HD)`, `Inverter`, `9.0 kg`, `1 HP`, `Rapid Air`) powering the retail spec chips on product cards and technical tables on the PDP.

### 5. Pricing
- Realistic Vietnamese Đồng (VND) pricing matching genuine retail price levels (e.g. `10,490,000 ₫`, `16,490,000 ₫`, `890,000 ₫`, `25,990,000 ₫`).

---

## 🦫 Testing & Inspecting Data with DBeaver

You can connect to `catalog_db` via **DBeaver** or **pgAdmin**:

- **Host:** `localhost`
- **Port:** `5435`
- **Database:** `catalog_db`
- **Username:** `application`
- **Password:** `123456`

Open and run [`test-catalog-queries.sql`](file:///C:/Projects/eSupermarket/database/test-catalog-queries.sql) to inspect the catalog, verify category trees, check spec chips, and preview the JSON responses.
