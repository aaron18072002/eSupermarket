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
| **Validation / Utility**| `validate...` / `process...` | `validateAdminRole()`, `processSepayWebhook()` |

> **Prohibited:** Do not use arbitrary or mixed prefixes such as `get...`, `find...`, `fetch...`, `save...`, `modify...` in Controller or Service interfaces. Keep Repository methods aligned with Spring Data JPA conventions (`findById`, `existsByOrderCode`).

---

## 2. 📦 Core Technology & Dependency Version Standards

To maintain compatibility and prevent dependency version drift across microservices:

| Technology / Library | Standard Version | Purpose / Notes |
| :--- | :--- | :--- |
| **Java JDK** | `17` | Language baseline for all backend microservices |
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
* **App Router Structure:** Strictly use the Next.js `app/` directory convention.
* **Server vs. Client Components:** Default to React Server Components (RSC) for data fetching. Mark components with `'use client'` only when state or browser event handlers are needed.
* **Caching & Revalidation:** Use Next.js incremental cache tags with `fetch(url, { next: { revalidate: 60, tags: [...] } })`.
* **Indentation:** 2 spaces indentation for TypeScript, TSX, JSON, and YAML files.
* **Strict Type Safety:** Avoid `any`. Define dedicated interfaces or types for all API responses and component props.

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
* **Timing-Attack Prevention:** Webhook signatures (e.g., SePay HMAC-SHA256) must always be validated using constant-time comparison via `MessageDigest.isEqual()`.
* **Role-Based Access Control (RBAC):** Admin endpoints (e.g., `/dashboard/summary`, `readAllOrders`) must verify `ROLE_ADMIN` from token claims.
* **OpenAPI Documentation:** All protected microservices must declare the `BearerAuth` security scheme in `OpenApiConfig`.
