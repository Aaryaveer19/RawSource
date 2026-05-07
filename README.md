# RawSource 🌐

> **A production-grade B2B Supply Chain SaaS Platform** connecting raw material Buyers and Suppliers through a secure, role-aware, and data-driven interface.

---

## 📦 Project Overview

RawSource is a full-stack Supply Chain Management System built to simulate real-world B2B raw material trading. The platform supports two distinct user roles — **Consumers (Buyers)** and **Suppliers** — each with a dedicated, role-protected dashboard. It features a public **Global Marketplace**, a complete **order management lifecycle**, a **consumer review & quality rating system**, and a fully automated **cloud deployment pipeline**.

The application was developed with a strong focus on software engineering best practices, including stateless JWT authentication, automated log archiving, Docker containerization, and a CI/CD pipeline that deploys on every GitHub push.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Frontend** | Angular 17+, Tailwind CSS v3, TypeScript |
| **Backend** | Spring Boot 3.5, Java 17 |
| **Database** | PostgreSQL 15 (hosted on Neon.tech) |
| **Security** | JWT (JSON Web Tokens), BCrypt Password Hashing, Spring Security |
| **API Documentation** | SpringDoc OpenAPI / Swagger UI |
| **Logging** | Logback (Time-Based Log Rotation + Auto GZ Compression) |
| **Containerization** | Docker (Multi-Stage Build) |
| **Build Tools** | Maven (backend), Angular CLI (frontend) |
| **Deployment** | Render (Backend), Vercel (Frontend), Neon (Database) |

---

## 🗂️ Project Structure

```
RawSource-FinalDraft/
├── backend/                          # Spring Boot REST API
│   ├── Dockerfile                    # Multi-stage Docker build
│   └── src/main/
│       ├── java/com/example/supply_chain/
│       │   ├── controller/           # REST Endpoints (10 controllers)
│       │   ├── entity/               # JPA Entities (10 entities)
│       │   ├── repository/           # Spring Data JPA Repositories
│       │   ├── service/              # Business Logic Layer
│       │   └── config/               # Security & CORS Configuration
│       └── resources/
│           ├── application.properties
│           └── logback-spring.xml    # Log rotation configuration
│
└── frontend/                         # Angular SaaS Application
    ├── vercel.json                   # Vercel deployment config
    └── src/app/
        ├── auth/                     # Login & Register (Buyer / Supplier)
        │   ├── interceptors/         # JWT AuthInterceptor
        │   └── guards/               # AuthGuard (route protection)
        ├── core/                     # Interceptors, Layout, Landing Page
        ├── dashboard/                # Protected Dashboard Shell
        │   ├── components/
        │   │   ├── consumer/         # Buyer: Orders, Order Details, Reviews
        │   │   └── supplier/         # Supplier: Materials, Inventory, Contracts
        │   └── services/             # ConsumerService, SupplierService
        ├── marketplace/              # Public Raw Materials Listing
        ├── shared/                   # Header, Sidebar, Footer Components
        └── environments/             # Dev & Production API config
```

---

## ✨ Core Features

### 🔐 Authentication & Authorization
- Unified Auth screen with a **Buyer / Supplier role toggle**
- Separate registration flows for Consumers and Suppliers
- **Stateless JWT Authentication** — tokens stored in `localStorage`
- **AuthInterceptor** automatically attaches JWT Bearer token to every outgoing HTTP request
- **AuthGuard** protects all dashboard routes from unauthorized access
- **Role-Based Access Control (RBAC)** — a Supplier cannot access Consumer routes and vice versa

### 🛒 Consumer (Buyer) Portal
- **My Orders** — Live table of all placed orders with color-coded status badges (`PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`)
- **Order Details** — Full item-level breakdown with auto-calculated Grand Total
- **Review Submission** — Star rating (1–5) and written review for delivered orders

### 🏭 Supplier Portal
- **Materials Hub** — Full CRUD management of raw material catalog
- **Inventory & Pricing** — Inline editing of stock quantities and unit prices
- **Contracts** — View and monitor all active supply agreements

### 🌍 Global Marketplace
- Publicly accessible product grid — **no login required**
- **Live Search** — Instantly filters cards by material name, description, or supplier name (client-side, zero API calls)
- Quality rating badge on every card showing aggregate consumer score
- Direct order placement with selectable quantity

### ⭐ Quality Rating System
- Consumers submit reviews after delivery
- Backend **automatically recalculates** the aggregate average score
- Updated rating is instantly reflected on the Marketplace card (API Chaining)

### 📊 Role-Aware Dashboard
- Metrics dynamically adapt based on logged-in role
- Buyers see: Total Orders & Orders In Process
- Suppliers see: Active Contracts & Material Asset Count

---

## 🏗️ Advanced Engineering Features

### 1. ⏱️ Time-Based Log Rotation & Auto-Compression
Application logs are automatically rolled over daily and compressed into `.gz` archive files using **Logback's TimeBasedRollingPolicy**. Keeps a 30-day history with a 3GB total size cap. Prevents disk exhaustion on production servers.
> **File:** `backend/src/main/resources/logback-spring.xml`

### 2. 🔗 API Chaining (Review → Quality Rating)
A single review submission triggers a backend chain: save review → fetch all reviews for the material → calculate new average → update `QualityRating` record. One user action causes multiple automated database operations.
> **File:** `backend/.../controller/ReviewController.java`

### 3. ⚡ Parallel API Calls with ForkJoin
The Supplier Dashboard fires multiple API calls simultaneously using Angular's `forkJoin` operator, waiting for all results before rendering. Reduces perceived load time compared to sequential calls.
> **File:** `frontend/.../dashboard-home.component.ts`

