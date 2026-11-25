# 📐 eSupermarket Engineering & Coding Standards

This document establishes the official engineering guidelines, architectural conventions, formatting rules, and dependency standards for the **eSupermarket** platform. All microservices (Java Spring Boot) and frontend applications (Next.js App Router) must strictly adhere to these rules.

---

## 1. 🏷️ Controller & Service Method Naming Standards

All controllers and service interfaces across microservices MUST follow uniform prefix naming standards:

| Action | Required Prefix | Controller / Service Examples |
| :--- | :--- | :--- |
| **Creation** | `create...` | `createProduct()`, `createOrder()`, `createCategory()` |
| **Retrieval** | `read...` | `readProductById()`, `readAllOrders()`, `readDashboardSummary()`, `readCurrentUser()` |
| **Search** | `search...` | `searchProducts(query, pageable)` |
| **Update** | `update...` | `updateProduct()`, `updateUser()`, `updateOrderStatus()` |
| **Deletion** | `delete...` | `deleteProductById()`, `deleteUserById()` |
| **Validation / Utility**| `validate...` / `process...` | `validateAdminRole()`, `validatePassword()` |

> **Prohibited:** Do not use arbitrary or mixed prefixes such as `get...`, `find...`, `fetch...`, `save...`, `modify...` in Controller or Service interfaces. Keep Repository methods aligned with Spring Data JPA conventions (`findById`, `existsByOrderCode`).

---

## 2. 📦 Core Technology & Dependency Version Standards

To maintain compatibility and prevent dependency version drift across microservices:

| Technology / Library | Standard Version | Purpose / Notes |
| :--- | :--- | :--- |
| **Java JDK** | `17` | Language baseline for all backend microservices |
| **Node.js Runtime** | `v22.23.3` | JavaScript/TypeScript runtime baseline for frontend Storefront |
| **Spring Boot** | `3.3.4` / `4.0.8` | Core application framework |
| **Spring Cloud** | `2025.1.3` / `2023.0.3` | Eureka service discovery and API Gateway |
| **Spring Cloud OpenFeign** | `4.2.0` | East-West synchronous service communication |
| **PostgreSQL Driver** | `42.7.3` | Relational database driver for PostgreSQL 16 |
| **JJWT (Java JWT)** | `0.12.6` | `jjwt-api`, `jjwt-impl`, `jjwt-jackson` for stateless authentication |
| **MapStruct** | `1.6.3` | DTO-to-Entity mapping with compile-time generation |
| **Lombok** | `1.18.32` | Boilerplate reduction with `lombok-mapstruct-binding:0.2.0` |
| **Springdoc OpenAPI** | `2.8.5` | Swagger UI documentation (`springdoc-openapi-starter-webmvc-ui`) |
| **Frontend Framework** | Next.js 14+ / React 18+ | Next.js App Router (`app/`) with TypeScript & Tailwind CSS |

---

## 3. ⚙️ Configuration Properties & Fail-Fast Architecture

### Strict "No Inline Fallback / Backup" Rule
* **NO default fallbacks in `@Value`:** Never write `@Value("${my.prop:default_value}")`. Always write `@Value("${my.prop}")`.
* **NO backup fallbacks in `application.yaml`:** Never declare `${ENV_VAR:fallback_value}`. Declare explicit configurations directly.
* **Fail-Fast Principle:** If a required property is missing, the application must immediately fail to boot at startup rather than quietly continuing with dummy or insecure default values.

---

## 4. 📝 Java & TypeScript Code Formatting Rules

### Java Formatting Guidelines
* **No Wildcard Imports:** Strictly forbidden (`import java.util.*;`). Always import individual classes explicitly.
* **Immutability with Records for DTOs:**
  * All Request, Response, and Projection DTOs MUST strictly use Java `record` types (e.g. `public record OrderResponse(...)`, `public record DashboardMetricsProjection(...)`).
  * Never create mutable classes or POJOs with `@Getter`/`@Setter` for data transfer objects. Records guarantee immutability, thread-safety, auto-generated `equals`/`hashCode`/`toString`, and lightweight serialization.
* **Line Length Limit (Maximum 120 Characters):**
  * Java code lines must NEVER exceed 120 characters.
  * Wrap chained Stream or Builder calls onto new lines indented by 4 spaces.
  * Long method signatures, constructor arguments, and multiline annotations must be broken cleanly across multiple lines.
* **No Method References - Always Use Explicit Lambda Functions:**
  * Strictly DO NOT use Java method references (`Class::method`, `this::method`, `Object::toString`, `this.orderMapper::toResponse`).
  * ALWAYS write explicit, clear lambda expressions (e.g., `order -> this.orderMapper.toResponse(order)`, `role -> role.toString()`, `s -> s.toUpperCase()`).
  * *Rationale:* Explicit lambdas provide immediate visual clarity on parameter names, scope, and transformations without implicit method invocation ambiguity.
