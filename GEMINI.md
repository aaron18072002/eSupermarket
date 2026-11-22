# eSupermarket Workspace Guidelines

Please strictly follow the workspace coding standards and architectural conventions defined in [.agent/rules/coding-standards.md](file:///.agent/rules/coding-standards.md):

1. **Controller Method Prefixes:** Always use `create...`, `read...`, `update...`, `delete...`, and `search...`.
2. **Fail-Fast Configuration:** Do not use fallback backup values in `@Value` or `application.yaml` (no `${VAR:default}`).
3. **Java & TypeScript Formatting:** Record DTOs, no method references (always use explicit lambdas), 120-char line limit, no wildcard imports, `@BatchSize(size = 25)` for JPA relationships, Next.js App Router Server Components.
4. **Standard Exceptions & Response:** Standard `ApiResponse<T>`, domain exceptions extending `RuntimeException`, centralized `@RestControllerAdvice`.
5. **Security & RBAC:** JJWT 0.12.6, SHA-256 derived keys, constant-time HMAC validation, and `ROLE_ADMIN` protection for administrative endpoints.
6. **Architecture & Request Flows:** Refer to [.agent/skills/project-architecture-reader/SKILL.md](file:///.agent/skills/project-architecture-reader/SKILL.md), [FLOW.md](file:///FLOW.md), and [README.md](file:///README.md) for monorepo layout, port allocations, and service communication flows.
7. **Front-End Retail UI & Next.js Standards:** Refer to [.agent/skills/frontend-engineering/SKILL.md](file:///.agent/skills/frontend-engineering/SKILL.md) for the Điện Máy Xanh UI system (#2A83E9 header, #FFD400 promos, #D0021B/#FF6600 conversion hierarchy, 5-col grid, spec chips, 2-col PDP), API Gateway routing (:8080), Server Component patterns, pure functional architecture (zero classes), direct `response` contract (no "Result" word), and `localStorage` JWT token storage.
8. **Communication & Response Standards:**
   - **Language Level:** Communicate in clear, concise B1–C1 English. Avoid overly complex words, idioms, or academic jargon. Keep sentences direct and easy to read.
   - **Mandatory Visual Diagrams:** Always provide interactive Mermaid diagrams (flowcharts, sequence diagrams, or architecture maps) based on the current project code whenever explaining concepts, debugging, or recommending technical solutions.
