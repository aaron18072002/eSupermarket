# 🔄 System Request Flow

Visual architectural guide for request routing from **Frontend** through **API Gateway** to **Backend Domains**.

---

## 1. 🗺️ Big Picture

```mermaid
graph LR
    Client["📱 Next.js Web / Mobile\n(:3000)"]
    
    subgraph Edge ["Perimeter"]
        GW["🚪 API Gateway\n(:8080)"]
    end

    subgraph Backend ["Private Microservices Network"]
        CS["📦 Catalog Service\n(:8081)"]
        US["👤 User Service\n(:8082)"]
        IS["🏭 Inventory Service\n(:8083)"]
    end

    Client -->|Single Port 8080| GW
    GW -->|/api/v1/products/**| CS
    GW -->|/api/v1/auth/**| US
    GW -.->|/api/v1/inventory/**| IS
```

---

## 2. ⚡ Gateway Internal Pipeline

What happens to every HTTP request inside `api-gateway`:

```mermaid
flowchart TD
    In(["Incoming Request"]) --> CORS["1. CORS Filter\nCheck http://localhost:3000"]
    CORS --> LogIn["2. Log Request\n[GATEWAY-IN] METHOD + PATH"]
    LogIn --> Check{"3. Is Route Secured?"}
    
    Check -->|No| Route
    Check -->|Yes| Token{"4. Valid Bearer JWT?"}
    
    Token -->|Missing or Expired| Reject["❌ Return 401 Unauthorized (JSON)"]
    Token -->|Valid| Inject["5. Inject Downstream Headers:\nX-User-Id\nX-User-Roles"]
    
    Inject --> Route["6. Forward to Service"]
    Route --> Downstream["7. Microservice Execution"]
    Downstream --> LogOut["8. Log Response & Duration\n[GATEWAY-OUT] Status + Latency ms"]
    LogOut --> Out(["Client Response"])
```

---

## 3. 🚦 Route Decision: Public vs Secured

How `RouterValidator` decides whether authentication is required:

```mermaid
flowchart TD
    Req["Request Path & Method"] --> Q1{"Path starts with\n/api/v1/auth or /actuator?"}
    
    Q1 -->|Yes| Public["🟢 PUBLIC (Pass directly)"]
    Q1 -->|No| Q2{"HTTP GET and path starts with\n/api/v1/products, /categories, etc.?"}
    
    Q2 -->|Yes| Public
    Q2 -->|No| Secured["🔒 SECURED\nRequires valid Bearer token"]
```

---

## 4. 🛒 Scenario 1: Browsing Catalog (Public)

Anonymous user browsing products without login:

```mermaid
sequenceDiagram
    autonumber
    actor User as 👤 Customer
    participant GW as 🚪 Gateway (:8080)
    participant CS as 📦 Catalog (:8081)
    participant DB as 🗄️ catalog_db

    User->>GW: GET /api/v1/products
    Note over GW: Route is PUBLIC -> Skip Auth
    GW->>CS: GET /api/v1/products
    CS->>DB: SELECT * FROM products
    DB-->>CS: Product Data
    CS-->>GW: 200 OK (JSON)
    GW-->>User: 200 OK (Products displayed)
```

---

## 5. 🔑 Scenario 2: Login & Get Token

User logs in to receive a JWT:

```mermaid
sequenceDiagram
    autonumber
    actor User as 👤 Customer
    participant GW as 🚪 Gateway (:8080)
    participant US as 👤 User Service (:8082)
    participant DB as 🗄️ user_db

    User->>GW: POST /api/v1/auth/login { email, password }
    Note over GW: /api/v1/auth is PUBLIC -> Skip Auth
    GW->>US: POST /api/v1/auth/login
    US->>DB: Query user & verify password
    DB-->>US: User Record
    US->>US: Generate JWT signed with Secret Key
    US-->>GW: 200 OK { token: "eyJhbGci..." }
    GW-->>User: 200 OK { token: "eyJhbGci..." }
```

---

## 6. 🛡️ Scenario 3: Admin Creates Product (Secured)

Admin performs write operation with token:

```mermaid
sequenceDiagram
    autonumber
    actor Admin as 👨‍💼 Admin
    participant GW as 🚪 Gateway (:8080)
    participant CS as 📦 Catalog (:8081)
    participant DB as 🗄️ catalog_db

    Admin->>GW: POST /api/v1/products<br/>[Authorization: Bearer <token>]
    
    Note over GW: 1. POST is SECURED<br/>2. Verify JWT signature & expiration<br/>3. Extract claims: userId, roles
    
    rect rgb(235, 248, 255)
    GW->>CS: POST /api/v1/products<br/>[X-User-Id: "user-123"]<br/>[X-User-Roles: "ROLE_ADMIN"]
    end
    
    Note over CS: Reads @RequestHeader("X-User-Roles")<br/>No JWT library needed!
    CS->>DB: INSERT INTO products ...
    DB-->>CS: Success
    CS-->>GW: 201 Created
    GW-->>Admin: 201 Created
```

---

## 7. 🚫 Scenario 4: Expired or Bad Token (Rejected at Edge)

Bad token fails fast at Gateway without loading backend services:

```mermaid
sequenceDiagram
    autonumber
    actor Attacker as ⚠️ Bad Request
    participant GW as 🚪 Gateway (:8080)
    participant CS as 📦 Catalog (:8081)

    Attacker->>GW: DELETE /api/v1/products/123<br/>[Expired or Fake Token]
    
    Note over GW: 1. DELETE is SECURED<br/>2. JwtUtil parses token -> FAILS!
    
    rect rgb(255, 235, 235)
    GW-->>Attacker: 401 Unauthorized<br/>{ "error": "Unauthorized", "message": "JWT Token has expired" }
    end

    Note over CS: 🛡️ Catalog Service is NEVER called!
```

---

## 8. 📋 Downstream Header Contract

What internal microservices receive from the Gateway:

| Header | Example Value | Usage in Microservice |
| :--- | :--- | :--- |
| `X-User-Id` | `a1b2c3d4-...` | `@RequestHeader("X-User-Id") String userId` |
| `X-User-Roles` | `ROLE_ADMIN,ROLE_CUSTOMER` | `@RequestHeader("X-User-Roles") String roles` |
