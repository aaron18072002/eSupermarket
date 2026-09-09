# 🛒 eSupermarket Microservices Platform

Welcome to the **eSupermarket** project! This is a modern, scalable e-commerce platform built to explore microservices architecture using the JavaScript and TypeScript ecosystem.

## 🎯 Project Purpose

The main goal of this project is to build a complex supermarket system, inspired by real-world enterprise retail platforms like Lidl.

It is specifically designed to transition strong backend concepts from the Java world (like Domain-Driven Design, Dependency Injection, and strict typing) into modern Node.js frameworks. Instead of building one giant application (a monolith), this project is built as a **Monorepo**. This means the system is divided into small, independent microservices that can be developed, updated, and deployed separately.

## 🏗️ Core Business Domains (Planned)

The supermarket operations are split into these independent services:

- **Frontend Web App:** The user interface for customers to browse groceries and manage their shopping carts.
- **Inventory Service:** Manages the product catalog, categories, brands, and tracks how much stock is left.
- **Loyalty & User Service:** Manages customer accounts and discount coupons.
- **Checkout Service:** Handles the shopping cart, calculates totals, and processes final purchases.

## 🛠️ Technology Stack

_Note: The specific versions of the tools and libraries will be updated here as they are officially added to the project._

- **Frontend:** Next.js (App Router) & React
- **Backend:** NestJS
- **Database:** PostgreSQL
- **Language:** TypeScript
- **Workspace Management:** NPM Workspaces
- **Code Formatting:** Prettier

## 📁 Workspace Structure

This project uses NPM Workspaces to keep everything organized in one place while keeping the dependencies separate.

```text
eSupermarket/
├── apps/
│   ├── frontend/             # Next.js web application
│   ├── inventory-service/    # NestJS microservice (Coming soon)
│   └── checkout-service/     # NestJS microservice (Coming soon)
├── package.json              # Master workspace configuration
├── .prettierrc               # Global code formatting rules
└── .gitignore                # Files ignored by Git
```

## 🚀 Getting Started

_(Detailed instructions to run the databases and microservices will be added here as the project grows.)_

Currently, the workspace is configured and the initial Next.js frontend application is set up.
