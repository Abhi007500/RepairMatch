# RepairMatch 🛠️⚡
> **An algorithmic, on-demand household and daily-life repair marketplace that matches service requests to verified, specialized technicians.**

[![Java 21+](https://img.shields.io/badge/Java-21%2B-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot 3.3.4](https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MongoDB Atlas](https://img.shields.io/badge/MongoDB-Atlas%20%2F%206%2B-47A248?logo=mongodb&logoColor=white)](https://www.mongodb.com/atlas)
[![Spring Data MongoDB](https://img.shields.io/badge/Spring%20Data-MongoDB-47A248?logo=spring&logoColor=white)](https://spring.io/projects/spring-data-mongodb)
[![React 18](https://img.shields.io/badge/React-18-61DAFB?logo=react&logoColor=black)](https://react.dev/)
[![Vite 5](https://img.shields.io/badge/Vite-5-646CFF?logo=vite&logoColor=white)](https://vitejs.dev/)
[![Tailwind CSS 3](https://img.shields.io/badge/Tailwind%20CSS-3-38B2AC?logo=tailwindcss&logoColor=white)](https://tailwindcss.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 🌐 Live Demo & Deployment Status

| Service | Environment | URL | Status |
| :--- | :--- | :--- | :--- |
| **Web Application** | Cloud Production | `https://your-deployment-url.com` *(Placeholder — Add cloud URL upon deployment)* | Pending Deployment |
| **Backend REST API** | Cloud Production | `https://api.your-deployment-url.com` *(Placeholder — Add cloud URL upon deployment)* | Pending Deployment |
| **Local Frontend** | Development | [http://localhost:5173](http://localhost:5173) | Ready |
| **Local API Base** | Development | [http://localhost:8080](http://localhost:8080) | Ready |
| **Health Endpoint** | Development | [http://localhost:8080/api/health](http://localhost:8080/api/health) | `{"status":"UP"}` |

---

## 📑 Table of Contents
1. [Project Overview](#-project-overview)
2. [Problem Statement](#-problem-statement)
3. [Key Capabilities](#-key-capabilities)
4. [Technology Stack](#-technology-stack)
5. [System Architecture](#-system-architecture)
6. [Repository Structure](#-repository-structure)
7. [Database Architecture & Migrations](#-database-architecture--migrations)
8. [User Workflows](#-user-workflows)
   - [Customer Journey](#1-customer-workflow)
   - [Technician Console](#2-technician-workflow)
   - [Admin Governance](#3-administrator-workflow)
9. [Intelligent Matching Engine](#-intelligent-technician-matching-engine)
10. [Booking Lifecycle State Machine](#-booking-lifecycle-state-machine)
11. [Authentication & Security](#-authentication--security)
12. [Mobile OTP Architecture](#-mobile-otp-architecture)
13. [Online Payment Architecture (Razorpay)](#-online-payment-architecture-razorpay)
14. [Local Setup & Getting Started](#-local-setup--getting-started)
15. [Environment Variables Reference](#-environment-variables-reference)
16. [REST API Reference](#-rest-api-reference)
17. [Testing & Verification](#-testing--verification)
18. [Production Deployment Guide](#-production-deployment-guide)
19. [Future Roadmap](#-future-roadmap)
20. [Author & Contact](#-author--contact)

---

## 📌 Project Overview

**RepairMatch** is a full-stack, modular on-demand repair marketplace designed to eliminate the unpredictability of local utility and appliance repairs. Unlike standard directory websites that present unverified contact lists, RepairMatch implements an **algorithmic multi-factor matching engine** that analyzes repair requests against technician profiles in real time. 

The application matches users based on hardware brand specialization, problem diagnosis, Haversine geographic distance, schedule non-conflict, verified customer rating history, and pricing competitiveness. 

The project is built as a clean modular monolith using **Java 21**, **Spring Boot 3.3**, and **MongoDB Atlas** on the backend, paired with a responsive **React 18** Single Page Application styled with **Tailwind CSS**.

---

## ⚠️ Problem Statement

Homeowners and consumers face persistent challenges when sourcing technical help for appliances and utilities:
- **Generic Listings:** Platforms categorize contractors into broad buckets (e.g. "Electrician" or "Appliance Repair"), failing to capture whether the technician has certified experience with a specific brand (e.g., Apple logic boards vs. Dell laptops, or Daikin inverter compressors vs. standard window units).
- **Dispatch Clashes:** Requests are often routed to contractors who are already occupied with active bookings or located beyond a realistic travel radius.
- **Hidden Fees:** Pricing is rarely disclosed upfront; consumers are often quoted arbitrary amounts after diagnostic inspection.
- **Unverified Reviews:** Without mandatory booking validation, review platforms suffer from spam and unverified feedback.
- **Lack of Traceability:** Traditional service scheduling lacks a formal state machine, leading to lost requests, missed appointments, and zero audit logs.

RepairMatch addresses each challenge through strict domain boundaries, automated constraint checking, cryptographic payment verification, and an auditable lifecycle model.

---

## 🚀 Key Capabilities

- **16 Curated Repair Categories:**
  - *Electronics & Computing:* Smartphones, Laptops, Tablets, Televisions.
  - *Major Home Appliances:* Air Conditioners, Refrigerators, Washing Machines, Microwaves.
  - *Home Utilities:* Geysers, Water Purifiers (RO), Fans/Coolers, Inverters & Batteries.
  - *Trade Services:* Electrical Work, Plumbing, Carpentry, Furniture Repair.
- **Two-Stage Multi-Factor Matching Engine:** Filters unqualified candidates via mandatory constraints, then scores qualified technicians using a weighted multi-variable formula (0–100%).
- **Deterministic Booking Lifecycle:** State machine enforcing valid status progressions (`PENDING` ➔ `ACCEPTED` ➔ `IN_PROGRESS` ➔ `COMPLETED`, plus `CANCELLED` and `REJECTED`) with an immutable `booking_timeline` audit log.
- **Dual-Mode Authentication:**
  - Standard email and BCrypt-hashed password sign-in with stateless JWT issuance.
  - Rate-limited mobile phone OTP authentication with 60s cooldown, 5-attempt lockout, and BCrypt-hashed token storage.
- **Online Payment Integration:** Full Razorpay gateway workflow with backend HMAC-SHA256 signature verification, idempotency protection, and payment state tracking (`PENDING`, `AUTHORIZED`, `PAID`, `FAILED`, `REFUNDED`, `CANCELLED`).
- **Verified Review Engine:** 1-review-per-completed-booking restriction that atomically recalculates technician average ratings and review counts.
- **Technician Management Portal:** Active on-duty/off-duty toggle switch, live job dispatch queue, and diagnostic-to-completion invoicing.
- **Admin Governance Portal:** Platform metrics dashboard, KYC credential verification queue, and unified user registry.
- **Accessibility & Theme System:** System-aware Dark Mode and Light Mode with zero layout shift and Tailwind `class` styling.

---

## 🛠️ Technology Stack

### Backend Architecture
| Component | Technology | Rationale |
| :--- | :--- | :--- |
| **Runtime & Language** | Java 21 LTS / Java 23 | Modern language features, strong type safety, and high-performance concurrency |
| **Framework** | Spring Boot 3.3.4 | Dependency injection, enterprise security, and production-ready monitoring |
| **Security & JWT** | Spring Security 6, JJWT 0.12.6 | Stateless bearer token authentication and role-based endpoint protection |
| **Database Access** | Spring Data MongoDB | High-performance document mapping, indexed query execution, and atomic updates |
| **Data Initialization** | Spring Boot `CommandLineRunner` | Automatic, idempotent baseline dataset seeding across 16 categories and 8 demo users |
| **Testing Suite** | JUnit 5, Mockito, Spring Boot Test | Comprehensive unit, mock web, and integration test coverage |

### Frontend Architecture
| Component | Technology | Rationale |
| :--- | :--- | :--- |
| **Framework & Tooling** | React 18, Vite 5 | Lightning-fast HMR dev server and optimized production build tree-shaking |
| **Styling & Icons** | Tailwind CSS 3, Lucide React | Utility-first responsive design, dark mode tokens, and lightweight icons |
| **Routing** | React Router DOM 6 | Client-side route declarations, protected routes, and role-based redirection |
| **HTTP Client** | Axios | Interceptors for automated JWT bearer token injection and 401 response handling |

### Database & Storage
| Component | Technology | Rationale |
| :--- | :--- | :--- |
| **Primary Database** | MongoDB Atlas (or MongoDB 6+) | Scalable document model, native JSON alignment, high-speed geospatial and compound indexing |
| **Test Database** | In-Memory MongoDB Server (`mongo-java-server`) | Zero-dependency, hermetic integration testing running directly in the JVM without external Daemons |

---

## 🏛️ System Architecture

```
                          ┌───────────────────────────────┐
                          │     React 18 + Vite SPA       │
                          │   (Desktop & Mobile Web)      │
                          └───────────────┬───────────────┘
                                          │ HTTP / JSON
                                          │ (Bearer JWT / Axios)
                                          ▼
┌────────────────────────────────────────────────────────────────────────────────────────┐
│ Spring Boot 3.3 Backend Application (Port 8080)                                         │
│                                                                                        │
│  ┌─────────────────────────┐  ┌─────────────────────────┐  ┌─────────────────────────┐ │
│  │ Security & Filters      │  │ Domain Controllers      │  │ Business Services       │ │
│  │ • JwtAuthFilter         │  │ • AuthController        │  │ • AuthService           │ │
│  │ • SecurityConfig (RBAC) │  │ • MatchingController    │  │ • MatchingEngineService │ │
│  │ • CorsConfig            │  │ • BookingController     │  │ • BookingStateMachine   │ │
│  │ • GlobalExceptionHandler│  │ • PaymentController     │  │ • PaymentService        │ │
│  └─────────────────────────┘  │ • CatalogController     │  │ • TechnicianService     │ │
│                               │ • TechnicianController  │  │ • ReviewService         │ │
│                               │ • AdminController       │  │ • CustomerService       │ │
│                               └─────────────────────────┘  └─────────────────────────┘ │
│                                            │                                           │
│                                            ▼
│                               ┌─────────────────────────┐
│                               │ Spring Data Mongo Repos │
│                               └────────────┬────────────┘
└────────────────────────────────────────────┼───────────────────────────────────────────┘
                                             │ Wire Protocol / Connection String
                                             ▼
                                ┌─────────────────────────┐
                                │ MongoDB Atlas Cluster   │
                                │ (or Local Port 27017)   │
                                │ • Document Collections  │
                                │ • Compound & 2D Indexes │
                                │ • MongoDataInitializer  │
                                └─────────────────────────┘
```

---

## 📂 Repository Structure

```
Repairmatch/
├── .env.example                       # Master environment configuration template
├── .gitignore                         # Comprehensive ignore rules (Java, Node, IDEs, OS)
├── README.md                          # Project documentation
├── start-dev.sh                       # Local development startup script
│
├── backend/                           # Spring Boot 3 Java Backend
│   ├── pom.xml                        # Maven project descriptor & dependencies
│   └── src/
│       ├── main/
│       │   ├── java/com/repairmatch/
│       │   │   ├── RepairMatchApplication.java
│       │   │   ├── common/            # Cross-cutting concerns
│       │   │   │   ├── config/        # Security, CORS, MongoDataInitializer configs
│       │   │   │   ├── exception/     # GlobalExceptionHandler, Custom Exceptions
│       │   │   │   ├── security/      # JwtTokenProvider, UserPrincipal, JwtAuthFilter
│       │   │   │   └── utils/         # GeoUtils (Haversine distance calculations)
│       │   │   └── modules/           # Cohesive business domain packages
│       │   │       ├── admin/         # Operations stats, KYC approvals
│       │   │       ├── auth/          # Login, Register, Mobile OTP, SMS providers
│       │   │       ├── booking/       # Booking state machine, lifecycle, timeline
│       │   │       ├── catalog/       # Categories, Brands, Models, Problem Types
│       │   │       ├── matching/      # Multi-factor matching scoring algorithm
│       │   │       ├── payment/       # Razorpay gateway, signature verification
│       │   │       ├── review/        # Customer feedback & reputation calculation
│       │   │       ├── technician/    # Profiles, skills, availability toggles
│       │   │       └── user/          # User entities, addresses, profiles
│       │   └── resources/
│       │       └── application.yml    # Externalized configuration properties (Atlas URI)
│       └── test/                      # Automated test suites (42 test cases)
│
└── frontend/                          # React 18 + Vite Frontend
    ├── .env.example                   # Frontend environment template
    ├── package.json                   # NPM dependencies and scripts
    ├── vite.config.js                 # Vite server & API proxy config
    ├── tailwind.config.js             # Tailwind design system configuration
    ├── postcss.config.js              # PostCSS plugins
    ├── index.html                     # HTML entry point
    └── src/
        ├── api/client.js              # Configured Axios client with JWT interceptors
        ├── context/                   # React context providers (AuthContext, ThemeContext)
        ├── components/common/         # Shared UI elements (Navbar, Footer, Badge, Rating)
        └── features/                  # Feature pages & components
            ├── admin/                 # AdminDashboardPage (Metrics, KYC, User list)
            ├── auth/                  # LoginPage (Dual-mode), RegisterPage
            ├── customer/              # RepairWizardPage (5 steps), CustomerBookingsPage
            ├── home/                  # HomePage (Hero, Categories, Value props)
            ├── payment/               # PaymentModal (Razorpay checkout & receipt)
            └── technician/            # TechnicianDashboardPage (Queue, Status, Invoicing)
```

---

## 🗄️ Database Architecture & Collections

RepairMatch is backed by **MongoDB Atlas** using **Spring Data MongoDB**. Persistence models use high-performance JSON-native document collections with explicit indexes, `@DBRef` relational linkages, and foreign keys stored directly on child documents for O(1) indexed lookups.

```
┌─────────────────┐       ┌────────────────────────┐       ┌─────────────────┐
│      users      │──1:N──│       addresses        │       │   categories    │
│  (id, role,     │       │ (lat, lng, street,     │       │ (id, slug, icon,│
│   password_hash)│       │  userId)               │       │  brands [@DBRef])│
└────────┬────────┘       └────────────────────────┘       └────────┬────────┘
         │ 1:1                                                      │ 1:N
┌────────┴────────┐       ┌────────────────────────┐       ┌────────┴────────┐
│technician_profiles│─1:N─│    catalog models      │       │  problem_types  │
│(radius, rating, │       │(brands, models,        │       │ (title, fee,    │
│ fee, kyc_status,│       │ categoryIds)           │       │  categoryId)    │
│ categories,     │       └────────────────────────┘       └─────────────────┘
│ brands, problems)
└────────┬────────┘
         │ 1:N
┌────────┴────────┐       ┌────────────────────────┐       ┌─────────────────┐
│    bookings     │──1:N──│    booking_timeline    │       │     reviews     │
│(status, amount, │       │ (state, timestamp,     │       │(rating 1-5,     │
│ payment_status, │       │  bookingId)            │       │ customer, tech, │
│ @Version lock)  │       └────────────────────────┘       │ bookingId)      │
└────────┬────────┘                                        └─────────────────┘
         │ 1:N
┌────────┴────────┐       ┌────────────────────────┐
│    payments     │       │   otp_verifications    │
│ (provider,      │       │ (phoneNumber, otpHash, │
│  orderId, sig,  │       │  attempts, verified,   │
│  bookingId)     │       │  expiresAt)            │
└─────────────────┘       └────────────────────────┘
```

### MongoDB Indexing Strategy:
1. **Unique Indexes:**
   - `users.email` — Prevents duplicate registrations.
   - `categories.slug` & `brands.slug` — Fast slug-based route resolution.
   - `bookings.bookingReference` — Guarantees unique human-readable booking IDs (`RM-2026-XXXX`).
   - `reviews.bookingId` — Guarantees exactly one review per booking.
   - `payments.providerOrderId` — Prevents duplicate gateway order mapping.
2. **Compound & Query Optimization Indexes:**
   - `bookings`: `[technicianId ASC, scheduledDate ASC, timeSlot ASC, status ASC]` — Millisecond-level conflict detection for the matching engine.
   - `bookings`: `[customerId ASC, createdAt DESC]` — Instant customer history feeds.
   - `bookings`: `[technicianId ASC, createdAt DESC]` — Real-time technician dispatch queues.
   - `otp_verifications`: `[phoneNumber ASC, verified ASC, createdAt DESC]` — High-speed cooldown and attempt verification.
   - `technician_profiles`: `[verificationStatus ASC, isAvailable ASC]` — Ultra-fast first-stage matching filtering.
3. **Concurrency Control:**
   - `Booking` documents employ `@Version Long version` providing optimistic locking against simultaneous technician assignment or status change race conditions.
4. **Idempotent Baseline Data Seeding:**
   - Spring Boot `MongoDataInitializer` automatically provisions baseline categories, brands, problem types, models, addresses, 8 demo users with BCrypt credentials, technician profiles, bookings, timelines, and sample reviews upon initial boot if collections are empty.

---

## 👤 User Workflows

### 1. Customer Workflow
```
[Select Category] ➔ [Specify Brand & Model] ➔ [Choose Symptom] ➔ [Select Address & Slot] ➔ [Ranked Technicians] ➔ [Book Service] ➔ [Track Live State] ➔ [Pay Online via Razorpay] ➔ [Submit Review]
```
1. **Diagnosis:** Customer launches the 5-step Repair Diagnostic Wizard (`/wizard`).
2. **Device Details:** For technical categories (e.g., Laptops, ACs), the customer selects their device brand and specific model. For trade services (Plumbing, Carpentry), brand selection is skipped automatically.
3. **Problem Selection:** Customer picks the exact fault from common diagnosed problems with baseline cost estimates.
4. **Location & Schedule:** Customer selects their saved address (or inputs latitude/longitude) and picks an appointment date and time slot.
5. **Matched Recommendations:** Multi-factor matching engine evaluates all qualified candidates and returns ranked results with transparency badges.
6. **Booking & Tracking:** Customer confirms the booking and monitors real-time status progressions (`/bookings`).
7. **Payment & Review:** Upon job completion, customer pays online and submits a 1-to-5 star verified review.

### 2. Technician Workflow
```
[Login / Register] ➔ [Submit KYC Details] ➔ [Toggle On-Duty] ➔ [Inspect Job Queue] ➔ [Accept / Reject] ➔ [Start Work] ➔ [Invoice & Complete] ➔ [Track Ratings]
```
1. **Onboarding:** Technicians register with their coverage radius, inspection rate, and domain skills. Initial profile status is set to `PENDING` approval.
2. **Duty Toggle:** Verified technicians activate their availability toggle switch on `/technician` to start receiving match requests.
3. **Job Dispatch:** Incoming booking requests appear in the technician console with problem details, device brand/model, address, and requested slot.
4. **Job Execution:** Technician accepts the request, marks `In Progress` upon arrival, and logs final repair charges upon completion.
5. **Reputation Monitoring:** Technicians view customer ratings and feedback transparently on their dashboard.

### 3. Administrator Workflow
```
[Admin Console] ➔ [Review Platform Metrics] ➔ [Inspect Pending KYC] ➔ [Approve / Reject Credentials] ➔ [Manage User Directory]
```
1. **Platform Metrics:** Real-time visibility into total registered accounts, active technicians, total bookings, and platform activity.
2. **KYC Document Verification:** Review technician government credentials and identity documents; approve or reject verification with a single action.
3. **User Directory:** Filterable registry of customers, technicians, and administrators with account status inspection.

---

## 🧠 Intelligent Technician Matching Engine

When a customer submits a repair request, the engine (`POST /api/matching/find`) processes all candidate technicians through a two-stage evaluation:

```
+-----------------------------------------------------------------------+
|                       Incoming Repair Request                         |
|      (Category ID, Brand ID, Problem Type ID, Latitude, Longitude)    |
+-----------------------------------------------------------------------+
                                   |
                                   v
+-----------------------------------------------------------------------+
|                       STAGE 1: HARD FILTERS                           |
|  [x] Verification Gate:    technician.verification_status == VERIFIED |
|  [x] Duty Gate:            technician.is_available == true           |
|  [x] Category Gate:        technician covers selected category_id     |
|  [x] Proximity Gate:       Haversine distance <= service_radius_km    |
|  [x] Schedule Gate:        no conflicting booking in requested slot   |
+-----------------------------------------------------------------------+
                                   | (Candidates passing all gates)
                                   v
+-----------------------------------------------------------------------+
|                    STAGE 2: MULTI-FACTOR SCORING                      |
|                                                                       |
|  Factor                     Weight   Computation Method               |
|  -------------------------------------------------------------------  |
|  Brand Specialization        25%     Has brand in skill profile       |
|  Problem Expertise           20%     Has problem in skill profile     |
|  Geographical Proximity      20%     Linear decay: 1 - (dist / radius)|
|  Customer Rating Average     15%     Linear scale: (rating / 5.0) * 15|
|  Completed Job Track Record  10%     Tiered: >=50: 10, >=20: 7, >=5: 4|
|  Pricing Competitiveness     10%     Ratio: category_avg / fee        |
+-----------------------------------------------------------------------+
                                   |
                                   v
+-----------------------------------------------------------------------+
|                   FINAL SCORE (0 - 100%) + BADGES                     |
|  • "Brand Specialist"   • "Nearby (X km)"   • "Top Rated 4.9★"        |
+-----------------------------------------------------------------------+
```

### Proximity Calculation (Haversine Formula)
Geographic distance $d$ between customer coordinates $(\phi_1, \lambda_1)$ and technician coordinates $(\phi_2, \lambda_2)$ is calculated in `GeoUtils.java`:
$$a = \sin^2\left(\frac{\Delta\phi}{2}\right) + \cos(\phi_1)\cos(\phi_2)\sin^2\left(\frac{\Delta\lambda}{2}\right)$$
$$d = 2 \cdot R \cdot \text{atan2}\left(\sqrt{a}, \sqrt{1-a}\right) \quad \text{where } R = 6371 \text{ km}$$

---

## 🔄 Booking Lifecycle State Machine

Bookings follow a formal, validated state machine enforced by `BookingStateMachine.java`.

### Valid Transition Matrix:

| From State | Allowed Target State | Triggering Role | Automated Side Effects |
| :--- | :--- | :--- | :--- |
| `PENDING` | `ACCEPTED` | Technician | Adds `ACCEPTED` timeline entry |
| `PENDING` | `REJECTED` | Technician | Frees up schedule slot |
| `PENDING` | `CANCELLED` | Customer | Cancels booking before technician confirmation |
| `ACCEPTED` | `IN_PROGRESS` | Technician | Updates service start timestamp |
| `ACCEPTED` | `CANCELLED` | Customer | Cancels booking before technician arrival |
| `IN_PROGRESS` | `COMPLETED` | Technician | Increments technician `completed_jobs_count`, unlocks review eligibility |
| `COMPLETED` | *(Terminal)* | — | Read-only state; eligible for verified customer review |
| `CANCELLED` | *(Terminal)* | — | Read-only state |
| `REJECTED` | *(Terminal)* | — | Read-only state |

*Any unauthorized or illegal state transition attempts immediately return `400 Bad Request`.*

---

## 🔐 Authentication & Security

- **Stateless JWT Authorization:** Every authenticated request requires a Bearer JWT in the `Authorization` header. Tokens are signed with HMAC-SHA256 using `app.jwt.secret` (minimum 256 bits).
- **BCrypt Password Hashing:** Passwords are never stored in plaintext; all user credentials use salted BCrypt password hashing.
- **Role-Based Access Control (RBAC):** Every API endpoint is secured with Spring Security annotations (`@PreAuthorize("hasRole('ROLE_...')")`).
- **CORS Defense:** CORS policy explicitly restricts allowed origins (`CORS_ALLOWED_ORIGINS`) to authorized frontend clients.

---

## 📱 Mobile OTP Architecture

RepairMatch provides secure, production-ready phone-number authentication for customers and technicians alongside standard email sign-in.

```
[User Browser]                      [Backend AuthService]               [MongoDB Database]
      │                                       │                                   │
      │── 1. POST /api/auth/otp/send ────────>│                                   │
      │      (phone: "9876543210")            │── 2. Validate Indian phone format │
      │                                       │── 3. Check 60s cooldown ─────────>│
      │                                       │── 4. Generate SecureRandom OTP    │
      │                                       │── 5. Hash OTP with BCrypt         │
      │                                       │── 6. Persist hash & expiry ──────>│
      │                                       │── 7. Dispatch SMS (Twilio / Dev)  │
      │<── 8. Return 200 OK (no OTP in body)──│                                   │
      │                                       │                                   │
      │── 9. POST /api/auth/otp/verify ──────>│                                   │
      │      (phone & 6-digit code)           │── 10. Fetch active record ───────>│
      │                                       │── 11. Check expiry & attempts     │
      │                                       │── 12. BCrypt matches check        │
      │                                       │   [If invalid: increment count]   │
      │                                       │   [If valid: mark is_verified]───>│
      │<── 13. Return 200 OK with Bearer JWT ─│                                   │
```

### Security Safeguards:
1. **Zero Plaintext OTP Storage:** Verification codes are hashed with BCrypt prior to persistence in `otp_verifications`.
2. **Indian Phone Number Validation:** Enforces standard 10-digit mobile numbering starting with 6, 7, 8, or 9 (`^[6-9]\d{9}$`).
3. **Resend Cooldown:** Strict 60-second cooldown enforced at both database and frontend UI levels.
4. **Brute-Force Rate Limiting:** Verifications are locked after 5 failed attempts (`OTP_MAX_ATTEMPTS`).
5. **Single-Use Invalidation:** Successful verification marks `is_verified = true` immediately, preventing replay attacks.
6. **Zero Secret Leaks:** OTP values are never returned in HTTP responses or written to production logs.
7. **SMS Provider Abstraction:** Configurable `SmsOtpProvider` interface enables instant switching between local dev simulation (`dev`) and live SMS gateways (`twilio`, `fast2sms`).

---

## 💳 Online Payment Architecture (Razorpay)

Payment for technician inspection and repair services uses Razorpay with cryptographic backend verification:

```
[Customer Browser]                     [Backend API]                    [Razorpay Gateway]
        │                                    │                                  │
        │── 1. POST /api/payments/create-ord>│                                  │
        │      (bookingId)                   │── 2. Verify ownership & amount   │
        │                                    │── 3. Generate Order ID ─────────>│
        │                                    │── 4. Create PENDING in DB        │
        │<── 5. Return order details & key ──│                                  │
        │                                    │                                  │
        │── 6. Open Razorpay Checkout Modal ───────────────────────────────────>│
        │<── 7. Receive payment callback (order_id, payment_id, signature) ─────│
        │                                    │                                  │
        │── 8. POST /api/payments/verify ───>│                                  │
        │      (paymentId, orderId, sig)     │── 9. Compute HMAC-SHA256 digest  │
        │                                    │── 10. Constant-time equality check│
        │                                    │── 11. Update payment: PAID       │
        │                                    │── 12. Update booking: PAID       │
        │                                    │── 13. Append timeline audit log  │
        │<── 14. 200 OK Verified Receipt ────│                                  │
```

### Key Technical Details:
- **Cryptographic Verification:** Signatures are computed via standard Java `javax.crypto.Mac` (`HmacSHA256`) and compared in constant time (`MessageDigest.isEqual`) to protect against timing attacks.
- **Idempotent Handling:** Duplicate callbacks for already-verified payments return the existing `PAID` record safely without duplicate accounting.
- **Payment Lifecycle States:** Tracks `PENDING`, `AUTHORIZED`, `PAID`, `FAILED`, `REFUNDED`, and `CANCELLED`.
- **Audit Integration:** Payment confirmations automatically advance booking payment indicators and write audit events to `booking_timeline`.

---

## 🚀 Local Setup & Getting Started

### System Prerequisites
- **Java Development Kit:** OpenJDK 21 or 23
- **Node.js:** Node.js 18+ (tested on Node 23) and npm
- **Database:** MongoDB Atlas (Free Tier M0) or local MongoDB Community 6+
- **Build Tool:** Apache Maven 3.9+

---

### Step 1: Database Setup (MongoDB Atlas or Local MongoDB)

#### Option A: MongoDB Atlas (Cloud — Recommended)
1. Sign up for a free account at [MongoDB Atlas](https://www.mongodb.com/atlas).
2. Create an **M0 Free Cluster** in your preferred region.
3. Under **Security ➔ Database Access**, create a database user (e.g., `repairmatch_admin` with password).
4. Under **Security ➔ Network Access**, click **Add IP Address** and select **Allow Access from Anywhere** (`0.0.0.0/0`) or whitelist your current IP.
5. Under **Deployments ➔ Database**, click **Connect ➔ Drivers (Java)**, and copy your connection string:
   ```bash
   mongodb+srv://<username>:<password>@<cluster-name>.mongodb.net/repairmatch?retryWrites=true&w=majority
   ```

#### Option B: Local MongoDB (Homebrew / System)
```bash
# macOS (Homebrew)
brew tap mongodb/brew
brew install mongodb-community
brew services start mongodb-community

# Linux (Ubuntu / Debian)
sudo systemctl start mongod
```
*On initial startup, `MongoDataInitializer` automatically detects empty collections and provisions all 16 categories, 16 brands, 10 models, 10 problem types, and 8 demo user accounts.*

---

### Step 2: Environment Configuration
Copy the configuration template to `.env`:
```bash
cp .env.example .env
```
Open `.env` and set `MONGODB_URI` to your MongoDB Atlas connection string (or keep the default `mongodb://localhost:27017/repairmatch` for local MongoDB):
```bash
MONGODB_URI=mongodb+srv://<username>:<password>@<cluster-name>.mongodb.net/repairmatch?retryWrites=true&w=majority
MONGODB_DATABASE=repairmatch
```

---

### Step 3: Run the Development Stack

#### Option A: One-Command Startup (Recommended)
A zero-dependency bash script verifies MongoDB, compiles and launches the Spring Boot backend, starts Vite, and verifies health:
```bash
chmod +x start-dev.sh
./start-dev.sh
```

#### Option B: Manual Startup (Two Terminals)

**Terminal 1 — Spring Boot Backend:**
```bash
cd backend
mvn spring-boot:run
```
- API Base: `http://localhost:8080`
- Health Endpoint: `http://localhost:8080/api/health`

**Terminal 2 — React Vite Frontend:**
```bash
cd frontend
npm install
npm run dev
```
- Web Application: `http://localhost:5173`

---

## 👥 Pre-Seeded Test Accounts

The baseline seeder (`MongoDataInitializer.java`) pre-configures test accounts for all roles. The frontend login page includes convenient **1-Click Quick Demo Login** buttons:

| Role | Email | Phone Number | Password | Profile Highlights |
| :--- | :--- | :--- | :--- | :--- |
| **Customer** | `rahul@gmail.com` | `9876543210` | `password123` | Active customer with saved Bengaluru addresses and active bookings |
| **Technician (Electronics)** | `rajesh.tech@repairmatch.com` | `9811122233` | `password123` | Apple & Dell certified specialist (4.9★ rating, 126 completed repairs) |
| **Technician (Appliances)** | `amit.tech@repairmatch.com` | `9822233344` | `password123` | Daikin, LG & Samsung HVAC/cooling master (4.7★ rating, 94 repairs) |
| **Technician (Plumbing)** | `vikram.plumber@repairmatch.com` | `9833344455` | `password123` | Master plumber for residential pipe, leak & pump repairs (4.8★ rating) |
| **Administrator** | `admin@repairmatch.com` | `9999900001` | `password123` | Platform operations manager with KYC approval and user consoles |

---

## ⚙️ Environment Variables Reference

| Variable | Default Value | Description |
| :--- | :--- | :--- |
| `MONGODB_URI` | `mongodb://localhost:27017/repairmatch` | MongoDB connection URI (Atlas SRV or local) |
| `MONGODB_DATABASE` | `repairmatch` | Target MongoDB database name |
| `PORT` | `8080` | Backend HTTP listening port |
| `JWT_SECRET` | `404E6352...` *(dev default)* | 256-bit secret key for HMAC-SHA256 JWT signing |
| `JWT_EXPIRATION_MS` | `86400000` | JWT token lifetime (24 hours) |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://localhost:3000` | Allowed frontend origins |
| `OTP_EXPIRATION_MINUTES`| `5` | OTP validity window |
| `OTP_COOLDOWN_SECONDS`  | `60` | Minimum wait between OTP resends |
| `OTP_MAX_ATTEMPTS`      | `5` | Failed attempts before lockout |
| `SMS_PROVIDER`          | `dev` | SMS provider (`dev`, `twilio`, `fast2sms`) |
| `SMS_API_KEY`           | *(empty)* | Twilio Account SID or SMS API Key |
| `SMS_API_SECRET`        | *(empty)* | Twilio Auth Token |
| `SMS_SENDER_ID`         | `RepairMatch` | SMS Sender ID / phone number |
| `PAYMENT_PROVIDER`      | `razorpay` | Payment provider (`razorpay` or `mock`) |
| `RAZORPAY_KEY_ID`       | `rzp_test_mockKey123456` | Razorpay Key ID |
| `RAZORPAY_KEY_SECRET`   | `mockSecretKey987654` | Razorpay Key Secret (Backend only) |
| `RAZORPAY_CURRENCY`     | `INR` | Transaction currency |
| `VITE_API_BASE_URL`     | `http://localhost:8080/api` | Frontend API client base URL |

---

## 🔌 REST API Reference

### Authentication & Identification
- `POST /api/auth/register` — Register a new customer or technician
- `POST /api/auth/login` — Authenticate via email/password and receive JWT
- `POST /api/auth/otp/send` — Request a time-limited verification OTP
- `POST /api/auth/otp/verify` — Verify phone OTP and receive JWT
- `POST /api/auth/otp/resend` — Resend OTP subject to 60-second cooldown
- `GET /api/auth/me` — Retrieve authenticated user profile
- `GET /api/health` — Service health check endpoint

### Catalog & Categories
- `GET /api/catalog/categories` — List all 16 repair categories
- `GET /api/catalog/categories/{id}` — Category details with brands & problem types
- `GET /api/catalog/brands/{brandId}/models` — Device models for a brand
- `GET /api/catalog/categories/{categoryId}/problems` — Common symptoms for a category

### Matching & Discovery
- `POST /api/matching/find` — Execute two-stage algorithmic technician matching
- `GET /api/technicians/public/{id}` — Public profile and customer reviews for a technician

### Booking Lifecycle
- `POST /api/bookings` — Create a new repair request
- `GET /api/bookings/{id}` — Retrieve booking details and timeline
- `GET /api/bookings/customer` — List active and past customer bookings
- `GET /api/bookings/technician` — List technician job queue
- `PUT /api/bookings/{id}/status` — Advance booking state machine
- `POST /api/bookings/{id}/cancel` — Cancel active booking

### Online Payments
- `POST /api/payments/create-order` — Generate Razorpay payment order
- `POST /api/payments/verify` — Cryptographically verify HMAC-SHA256 signature
- `POST /api/payments/fail` — Record failed payment attempt
- `GET /api/payments/booking/{bookingId}` — Get payment status for booking

### Reviews & Reputation
- `POST /api/reviews` — Submit verified customer review
- `GET /api/reviews/technician/{id}` — Public reviews for a technician

### Technician & Admin Operations
- `GET /api/technician/profile` / `PUT /api/technician/profile` — Technician profile
- `PATCH /api/technician/availability` — Toggle on-duty availability
- `GET /api/admin/stats` — Platform metrics summary
- `GET /api/admin/technicians/pending` — Unverified technicians awaiting KYC check
- `PUT /api/admin/technicians/{id}/verify` — Approve or reject technician credentials
- `GET /api/admin/users` — User directory

---

## 🧪 Testing & Verification

### Automated Backend Tests
Run the entire JUnit 5 test suite (utilizes an in-memory MongoDB mock server for hermetic, zero-dependency testing without external database or Docker processes):
```bash
cd backend
mvn test
```
**Results:** **42 of 42 tests passing** (`BUILD SUCCESS`).

```
[INFO] Results:
[INFO] 
[INFO] Tests run: 42, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### Test Suite Coverage:
- `OtpAuthenticationTest` (8 tests) — Code generation, verification, attempt decrement, lockout at 5 failures, cooldown enforcement, single-use invalidation, Indian phone number format validation.
- `PaymentWorkflowTest` (7 tests) — Order creation, cryptographic HMAC-SHA256 signature verification, tamper rejection, duplicate callback idempotency, failure recording, authorization checks.
- `MatchingEngineTest` (5 tests) — Hard filters (KYC, duty, radius, slot conflicts) and weighted scoring calibration.
- `BookingLifecycleTest` (2 tests) — Forward transitions and illegal state jump rejection.
- `ReviewWorkflowTest` (2 tests) — Rating recalculation, duplicate review guards, and completed booking requirements.
- `AuthControllerTest` (5 tests) — Customer and technician registration, bad credentials handling, JWT issuance.
- `CatalogControllerTest` (4 tests) — Category, brand, model, and symptom hierarchy.
- `DomainModelAndSeedDataTest` (4 tests) — Baseline collection initialization, categories/brands/models, technician queries, BCrypt encryption.
- `CustomerAndTechnicianControllerTest` (3 tests) — Address books, profile updates, on-duty toggles.
- `EndToEndRepairJourneyTest` (1 test) — Comprehensive multi-step flow from diagnosis to payment and review.
- `HealthControllerTest` (1 test) — Actuator health check verification.

### Frontend Production Build
Compile and verify the React production bundle:
```bash
cd frontend
npm run build
```
**Result:** Built cleanly in 1.25s with 0 errors or warnings.

---

## 🚢 Production Deployment Guide

### 1. Backend Deployment on Render (Docker Runtime)

RepairMatch includes a production-ready, multi-stage [`backend/Dockerfile`](file:///Users/abhishekmishra/Documents/Repairmatch/backend/Dockerfile) engineered for **Render Web Services**:

1. Log in to [Render Dashboard](https://dashboard.render.com/) and click **New + ➔ Web Service**.
2. Connect your GitHub repository: `https://github.com/Abhi007500/RepairMatch`.
3. Configure the service settings:
   - **Name:** `repairmatch-backend`
   - **Region:** Choose the region closest to your MongoDB Atlas cluster (e.g., Singapore, Frankfurt, Oregon).
   - **Language / Runtime:** **Docker**
   - **Root Directory:** `backend`
   - **Dockerfile Path:** `Dockerfile` (relative to `backend` directory)
   - **Instance Type:** Free or Starter
4. Under **Environment Variables**, add the required secrets:
   | Key | Example / Description |
   | :--- | :--- |
   | `MONGODB_URI` | `mongodb+srv://<user>:<password>@cluster0.xxxx.mongodb.net/repairmatch?retryWrites=true&w=majority` |
   | `MONGODB_DATABASE` | `repairmatch` |
   | `JWT_SECRET` | Generate with `openssl rand -hex 32` |
   | `CORS_ALLOWED_ORIGINS` | `https://repairmatch.vercel.app,http://localhost:5173` |
   | `RAZORPAY_KEY_ID` | Your Razorpay Key ID |
   | `RAZORPAY_KEY_SECRET` | Your Razorpay Key Secret |
   | `SMS_PROVIDER` | `dev` (or `twilio` with `SMS_API_KEY`, `SMS_API_SECRET`, `SMS_SENDER_ID`) |
5. Under **Advanced ➔ Health Check Path**, set `/api/health`.
6. Click **Create Web Service**. Render will automatically build the container and deploy the service.

---

### 2. Frontend Deployment on Vercel

1. Import the repository on [Vercel](https://vercel.com/new).
2. Configure project settings:
   - **Framework Preset:** `Vite`
   - **Root Directory:** `frontend`
   - **Build Command:** `npm run build`
   - **Output Directory:** `dist`
3. Under **Environment Variables**, configure:
   ```bash
   VITE_API_BASE_URL=https://repairmatch-backend.onrender.com/api
   ```
4. Click **Deploy**.

---

### 3. Cloud Deployment Architecture

- **Database:** Managed MongoDB Atlas (Free M0 or Serverless Cluster with TLS, compound indexes, and automated daily backups).
- **Backend API:** Containerized Spring Boot 3 running on Render Docker runtime (dynamic port resolution via `${PORT:8080}`).
- **Frontend SPA:** Globally distributed edge CDN hosting on Vercel with automatic HTTPS.
- **SMS Gateway:** Configurable provider interface (`dev` for simulation, or live SMS dispatch via Twilio / Fast2SMS).
- **Payment Gateway:** Razorpay India gateway with cryptographic HMAC-SHA256 signature verification.

---

## 🔮 Future Roadmap

- [ ] **Real-Time WebSocket Dispatch:** Instant alert dispatch to on-duty technicians when a matching job is requested.
- [ ] **Spare Parts Inventory Tracking:** In-app parts catalogue allowing technicians to itemize replacement parts on invoices.
- [ ] **Native Mobile Application:** React Native / Flutter builds for field technicians with continuous background GPS tracking.
- [ ] **Automated Escrow Settlements:** Automated payout release from platform escrow to technician bank accounts upon customer job sign-off.

---

## 👨‍💻 Author & Contact

**Abhishek Mishra**  
- **GitHub:** [@Abhi007500](https://github.com/Abhi007500)  
- **Repository:** [https://github.com/Abhi007500/RepairMatch](https://github.com/Abhi007500/RepairMatch)  
- **Role:** Full-Stack Software Engineer  
- **Specialization:** Spring Boot, Distributed Systems, React, High-Reliability Architecture