### 4. 🐳 Docker Multi-Stage Build
The backend is containerized using a two-stage Dockerfile: Stage 1 compiles the Java source using a Maven image; Stage 2 runs only the compiled `.jar` in a lightweight JRE image. Results in a smaller, more secure production container.
> **File:** `backend/Dockerfile`

### 5. 🚀 Automated CI/CD Pipeline
Every `git push` to the `main` branch automatically triggers a rebuild and redeploy on both **Render** (backend) and **Vercel** (frontend). Zero manual deployment steps required after the initial setup.

### 6. 🌐 Environment-Based API Switching
The Angular app automatically switches its API base URL between `http://localhost:8080` (development) and the live Render URL (production) using Angular's `environment.ts` file replacement system configured in `angular.json`.
> **Files:** `frontend/src/environments/environment.ts` & `environment.prod.ts`

### 7. 🔒 BCrypt Password Hashing
All user passwords are hashed with BCrypt before being stored in the database. Plain-text passwords are never persisted. Even if the database is compromised, actual passwords cannot be recovered.

### 8. 📚 Auto-Generated API Documentation
All REST endpoints are automatically documented and made interactively testable via **Swagger UI**, powered by `springdoc-openapi`. No separate documentation effort required.
> **URL:** `http://localhost:8080/swagger-ui.html`

### 9. 📦 Angular Lazy Loading
The app is split into feature modules (`AuthModule`, `DashboardModule`, `MarketplaceModule`) that are loaded on demand. The browser only downloads the code for the page being visited, improving initial load performance.

---

## 🗄️ Database Entities (ER Overview)

| Entity | Description |
|---|---|
| `Consumer` | Buyer account with role and credentials |
| `Supplier` | Supplier account with company details |
| `RawMaterial` | Material product listed by a Supplier |
| `Availability` | Stock quantity per material per supplier |
| `Pricing` | Unit price per material per supplier |
| `Order` | A purchase order placed by a Consumer |
| `OrderItem` | A line item within an Order |
| `Contract` | A formal supply agreement |
| `Review` | A Consumer's rating and comment on a delivered order |
| `QualityRating` | Aggregated average score calculated from all Reviews |

---

## 🔌 API Reference

### Authentication

| Endpoint | Method | Description |
|---|---|---|
| `/api/consumers/login` | `POST` | Consumer login → returns JWT |
| `/api/consumers/register` | `POST` | Register new Consumer |
| `/api/suppliers/login` | `POST` | Supplier login → returns JWT |

### Consumer Endpoints

| Endpoint | Method | Description |
|---|---|---|
| `/api/consumers/{id}/orders` | `GET` | Get all orders for a buyer |
| `/api/orders/{id}/items` | `GET` | Get items for a specific order |

### Supplier Endpoints

| Endpoint | Method | Description |
|---|---|---|
| `/api/suppliers/{id}/availability` | `GET` | Stock levels |
| `/api/suppliers/{id}/pricing` | `GET` | Pricing records |
| `/api/suppliers/{id}/contracts` | `GET` | Active contracts |

### Public Endpoints

| Endpoint | Method | Description |
|---|---|---|
| `/api/marketplace/listings` | `GET` | Browse global marketplace |
| `/api/reviews` | `POST` | Submit a consumer review |

> All protected endpoints require an `Authorization: Bearer <token>` header.

---

## 🚀 Running Locally

### Prerequisites
- Java 17+
- Node.js 18+
- PostgreSQL 14+
- Maven

### 1. Database Setup
```sql
CREATE DATABASE supply_chain;
```

### 2. Backend Setup
```powershell
cd backend
.\mvnw.cmd spring-boot:run
```
Backend runs at: **`http://localhost:8080`**
API Docs (Swagger): **`http://localhost:8080/swagger-ui.html`**

### 3. Frontend Setup
```powershell
cd frontend
npm install
npm run start
```
Frontend runs at: **`http://localhost:4200`**

---

## ☁️ Cloud Deployment Architecture

```
GitHub Repository
       │
       ├──► Vercel          (Frontend - Angular SPA)
       │         └── Auto-deploys on every git push
       │
       └──► Render          (Backend - Docker Container)
                 └── Auto-deploys on every git push
                           │
                           └──► Neon.tech    (PostgreSQL Database - Permanent Free Tier)
```

---

## 📸 Application Routes

| Route | Access | Description |
|---|---|---|
| `/` | Public | Landing Page |
| `/marketplace` | Public | Global raw materials grid with live search |
| `/auth/login` | Public | Unified login with role toggle |
| `/auth/register` | Public | Registration with role selection |
| `/dashboard` | Protected | Role-aware dashboard home |
| `/dashboard/consumer/orders` | Consumer only | Buyer order history |
| `/dashboard/consumer/orders/:id` | Consumer only | Order details & review submission |
| `/dashboard/supplier/materials` | Supplier only | Material catalog management |
| `/dashboard/supplier/inventory` | Supplier only | Inline stock & price editor |
| `/dashboard/supplier/contracts` | Supplier only | Contract viewer |

---

## 🔒 Security Notes

- Database credentials are **never hardcoded**. They are set via environment variables (`DATABASE_URL`, `DB_USERNAME`, `DB_PASSWORD`).
- JWT secret is environment-variable based.
- The `application.properties` file uses `${VARIABLE:fallback}` syntax to support both local development and cloud deployment seamlessly.

---

## 📄 License

This project is built for academic and demonstration purposes.
