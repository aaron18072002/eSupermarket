# eSupermarket Workspace Guidelines

Please strictly follow the workspace coding standards and architectural conventions defined in [.agent/rules/coding-standards.md](./.agent/rules/coding-standards.md):

1. **Controller Method Prefixes:** Always use `create...`, `read...`, `update...`, `delete...`, and `search...`.
2. **Fail-Fast Configuration:** Do not use fallback backup values in `@Value` or `application.yaml` (no `${VAR:default}`).
3. **Java & TypeScript Formatting:** Record DTOs, no method references (always use explicit lambdas), 120-char line limit (Java) / 100-char line limit (Frontend TS/TSX), no wildcard imports, `@BatchSize(size = 25)` for JPA relationships, Next.js App Router Server Components, PascalCase/camelCase FE files (no kebab-case code files), inline multi-line `className="..."` formatting, and plain error messages (no `[XXX]` prefixes).
4. **Standard Exceptions & Response:** Standard `ApiResponse<T>`, domain exceptions extending `RuntimeException`, centralized `@RestControllerAdvice`.
5. **Security & RBAC:** JJWT 0.12.6, SHA-256 derived keys, constant-time HMAC validation, and `ROLE_ADMIN` protection for administrative endpoints.
6. **Architecture & Request Flows:** Refer to [.agent/skills/project-architecture-reader/SKILL.md](./.agent/skills/project-architecture-reader/SKILL.md), [document/FLOW.md](./document/FLOW.md), [document/README.md](./document/README.md), and [README.md](./README.md) for monorepo layout, port allocations, and backend inter-module communication flows.
7. **Front-End Retail UI & Next.js Standards:** Refer to [.agent/skills/frontend-engineering/SKILL.md](./.agent/skills/frontend-engineering/SKILL.md) for the Modern Minimalist White & Brown UI system (#FFFFFF/#FAF8F5 surfaces, #3E2723 espresso & #8B5E3C chestnut accents, 4-col gallery grid, spec chips, 2-col PDP, English UI copy with VND currency), API Gateway routing (:8080), Server Component patterns, pure functional architecture (zero classes), direct `response` contract (no "Result" word), `localStorage` JWT token storage, PascalCase/camelCase file naming, plain error messages, and 100-char + inline multi-line `className` formatting.
8. **Communication & Response Standards (PowerShell & agy CLI):**
   - **Environment Context:** The user always interacts with Antigravity via **Windows PowerShell (`agy`)**. All responses, terminal commands, and visual models must be optimized for PowerShell console buffers and column limits.
   - **Language Level:** Communicate in clear, concise B1–C1 English. Avoid overly complex words, idioms, or academic jargon. Keep sentences direct and easy to read.
   - **PowerShell Command Conventions:** Recommend PowerShell-compatible commands (e.g. `$env:PORT="8080"` instead of `export`, prefer cross-platform tools `mvn`, `pnpm`, `git`).
   - **Mandatory Visual Diagrams & Zero-Failure Chart Standards:** Always provide visual flowcharts or architecture diagrams based on project code whenever explaining concepts, debugging, or recommending technical solutions.
     - **Terminal-Safe Dual Visualization:** Because CLI terminals may not render graphical Mermaid blocks, always accompany explanations with a clean ASCII/Unicode box-drawing flowchart (`┌───┐`, `│`, `▼`, width < 60 cols) so the user can immediately see the visual flow model in PowerShell.
     - **Mermaid Terminal Width Limit (Diagram Width < 65 cols):**
       - **Strictly No Backward Edges (Cycles):** NEVER route a lower node back up to an earlier node (e.g. `CheckStatus --> ShowError`). It causes Dagre to route wide outer bypass lanes, exploding diagram width (> 220 cols) and failing terminal inline display.
       - **Local Terminal Leaf Nodes:** When decisions fail, terminate immediately into local leaf nodes (`Err1["Error Alert"]`, `Err2["Error Alert"]`).
       - Use vertical orientation (`flowchart TD`).
       - Enclose all node labels in double quotes: `NodeId["Label text"]`.
       - NEVER use raw angle brackets `<...>` (e.g. `<AuthResponse>`, `<T>`); use parentheses `(AuthResponse)` instead.
       - Use simple alphanumeric node IDs (no hyphens, colons, or dots).
       - Keep line lengths under 18–22 characters per line using `<br/>`.
       - Keep edge labels short (`-- "Yes" -->`, `-- "No" -->`).
9. **Flow Documentation Synchronization (`document/`):**
   - Refer to [.agent/skills/flow-documentation/SKILL.md](./.agent/skills/flow-documentation/SKILL.md).
   - Always use **relative Markdown links** inside repository `.md` files (never `file:///` or absolute drive paths) so docs stay portable across devices.
   - Whenever adding or modifying a page (`frontend/src/app/**/page.tsx`), frontend service request, API Gateway route or security rule, backend controller/service/OpenFeign call, or database schema, create or update the matching `document/flows/<feature>Flow.md` and/or `document/schemas/<module>Schema.md` file (plus `document/README.md`) with Unicode box art (`< 60` cols) and Mermaid (`flowchart TD` `< 65` cols or `erDiagram` in schema docs).
   - Whenever changing backend inter-module communication (Eureka, Gateway `lb://` routing, OpenFeign calls, or shared JWT/DB boundaries), also update [document/FLOW.md](./document/FLOW.md).
