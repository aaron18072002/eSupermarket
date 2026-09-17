content = """# 🛒 eSupermarket Microservices Platform

Welcome to the **eSupermarket** project! This is a modern, scalable e-commerce platform built to explore a polyglot microservices architecture. It combines a fast, modern frontend with a robust, enterprise-grade Java backend.

## 🎯 Project Purpose

The main goal of this project is to build a complex supermarket system, inspired by real-world enterprise retail platforms like Lidl.

Instead of building one giant application (a monolith), this project uses a **Polyglot Monorepo** approach. The user interface is built with modern JavaScript/TypeScript tools, while the core business logic is divided into small, independent Java Spring Boot microservices.

## 🏗️ Core Business Domains

The supermarket operations are split into these independent services:

- **Frontend Web App:** The user interface for customers built with Next.js.
- **Catalog Service (Backend):** Manages the product catalog, categories, brands, and product attributes.
- **Inventory Service (Backend):** Tracks physical stock across different locations and manages stock reservations during checkout.

## 🛠️ Technology Stack

**Frontend:**

- **Framework:** Next.js (App Router) & React
- **Language:** TypeScript
- **Styling & Formatting:** Tailwind CSS, Prettier

**Backend (Spring Boot Microservices):**

- **Framework:** Spring Boot (v4.0.8)
- **Language:** Java 17
- **Database:** PostgreSQL 16 (using Spring Data JPA)
- **Communication:** Spring Cloud OpenFeign
- **Utilities:** Lombok (v1.18.32), MapStruct (v1.6.3) for DTO mapping
- **Build Tool:** Maven

## 📁 Workspace Structure

This project keeps frontend and backend code in the same repository but uses native build tools for each language.

```text
eSupermarket/
├── backend/                      # Java Spring Boot Microservices
│   ├── catalog-service/          # Product and category management
│   └── inventory-service/        # Stock and location management
├── frontend/                     # Next.js web application
├── docker-compose.yml            # Local infrastructure (Databases, etc.)
├── .gitignore                    # Global git ignore rules (Java & Node)
└── README.md                     # Project documentation
```
