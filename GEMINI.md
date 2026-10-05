# eSupermarket Workspace Guidelines

Please strictly follow the workspace coding standards and architectural conventions defined in [.agent/rules/coding-standards.md](file:///.agent/rules/coding-standards.md):

1. **Controller Method Prefixes:** Always use `create...`, `read...`, `update...`, `delete...`, and `search...`.
2. **Fail-Fast Configuration:** Do not use fallback backup values in `@Value` or `application.yaml` (no `${VAR:default}`).
3. **Java & TypeScript Formatting:** Record DTOs, no method references (always use explicit lambdas), 120-char line limit, no wildcard imports, `@BatchSize(size = 25)` for JPA relationships, Next.js App Router Server Components.
4. **Standard Exceptions & Response:** Standard `ApiResponse<T>`, domain exceptions extending `RuntimeException`, centralized `@RestControllerAdvice`.
5. **Security & RBAC:** JJWT 0.12.6, SHA-256 derived keys, constant-time HMAC validation, and `ROLE_ADMIN` protection for administrative endpoints.
6. **Architecture & Request Flows:** Refer to [.agent/skills/project-architecture-reader/SKILL.md](file:///.agent/skills/project-architecture-reader/SKILL.md), [FLOW.md](file:///FLOW.md), and [README.md](file:///README.md) for monorepo layout, port allocations, and service communication flows.
