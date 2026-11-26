# 📚 eSupermarket Flow & Architecture Documentation

This directory stores the living architecture and end-to-end flow documentation for **eSupermarket**. It stays outside `frontend/`, `backend/`, and `database/` so runtime builds remain untouched.

---

## 🏛️ Backend System Architecture

* **[FLOW.md](./FLOW.md)** — General backend module topology, Eureka service discovery (`:8761`), API Gateway `lb://` routing (`:8080`), shared SHA-256 JWT trust, OpenFeign East-West communication, and database-per-service isolation.

---

## 🔀 Page & Feature End-to-End Flows (`Browser -> FE -> BE -> DB`)

| Route / Feature | Flow Document | Frontend Entry | Backend Service & DB |
| :--- | :--- | :--- | :--- |
| **`/login`** (User Sign In) | [loginFlow.md](./flows/loginFlow.md) | `frontend\src\app\login\page.tsx` | `api-gateway` (`:8080`) → `user-service` (`:8082`) → `user_db` (`:5436`) |

---

## 🗄️ Downstream Backend Database Schemas (`document/schemas/`)

| Downstream Module | Schema Document | PostgreSQL DB & Port | Tables |
| :--- | :--- | :--- | :--- |
| **`catalog-service`** (`:8081`) | [catalogSchema.md](./schemas/catalogSchema.md) | `catalog_db` (`:5435`) | `BRANDS`, `CATEGORIES`, `SUPPLIERS`, `PRODUCT_GROUPS`, `TAGS`, `PRODUCTS`, `PRODUCT_TAGS`, `PRODUCT_ATTRIBUTES`, `PRODUCT_IMAGES` |
| **`user-service`** (`:8082`) | [userSchema.md](./schemas/userSchema.md) | `user_db` (`:5436`) | `users`, `user_roles`, `refresh_tokens` |
| **`order-service`** (`:8083`) | [orderSchema.md](./schemas/orderSchema.md) | `order_db` (`:5437`) | `orders`, `order_items` |
| **`inventory-service`** (`:8084`) | [inventorySchema.md](./schemas/inventorySchema.md) | `inventory_db` (`:5438`) | `inventories` |

---

## 🛠️ Documentation Maintenance Rule

This folder is governed by [.agent/skills/flow-documentation/SKILL.md](../.agent/skills/flow-documentation/SKILL.md) and [GEMINI.md](../GEMINI.md). Whenever a page, frontend service call, API Gateway route, backend controller/service, or database migration changes, the corresponding document here must be updated in sync.
