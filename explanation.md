# RawSource B2B Supply Chain Platform: Technical Architecture & System Documentation

## 1. Project Overview
RawSource is an enterprise-grade, full-stack B2B Supply Chain orchestration platform. Its primary goal is to securely manage raw material distribution between independent producers (Suppliers) and manufacturing organizations (Consumers). The system consists of a robust Spring Boot REST API ecosystem, a secure PostgreSQL database layout, and an asynchronous, modular Angular single-page application (SPA).

---

## 2. Backend Architecture (Spring Boot & JPA)
The backend is structured around a multi-layered REST pattern using Spring Data JPA.

### Database Entities & Relationships
The core of the system relies on the interplay between several domain models:
* **User Personas:** Defined as `Supplier` and `Consumer`.
* **The Goods:** `RawMaterial` acts as a generic commodity identifier (e.g., "Steel Sheet").
* **Logistics & Inventory:** 
  - `Availability` (Linked to a Supplier & RawMaterial) defines current warehouse volume and metrics (e.g., 400 tons).
  - `Pricing` tracks time-bound pricing agreements per material/supplier.
* **Fulfillment:** `Contract` acts as the legal backbone, binding a Consumer and Supplier at a locked price. `Order` and `OrderItem` handle direct supply fulfillment metrics.

### Key Backend Technical Hurdles Resolved
* **Bidirectional Relationship Recursion:** In standard Hibernate environments, circular loops between `RawMaterial` and `Availability` can crash Jackson parsers. We carefully introduced `@JsonProperty(access = Access.WRITE_ONLY)` constraints to block payload explosions while remaining open to incoming user payloads.
* **Lazy Initialization Proxies:** Because Spring loads mapped relationships lazily for speed, direct database queries would often drop important child fields. We wrote a targeted orchestration layer (`MarketplaceController`) that pulls records sequentially and resolves proxies securely into lightweight Data Transfer Objects (DTOs).

---

## 3. Frontend Architecture (Angular SPA)
The frontend UI enforces strong state persistence and strict separation of administrative privileges.

### Core Modules
* **Auth Guard & Services:** Uses token-based access checkpoints. If a Supplier tries viewing a Buyer route, Angular safely routes them out.
* **Component Lazy Loading:** Modules like the Dashboard load asynchronously to maintain highly scalable initial load times.

---

## 4. End-to-End Data Flow

### The Authentication Handshake
1. The user logs in via the UI.
2. The Angular `AuthInterceptor` intercepts outbound requests to pack cryptographic Bearer tokens.
3. Spring Boot validates signatures cleanly before running queries.

### Creating New Inventory (Workflow)
When a seller updates the marketplace:
- **Phase 1:** Creates a unique `RawMaterial` footprint.
- **Phase 2:** Assigns dynamic warehouse capacity limits (`Availability`).
- **Phase 3:** Validates a standardized spot contract rate.

### Buying/Order Execution
Clicking "Order" locks all related logistics instantaneously via a unified Spring Boot transaction layer—preventing database fragmentation.
