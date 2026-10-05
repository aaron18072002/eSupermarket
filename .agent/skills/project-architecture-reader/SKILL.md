---
name: project-architecture-reader
description: >-
  Inspects and analyzes the eSupermarket workspace layout, reading FLOW.md for
  end-to-end communication traffic (North-South and East-West) and README.md for
  microservices portfolios, port mappings, and database configurations.
---

# 🏛️ Project Architecture & Flow Reader Skill

This skill guides the agent through inspecting the **eSupermarket** polyglot monorepo structure, understanding request lifecycles via [FLOW.md](file:///D:/fe/eSupermarket/FLOW.md), reviewing service documentation in [README.md](file:///D:/fe/eSupermarket/README.md), and adhering to [coding-standards.md](file:///D:/fe/eSupermarket/.agent/rules/coding-standards.md).

---

## 📌 Core Documentation Index

Whenever you onboard, modify services, or debug communications, read these root files first:
1. **[FLOW.md](file:///D:/fe/eSupermarket/FLOW.md)**: Visual sequence diagrams illustrating North-South traffic (Next.js ➔ API Gateway ➔ Microservices) and East-West traffic (OpenFeign direct calls), Eureka service discovery, and Gateway JWT authorization pipelines.
2. **[README.md](file:///D:/fe/eSupermarket/README.md)**: System overview, service descriptions, database schemas, Swagger UI documentation links, and quick-start instructions.
3. **[.agent/rules/coding-standards.md](file:///D:/fe/eSupermarket/.agent/rules/coding-standards.md)**: Strict architectural conventions (Controller prefixes, Record DTOs, explicit lambdas, fail-fast configuration without backup values, 120-char line limit, and `@BatchSize` optimizations).

---

## 🗺️ Monorepo Directory Layout

```text
eSupermarket/
├── .agent/
│   ├── rules/
│   │   └── coding-standards.md   # Platform architectural rules and coding standards
│   └── skills/
│       └── project-architecture-reader/
│           └── SKILL.md          # This architecture onboarding skill
├── backend/                      # Java Spring Boot Microservices
│   ├── eureka-server/            # Service Registry (Port 8761)
│   ├── api-gateway/              # Reverse Proxy & Edge Router (Port 8080)
│   ├── catalog-service/          # Master Catalog & Product Management (Port 8081)
│   ├── user-service/             # Authentication & Profiles (Port 8082)
│   ├── order-service/            # Orders, VietQR & Analytics (Port 8083)
│   └── inventory-service/        # Stock Tracking & Reservations (Port 8084)
├── database/                     # PostgreSQL Docker & Flyway Migrations
│   ├── docker-compose.yml        # Multi-database container cluster (5435-5438)
│   ├── README.md                 # Migration and database management guide
│   └── migrations/               # Domain Flyway scripts (catalog, user, order, inventory)
├── frontend/                     # Next.js App Router Web Storefront (Port 3000)
├── FLOW.md                       # Architecture & Request Flow Sequence Diagrams
├── GEMINI.md                     # Root workspace guidelines
└── README.md                     # Comprehensive platform documentation
```

---

## 🧭 Step-by-Step Onboarding Workflow

When asked to investigate, extend, or debug any part of the eSupermarket platform, execute these steps:

### Step 1: Read System Port & Discovery Mapping
Check active ports and service names before configuring any inter-service communication:
- **Registry:** `eureka-server` (Port `8761`)
- **Gateway:** `api-gateway` (Port `8080`)
- **Catalog:** `catalog-service` (Port `8081`, DB Port `5435`, `catalog_db`)
- **User:** `user-service` (Port `8082`, DB Port `5436`, `user_db`)
- **Order:** `order-service` (Port `8083`, DB Port `5437`, `order_db`)
- **Inventory:** `inventory-service` (Port `8084`, DB Port `5438`, `inventory_db`)
- **Frontend:** Next.js Storefront (Port `3000`)

### Step 2: Trace Communication Flow in [FLOW.md](file:///D:/fe/eSupermarket/FLOW.md)
Distinguish between the two types of network traffic:
1. **North-South Traffic (Edge to Microservice):**
   - Client (`localhost:3000`) sends request to `http://localhost:8080/api/v1/...`.
   - API Gateway checks CORS, logs request, and validates JWT Bearer tokens for mutating routes (`POST`, `PUT`, `DELETE`).
   - API Gateway resolves target service address dynamically from Eureka (`lb://<service-name>`).
   - Forwarded request preserves original headers (e.g., `Authorization: Bearer <token>`).
2. **East-West Traffic (Microservice to Microservice):**
   - Internal microservices communicate **directly** via Spring Cloud OpenFeign interfaces using Eureka service names (e.g., `@FeignClient(name = "catalog-service")`).
   - East-West requests **bypass** the API Gateway for minimum latency and zero edge bottlenecks.

### Step 3: Inspect Service Contracts in [README.md](file:///D:/fe/eSupermarket/README.md)
Review each microservice's:
- Data models and database table mappings.
- Public vs. secured REST endpoints.
- Direct Swagger UI documentation (`http://localhost:<PORT>/swagger-ui.html`).
- External payment gateway integrations (e.g., SePay VietQR webhook handling).

### Step 4: Validate Against [coding-standards.md](file:///D:/fe/eSupermarket/.agent/rules/coding-standards.md)
Verify that any proposed code adheres strictly to:
- **Method Prefix Standards:** `create...`, `read...`, `update...`, `delete...`, `search...`.
- **DTO Immutability:** DTOs must be Java `record` classes.
- **Explicit Lambdas:** Strictly **no method references** (use `item -> item.getName()`, not `Item::getName`).
- **Fail-Fast Configuration:** Absolutely **no backup default values** (forbidden: `@Value("${app.secret:default}")` or `${VAR:default}`).
- **Line Length:** Maximum 120 characters per line in Java and TypeScript.
- **Security & RBAC:** Constant-time token checks, SHA-256 derived keys, `ROLE_ADMIN` protection on administrative endpoints.
- **ORM Optimization:** `@BatchSize(size = 25)` on 1-to-many JPA associations to prevent N+1 queries.

---

## ⚡ Quick Architecture Verification Commands

To verify services and databases during development:

```powershell
# 1. Check PostgreSQL containers status
docker compose -f database/docker-compose.yml ps

# 2. Build entire backend suite
mvn clean compile -DskipTests

# 3. Test specific microservice
cd backend/order-service; mvn test
cd backend/inventory-service; mvn test
```
