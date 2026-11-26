---
name: flow-documentation
description: >-
  Creates and maintains end-to-end page and feature flow documentation inside document/flows/*.md,
  downstream database schema relationships inside document/schemas/*.md, keeps document/README.md
  indexed, and maintains backend inter-module architecture in document/FLOW.md using terminal-safe
  Unicode box art and zero-failure Mermaid diagrams.
---

# 📘 Flow Documentation Maintenance Skill

This skill standardizes how architectural and page-level flows are created and kept synchronized in `document/` (at the monorepo root alongside `backend/`, `frontend/`, and `database/`).

---

## 📁 Directory Structure

```text
eSupermarket/
├── .agent/skills/flow-documentation/
│   └── SKILL.md                  # This documentation workflow skill
├── backend/                      # Spring Boot microservices
├── database/                     # PostgreSQL Docker & Flyway migrations
├── document/                     # Living architecture, flow & schema docs
│   ├── README.md                 # Master index of backend, flows & schemas
│   ├── FLOW.md                   # Backend inter-module communication only
│   ├── flows/                    # One file per Next.js route or user journey
│   │   └── loginFlow.md          # /login end-to-end flow (Browser -> DB)
│   └── schemas/                  # One file per downstream backend database
│       ├── catalogSchema.md      # catalog-service (catalog_db :5435)
│       ├── userSchema.md         # user-service (user_db :5436)
│       ├── orderSchema.md        # order-service (order_db :5437)
│       └── inventorySchema.md    # inventory-service (inventory_db :5438)
└── frontend/                     # Next.js App Router storefront
```

---

## 🔔 When to Create or Update Flow Docs (Mandatory Triggers)

Update the matching `document/flows/<feature>Flow.md` file (and `document/README.md`) whenever a task introduces or changes any of the following:

1. **New or Updated Frontend Route:** Creating or modifying `frontend/src/app/**/page.tsx` or interactive leaf components that initiate API calls (`"use client"`).
2. **Frontend Service or Caching Changes:** Adding/updating functions in `frontend/src/services/*.ts`, `localStorage` auth keys, or Next.js ISR `revalidate` / cache `tags`.
3. **API Gateway Changes:** Modifying routes, CORS rules, `RouterValidator`, or `AuthenticationFilter` in `backend/api-gateway/`.
4. **Backend Controller / Service / OpenFeign Changes:** Adding/modifying `@RestController` endpoints, `@Service` workflows, JWT claim structures, `@FeignClient` calls, or error mappings.
5. **Database Schema Changes:** Adding/modifying Flyway migrations in `database/migrations/` or JPA entity relationships/indexes in `backend/*/model/*.java`. Update both the matching `document/schemas/<module>Schema.md` (and `document/README.md`) and any affected `document/flows/<feature>Flow.md`.

Also update [document/FLOW.md](../../../document/FLOW.md) whenever **backend inter-module communication** changes (new microservice, new Eureka registration, new Gateway `lb://` route, shared security trust, or new OpenFeign East-West link).

> [!NOTE]
> **When to Skip:** Pure styling tweaks (Tailwind colors/spacing), copy edits, or internal helper refactors (e.g., formatting functions that do not alter data or request flow) do **not** require updating flow docs.

---

## 📝 Standard Template for `document/flows/<feature>Flow.md`

Every page/feature flow file uses **camelCase** naming ending in `Flow.md` (e.g., `loginFlow.md`, `homeCatalogFlow.md`, `productDetailFlow.md`) and must include these 6 sections:

### 1. Header & Quick Metadata Table
State the route URL, Next.js `page.tsx`, client interactive leaf, API Gateway endpoint, downstream microservice, and database tables.

### 2. Terminal-Safe Unicode Box Flowchart (`< 60` columns)
Include a clean box-drawing overview (`┌`, `─`, `┐`, `│`, `└`, `┘`, `▼`) strictly under **60 columns** wide for `agy` / PowerShell CLI readability.

### 3. Mermaid End-to-End Flowchart (`flowchart TD`)
Follow all rules from [powershell-response-standards](../powershell-response-standards/SKILL.md):
- Vertical orientation (`flowchart TD`)
- Rendered width `< 65` columns
- **Strictly NO backward edges / cycles** (error branches terminate in local leaf nodes like `ErrAuth["401 Error"]`)
- Wrap every label in double quotes (`NodeId["Label text"]`)
- Wrap label lines with `<br/>` at `<= 18–22` characters
- Never use raw angle brackets `<...>` (use parentheses `(AuthResponse)` instead)
- Alphanumeric node IDs only (no hyphens, colons, or dots)

### 4. Deep Layer-by-Layer Trace
Trace the exact execution in order:
1. **Browser & Next.js Frontend (`:3000`)** — Component state, validation, `envConfig`, `services/*`
2. **API Gateway (`:8080`) & Eureka (`:8761`)** — CORS preflight, `RouterValidator`, `AuthenticationFilter`, `lb://` resolution
3. **Backend Microservice (`:808x`)** — Controller validation, `@Transactional` service logic, security/cryptography
4. **PostgreSQL Database (`:543x`)** — Exact tables, queries/inserts, constraints, indexes, eager/batch loading
5. **Response Path & Frontend State** — `convertApiResponse()`, `localStorage` or RSC cache, navigation/redirect

### 5. Status & Error Matrix + Key Payloads
List all handled HTTP status codes (`200`/`201`, `400`, `401`, `403`, `404`, `409`, `503`) with exact frontend and backend behavior, plus key request/response shapes.

### 6. Verified Source References
List exact plain-text repository file paths with line numbers and symbol names (e.g., `frontend\src\app\_components\HeroPromoSection.tsx` (lines 12–48)) instead of clickable links to source code files. Never document unverified code or assumed behavior.
- **Plain Text Code Paths + Lines:** When citing source code files (`frontend\...`, `backend\...`, `database\...`), write plain text paths with exact line numbers rather than Markdown hyperlinks.
- **Strictly Relative Markdown Links for Docs:** When linking between Markdown documents inside the repo (e.g., `./FLOW.md`, `./flows/loginFlow.md`), always use relative paths, never `file:///` URLs or machine-specific drive paths (`C:/...`).

---

## 🗄️ Standard Template for `document/schemas/<module>Schema.md`

Create one file per downstream backend database using **camelCase** naming ending in `Schema.md` (`catalogSchema.md`, `userSchema.md`, `orderSchema.md`, `inventorySchema.md`). Include these 5 sections:

1. **Module & Database Metadata Table:** Service name & port, PostgreSQL DB name & host port, Hibernate `ddl-auto`, and Flyway migration / entity paths.
2. **Unicode Box Relationship Summary (`< 60` columns):** Terminal-safe overview showing intra-DB foreign keys and cross-service logical UUID references.
3. **Mermaid Entity-Relationship Diagram (`erDiagram`):** Show tables, `PK` / `FK` / `UK` keys, columns, and cardinalities (`||--o{`, `}|--|{`, `||--o|`). Never use raw HTML `<...>` tags.
4. **Constraints, Indexes & Cross-Service References:** Separate **Intra-DB SQL Foreign Keys** (`ON DELETE` rules, `@BatchSize`, `FetchType`) from **Cross-Service Logical References** (UUID fields without SQL FKs across isolated databases).
5. **Verified Source References:** Plain-text file paths with exact line ranges for SQL migrations and JPA `@Entity` classes.

---

## ✅ Post-Update Verification Checklist

Before finishing any task that triggers this skill:
- [ ] `document/flows/<feature>Flow.md` (and/or `document/schemas/<module>Schema.md`) reflects the exact current symbol names, ports, endpoints, and DB tables.
- [ ] Source code references use plain-text paths with line ranges (no code hyperlinks); doc-to-doc links use relative paths (zero `file:///` or absolute drive paths).
- [ ] Unicode box diagram width is strictly `< 60` columns.
- [ ] Mermaid `flowchart TD` has zero backward edges, zero raw `<...>`, and lines `<= 22` chars (`erDiagram` used cleanly inside `document/schemas/*.md`).
- [ ] `document/README.md` indexes the flow and schema files with relative links.
- [ ] `document/FLOW.md` is updated if any backend module-to-module routing or OpenFeign call changed.
