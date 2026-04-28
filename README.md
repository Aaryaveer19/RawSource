# RawSource 🌐

> **A modern B2B Supply Chain SaaS platform** connecting raw material Buyers and Suppliers through a seamless, data-driven interface.

---

## 📦 Project Overview

RawSource is a full-stack Supply Chain Management application built for real-world B2B raw material trading. It provides role-based dashboards for **Consumers (Buyers)** and **Suppliers**, a public **Global Marketplace**, and a secure **JWT-authenticated** backend API.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Frontend** | Angular 17+, Tailwind CSS v3, TypeScript |
| **Backend** | Spring Boot 3.5, Java 17 |
| **Database** | PostgreSQL |
| **Security** | JWT (JSON Web Tokens), BCrypt |
| **API Docs** | SpringDoc OpenAPI / Swagger UI |
| **Build Tools** | Maven (backend), Angular CLI / Vite (frontend) |

---

## 🗂️ Project Structure

```
RawSource-FinalDraft/
├── backend/                    # Spring Boot REST API
│   └── src/main/java/
│       └── com/example/supply_chain/
│           ├── controller/     # REST Endpoints
│           ├── entity/         # JPA Entities
│           ├── repository/     # Spring Data Repositories
│           └── config/         # Security Configuration
│
└── frontend/                   # Angular SaaS Application
    └── src/app/
        ├── auth/               # Login & Register (Buyer / Supplier)
        ├── core/               # Interceptors, Layout, Landing Page
        ├── dashboard/          # Protected Dashboard Shell
        │   ├── components/
        │   │   ├── consumer/   # Buyer: Orders, Order Details
        │   │   └── supplier/   # Supplier: Materials, Inventory, Contracts
        │   └── services/       # ConsumerService, SupplierService
        ├── marketplace/        # Public Raw Materials Listing
        └── shared/             # Header, Sidebar, Footer Components
```

---

## ✨ Features

### 🔐 Authentication
- Separate login & registration flows for **Buyers** and **Suppliers**
- Role Toggle on a single unified Auth screen
- JWT tokens stored in `localStorage`, auto-attached to every API request via `AuthInterceptor`

### 🛒 Consumer (Buyer) Portal
- **My Orders** — Live table of all placed orders with status badges
- **Order Details** — Item-level breakdown with auto-calculated totals

### 🏭 Supplier Portal
- **Materials Hub** — Manage your raw material catalog
- **Inventory & Pricing** — Edit stock volumes and unit prices inline
- **Contracts** — View and monitor active supply agreements

### 🌍 Global Marketplace
- Browse all available raw materials in a high-fidelity product grid
- Public access, no login required

### 📊 Role-Aware Dashboard
- Real-time metrics adapt based on your role (Buyer vs Supplier)
- Buyers see Order Count & Pending Shipments
- Suppliers see Active Contracts & Material Asset Count

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Node.js 18+
- PostgreSQL 14+
- Maven

---

### 1. Database Setup

Create a PostgreSQL database:
```sql
CREATE DATABASE supply_chain;
```

---

### 2. Backend Setup

```bash
cd backend
```

Set your database password as an environment variable and start the server:

**PowerShell:**
```powershell
$env:DB_PASSWORD="your_postgres_password"; .\mvnw.cmd spring-boot:run
```

**CMD:**
```cmd
set DB_PASSWORD=your_postgres_password && mvnw.cmd spring-boot:run
```

Backend runs at: **`http://localhost:8080`**

API Docs (Swagger): **`http://localhost:8080/swagger-ui.html`**

---

### 3. Frontend Setup

```bash
cd frontend
npm install
npm run start
```

Frontend runs at: **`http://localhost:4200`**

---

## 🔌 API Blueprint

### Authentication

| Role | Endpoint | Method |
|---|---|---|
| Consumer Login | `/api/consumers/login` | `POST` |
| Consumer Register | `/api/consumers/register` | `POST` |
| Supplier Login | `/api/suppliers/login` | `POST` |

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
| `/api/raw-materials` | `GET` | Browse global marketplace |

> All protected endpoints require an `Authorization: Bearer <token>` header.

---

## 🔒 Security Note

**Never commit your database password.** The `application.properties` file uses `${DB_PASSWORD}` which must be set as an environment variable locally. This keeps credentials off of GitHub.

---

## 📸 Application Screens

| Screen | Description |
|---|---|
| `/auth/login` | Unified login with Buyer / Supplier role toggle |
| `/auth/register` | Registration with role selection |
| `/dashboard` | Role-aware overview with live metrics |
| `/dashboard/consumer/orders` | Buyer order history table |
| `/dashboard/consumer/orders/:id` | Order item details with invoice summary |
| `/dashboard/supplier/materials` | Supplier material catalog |
| `/dashboard/supplier/inventory` | Inline stock & price editor |
| `/dashboard/supplier/contracts` | Active contract viewer |
| `/marketplace` | Public raw materials grid |

---

## 👥 Team

| Team | Responsibility |
|---|---|
| **Team A (Consumer)** | Buyer Dashboard & Order Management UI |
| **Team B (Supplier)** | Supplier Portal, Inventory & Contracts UI |

---

## 📄 License

This project is built for academic and demonstration purposes.