* **Indentation & Spacing:** Use 4 spaces for Java indentation. Avoid tab characters.
* **JPA Performance (Anti-N+1 Rule):**
  * When mapping `@OneToMany` relationships, always add Hibernate `@BatchSize(size = 25)` or use batch configuration.
  * For aggregations and metrics, write single-pass JPQL projection queries (`SELECT new com.coding.dto.response.DashboardMetricsProjection(...)`) instead of issuing multiple sequential queries.

### TypeScript & Next.js Formatting Guidelines
* **Front-End Architecture & Retail UI Skill:** Refer to [.agent/skills/frontend-engineering/SKILL.md](file:///C:/Projects/eSupermarket/.agent/skills/frontend-engineering/SKILL.md) for the Modern Minimalist White & Brown e-commerce UI system (`#FFFFFF`/`#FAF8F5` surfaces, `#3E2723` espresso & `#8B5E3C` chestnut accents, 4-col gallery grid, English UI copy with VND currency), Gateway API contracts, and implementation examples.
* **App Router Structure:** Strictly use the Next.js `app/` directory convention.
* **Server vs. Client Components:** Default to React Server Components (RSC) for data fetching. Mark components with `'use client'` only when state or browser event handlers are needed (pushing `'use client'` down to interactive leaf components).
* **Caching & Revalidation:** Use Next.js incremental cache tags with `fetch(url, { next: { revalidate: 60, tags: [...] } })`.
* **Indentation & Line Length (100 Characters):** Use 2 spaces indentation (`tabWidth: 2`, `endOfLine: "lf"`) for TypeScript, TSX, JSON, and YAML files. Frontend TypeScript/TSX lines must strictly stay within **100 characters** (`printWidth: 100` in `.prettierrc` and ESLint `max-len` rule).
* **Tailwind `className` Formatting:** Keep Tailwind classes directly inline inside `className="..."` (or template literal `` className={`...`} `` for conditional classes) on the JSX tag so IDE Tailwind CSS popups work natively; do not wrap them in helper functions (`joinClassNames`, `cn`) or external constants. Break long `className` values cleanly across lines with Enter so every line stays `<= 100` characters.
* **Strict Type Safety:** Avoid `any`. Define dedicated interfaces or types for all API responses and component props.
* **Fail-Fast Environment Variables:** No default fallbacks (`process.env.VAR || 'default'`); throw an immediate plain descriptive `Error` if missing.
* **Plain Error Messages:** Write clear, plain error messages without bracketed tags or prefixes such as `[FAIL-FAST CONFIG ERROR]`, `[ERROR]`, or `[XXX] ...`.
* **Pure Functions Only (Zero Classes):** Modern frontend uses pure functional components, hooks, and standalone exported functions. Strictly no ES6/TypeScript classes or custom Error classes (`class ... extends Error`) in frontend code.
* **Direct "response" Contract (No "Result" Word):** Never create `Result` or `Either` wrapper types (e.g., `Result<T>`, `AuthResult`). Functions must directly return and consume the uniform `ApiResponse<T>` contract named `response` (`{ status, message, data }`).
* **Client Session Storage:** Store JWT access/refresh tokens and user profiles in browser `localStorage` (not cookies). Manage tokens via pure helper functions (`storeAuthTokens`, `readAuthToken`, `deleteAuthTokens`).
* **Strict Route File Convention (page.tsx):** Route endpoints must strictly be named `page.tsx` (e.g., `src/app/login/page.tsx`). Do not use `index.page.ts`, `index.tsx`, or custom file extensions.
* **Descriptive File Naming (PascalCase & camelCase Only — No kebab-case):** All React component files must use descriptive **PascalCase** matching their component name (e.g., `LoginForm.tsx`, `ProductCard.tsx`). All non-component TypeScript modules (config, services, utils, hooks, types) must use **camelCase** (e.g., `envConfig.ts`, `authService.ts`, `formatters.ts`). Strictly prohibit `kebab-case` code filenames and generic `index.tsx` or `index.ts` files.
* **Component Colocation via Private Folders (_components):** Components used only on a specific route must be colocated inside `src/app/<route>/_components/<ComponentName>.tsx`. Components shared across multiple routes belong in `src/components/` (e.g., `src/components/ProductCard.tsx`).
* **Method & Action Prefixes:** Standard prefixes `create...`, `read...`, `search...`, `update...`, `delete...`.

---

## 5. 🛡️ Global Exception Handling & Uniform API Responses

### Standard Response Envelope: `ApiResponse<T>`
All REST controllers must return standard `ApiResponse<T>`:
```json
{
  "status": 200,
  "message": "Descriptive success message",
  "data": { ... }
}
```

### Exception Hierarchy & HTTP Status Mapping
All business exceptions must extend `RuntimeException` and be handled centrally via `@RestControllerAdvice`:

| Exception Class | HTTP Status Code | Scenario |
| :--- | :--- | :--- |
| `ResourceNotFoundException` | `404 NOT_FOUND` | Entity ID or Code not found in database |
| `UnauthorizedException` | `401 UNAUTHORIZED` | Missing, malformed, or expired JWT token |
| `AccessDeniedException` | `403 FORBIDDEN` | Authenticated user lacks required role (e.g., non-admin accessing dashboard) |
| `DuplicateResourceException`| `409 CONFLICT` | Resource uniqueness violation (SKU, barcode, email) |
| `MethodArgumentNotValidException` | `400 BAD_REQUEST` | Jakarta Bean Validation failure (`@Valid`, `@NotNull`) |
| `IllegalArgumentException` | `400 BAD_REQUEST` | Invalid business input argument |
| `Exception` | `500 INTERNAL_SERVER_ERROR` | Unexpected internal server error |

---

## 6. 🔒 Security & Cryptographic Standards

* **Deterministic Key Derivation:** All microservices sharing JWT tokens must derive 256-bit HMAC signing keys using SHA-256 (`JwtUtil.deriveSigningKey`).
* **Timing-Attack Prevention:** Webhook signatures (e.g., HMAC-SHA256) must always be validated using constant-time comparison via `MessageDigest.isEqual()`.
* **Role-Based Access Control (RBAC):** Admin endpoints (e.g., `/dashboard/summary`, `readAllOrders`) must verify `ROLE_ADMIN` from token claims.
* **OpenAPI Documentation:** All protected microservices must declare the `BearerAuth` security scheme in `OpenApiConfig`.

---

## 7. 💬 Antigravity Communication & Response Standards

When assisting or generating solutions within this repository, Antigravity must strictly adhere to the following response guidelines, specifically optimized for **Windows PowerShell running the Antigravity CLI (`agy`)**:

* **Runtime Environment Context:** The user always interacts with Antigravity using **Windows PowerShell (`agy`)**. All responses, terminal visualizations, and shell snippets must be tailored for PowerShell terminal behavior and column limits.
* **Language Level (B1–C1):** Use clear, natural, and accessible English. Avoid overly complicated vocabulary, convoluted sentence structures, or academic jargon. Keep answers direct, friendly, and practical.
* **PowerShell Command Conventions:** All recommended terminal commands must be valid in PowerShell (e.g., use `$env:VAR="value"` instead of `export VAR=value`, avoid bash-specific operators, prefer universal `mvn`, `pnpm`, `git` commands).
* **Mandatory Visual Diagrams & Zero-Failure PowerShell Chart Standards:** Always provide visual flowcharts or architectural diagrams based on current project code whenever explaining concepts, debugging, or proposing designs. To ensure diagrams NEVER fail or get demoted to raw code blocks in PowerShell:
  1. **Strict PowerShell Terminal Width Limit (< 65 cols):**
     - Windows PowerShell terminal windows often run at 80–120 columns (or split panes). 
     - **Rendered graph width must strictly remain < 65 columns.**
     - **STRICTLY NO BACKWARD EDGES / FEEDBACK LOOPS:** NEVER connect a lower node back up to an earlier node (e.g. `CheckStatus --> ShowError`). Backward routing forces Dagre to allocate wide bypass lanes around the diagram, instantly exploding diagram width beyond 220+ cols and triggering PowerShell terminal overflow.
     - **Local Terminal Leaf Nodes:** When a decision fails, terminate immediately into local leaf nodes (e.g. `Err1["Error Alert"]` and `Err2["Error Alert"]`) instead of routing back to a shared node.
     - **Short Arrow Labels:** Keep arrow edge labels minimal (e.g. `-- "Yes" -->`, `-- "No" -->`).
  2. **Strictly Escape Node Labels:**
     - ALWAYS wrap all node labels in double quotes: `NodeId["Label text"]`.
     - NEVER use unescaped angle brackets `<...>` (e.g. generic types like `<AuthResponse>`, `<T>`, or `<void>`) as Mermaid treats them as invalid HTML tags and crashes the parser. Use parentheses `(AuthResponse)` or plain text.
  3. **Safe Alphanumeric Node Identifiers:** Use plain alphanumeric identifiers (e.g., `Step1`, `ApiGateway`, `DbCheck`). Do not use dashes, dots, colons, or special characters in node IDs.
  4. **Strict Vertical Orientation (`flowchart TD`):**
     - Always use `flowchart TD` (top-down). Never use `LR` for multi-step flows.
     - Keep the primary happy path strictly aligned in a single vertical column.
  5. **Ultra-Short Line Lengths with `<br/>`:** Keep node text under 18–22 characters per line, wrapping with `<br/>`.
  6. **Dual Terminal-Safe Visual Flowchart (PowerShell Unicode Box Art):**
     - Because CLI terminals and certain markdown viewers cannot render graphical Mermaid blocks into interactive graphics, **always provide a clean ASCII/Unicode box flowchart (`┌───┐`, `│`, `▼`, `►`)** alongside or as a primary visual model.
     - Keep box art width strictly under 60 columns to prevent wrapping on standard PowerShell console buffers.
