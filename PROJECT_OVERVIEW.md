# Ecommerce Management Platform - Complete Project Guide

## 📋 Project Overview

A **scalable, enterprise-grade e-commerce management platform** built with Spring Boot microservices architecture. The system features independently deployable services for products, orders, payments, and users, combined with a modern React + Vite frontend for a seamless shopping experience.

**Tech Stack:**
- **Backend:** Java 21 + Spring Boot 3.5.15 (microservices)
- **API Gateway:** Spring Cloud Gateway Server WebMVC
- **Security:** JWT (JSON Web Tokens) with Spring Security
- **Database:** MySQL 8+ (4 separate databases)
- **Frontend:** React 19 + Vite 6 + Tailwind CSS
- **Build Tool:** Maven (Maven Wrapper per service)

---

## 🏗️ Architecture Overview

### Microservices Topology

```
┌─────────────────────────────────────────────────────┐
│         Frontend Client (React + Vite)              │
│         Running on http://localhost:5173            │
└────────────────────┬────────────────────────────────┘
                     │ HTTP Requests
                     ▼
┌─────────────────────────────────────────────────────┐
│      API Gateway (Spring Cloud Gateway)             │
│      Running on http://localhost:8080               │
│      Routes traffic to backend microservices        │
└──────┬──────────┬──────────┬──────────┬─────────────┘
       │          │          │          │
       ▼          ▼          ▼          ▼
   Product    Order       Payment     User
   Service    Service     Service     Service
   :8081      :8082       :8083       :8084
   
   ↓          ↓           ↓           ↓
 MySQL      MySQL        MySQL       MySQL
 DB         DB           DB          DB
 (product)  (order)      (payment)   (user)
```

---

## 🛠️ Spring Boot Modules & Packages

### **API Gateway (Port 8080)**

**Key Dependencies:**
- `spring-cloud-starter-gateway-server-webmvc` - Web-based gateway routing
- `spring-boot-starter-security` - Security framework
- `spring-boot-starter-actuator` - Health checks & monitoring
- `jjwt` (v0.12.7) - JWT creation and validation

**Responsibilities:**
- Single entry point for all frontend requests
- Route traffic to appropriate microservices based on URL patterns
- JWT token validation before forwarding requests
- CORS configuration for frontend origin `http://localhost:5173`

---

### **Microservices (Product, Order, Payment, User Services)**

**Common Spring Dependencies:**
- `spring-boot-starter-web` - RESTful web service support
- `spring-boot-starter-data-jpa` - ORM (Hibernate) for database access
- `spring-boot-starter-validation` - Input validation (annotations)
- `mysql-connector-j` - MySQL JDBC driver
- `lombok` - Reduce boilerplate code (getters, setters, constructors)
- `spring-boot-starter-test` - Testing framework (JUnit, Mockito)

**Architecture Pattern:**
Each microservice follows **layered architecture:**
```
Controller Layer (REST endpoints) 
       ↓
Service Layer (Business logic)
       ↓
Repository Layer (JPA/Database access)
       ↓
Database (MySQL)
```

---

## 📊 Database Architecture

### 4 Independent MySQL Databases

Each microservice has its own dedicated database to maintain data isolation and autonomy:

#### **1. ecomv1_product_db** (Product Service)
```sql
Tables:
- products (product details, price, seller info)
- inventory (stock levels, availability tracking)
```

#### **2. ecomv1_order_db** (Order Service)
```sql
Tables:
- orders (order header, customer info, status)
- order_items (line items within an order)
```

#### **3. ecomv1_payment_db** (Payment Service)
```sql
Tables:
- payments (payment records, transaction status, amount)
```

#### **4. ecomv1_user_db** (User Service)
```sql
Tables:
- users (user credentials, profile, roles)
```

**Database Setup Command:**
```sql
-- Located in: db-helper-commands.sql
DROP DATABASE IF EXISTS ecomv1_order_db; 
DROP DATABASE IF EXISTS ecomv1_payment_db; 
DROP DATABASE IF EXISTS ecomv1_product_db; 
DROP DATABASE IF EXISTS ecomv1_user_db;

CREATE DATABASE ecomv1_order_db; 
CREATE DATABASE ecomv1_payment_db; 
CREATE DATABASE ecomv1_product_db; 
CREATE DATABASE ecomv1_user_db;
```

---

## 🔐 Authentication & Authorization System

### JWT Flow (3-Part System)

**1. SecurityConfig.java (Wiring Layer)**
- Configures Spring Security filter chain
- Disables CSRF (stateless JWT-based API)
- Sets session creation policy to STATELESS
- Injects JwtAuthenticationFilter into the request pipeline

**2. JwtAuthenticationFilter.java (Enforcement Layer)**
- Extends `OncePerRequestFilter` (runs once per request)
- **Public endpoints** (no token required):
  - `POST /api/users/register`
  - `POST /api/users/login`
- **Protected endpoints** (JWT required):
  - All other routes require valid Bearer token
- Extracts JWT from `Authorization: Bearer <token>` header
- Validates token using JwtService
- Returns 401 Unauthorized if token is invalid/missing

**3. JwtService.java (Token Operations)**
- Validates JWT signature using secret key from `application.yaml`
- Extracts claims (email, role, expiration)
- Handles token expiration checks

### User Roles (Authorization Levels)
- **Public** - No authentication required
- **CUSTOMER** - Authenticated user shopping/viewing products
- **SELLER** - Manage own products and orders
- **ADMIN** - Full platform management, all operations

---

## 🚀 Microservices & API Routes

### **1. API Gateway (Port 8080)**

**Route Mappings:**
```
/api/products/**  → Product Service (localhost:8081)
/api/orders/**    → Order Service (localhost:8082)
/api/payments/**  → Payment Service (localhost:8083)
/api/users/**     → User Service (localhost:8084)
```

---

### **2. Product Service (Port 8081)**

**Endpoints:**
```
POST   /api/products                  | Create product        | Auth: SELLER/ADMIN
GET    /api/products                  | List all products     | Auth: PUBLIC
GET    /api/products/{id}             | Get product details   | Auth: PUBLIC
GET    /api/products/sellers/{sellerId} | Get seller's products | Auth: SELLER/ADMIN
GET    /api/products/{id}/availability | Check stock status   | Auth: PUBLIC
PUT    /api/products/{id}/stock       | Update stock level    | Auth: SELLER/ADMIN
```

---

### **3. Order Service (Port 8082)**

**Core Endpoints:**
```
POST   /api/orders                    | Create new order      | Auth: CUSTOMER/ADMIN
GET    /api/orders/{id}               | Get order details     | Auth: CUSTOMER/ADMIN
GET    /api/orders/sellers/{sellerId} | Get seller's orders   | Auth: SELLER/ADMIN
PATCH  /api/orders/{id}/status        | Update order status   | Auth: ADMIN
```

**Dashboard Endpoints:**
```
GET    /api/orders/dashboard/admin/overview
       | Admin dashboard with total orders, revenue, metrics | Auth: SELLER/ADMIN

GET    /api/orders/dashboard/sellers/{sellerId}/metrics
       | Seller-specific performance metrics | Auth: SELLER/ADMIN
```

---

### **4. Payment Service (Port 8083)**

**Endpoints:**
```
POST   /api/payments                  | Process payment       | Auth: ADMIN
GET    /api/payments/order/{orderId}  | Get payment details   | Auth: ADMIN
```

---

### **5. User Service (Port 8084)**

**Endpoints:**
```
POST   /api/users/register            | Register new user     | Auth: PUBLIC
POST   /api/users/login               | Login & get JWT token | Auth: PUBLIC
GET    /api/users/{id}                | Get user profile      | Auth: ADMIN
GET    /api/users                     | List all users        | Auth: ADMIN
```

---

### **6. DataSeeder (No HTTP Port)**

**Purpose:** CLI-based data initialization
```
Configuration: spring.main.web-application-type=none
Calls: Backend APIs through API Gateway (localhost:8080)
Tasks:
- Seeds baseline users (admin, seller, customer)
- Seeds sample products
- Seeds sample orders
- Verifies payment linkage
- Uses presence checks (doesn't recreate existing data)
```

---

## 💻 Frontend Stack (React + Vite)

### **Technology:**
- **React 19.1.0** - UI library
- **Vite 6.3.5** - Build tool & dev server
- **Tailwind CSS 4.3.1** - Utility-first CSS framework
- **Axios 1.18.1** - HTTP client for API calls
- **React Router DOM 7.18.0** - Client-side routing
- **ESLint 9.25.0** - Code linting

### **File Structure:**
```
frontend-client/
├── src/
│   ├── api/
│   │   ├── authApi.js           (Login/Register API calls)
│   │   ├── axios.js             (Axios instance with interceptors)
│   │   ├── dashboardApi.js      (Dashboard data fetching)
│   │   ├── orderApi.js          (Order operations)
│   │   └── productApi.js        (Product queries)
│   ├── components/
│   │   ├── Navbar.jsx           (Navigation header)
│   │   ├── Footer.jsx           (Footer section)
│   │   ├── ProductCard.jsx      (Product display component)
│   │   ├── ProtectedRoute.jsx   (Route authentication guard)
│   │   └── RoleGuard.jsx        (Role-based access control)
│   ├── context/
│   │   ├── AuthContext.jsx      (Global auth state)
│   │   └── CartContext.jsx      (Global shopping cart state)
│   ├── pages/
│   │   ├── Login.jsx            (Login/Register page)
│   │   ├── Home.jsx             (Landing page)
│   │   ├── Products.jsx         (Product listing)
│   │   ├── ProductDetails.jsx   (Single product details)
│   │   ├── Cart.jsx             (Shopping cart)
│   │   ├── Orders.jsx           (Order history)
│   │   ├── CreateProduct.jsx    (Seller product creation)
│   │   └── AdminDashboard.jsx   (Admin panel)
│   ├── App.jsx                  (Main app component)
│   └── main.jsx                 (Entry point)
├── vite.config.js               (Vite configuration)
├── eslint.config.js             (ESLint rules)
└── index.html                   (HTML entry point)
```

### **Key Features:**
- **Protected Routes** - ProtectedRoute component ensures authentication
- **Role-Based Access** - RoleGuard checks user roles (Customer, Seller, Admin)
- **API Integration** - Centralized Axios instance with JWT token injection
- **Context API** - Global auth and cart state management
- **Responsive UI** - Tailwind CSS for mobile-first design

---

## 🚀 Getting Started - Local Development Setup

### **Prerequisites:**
- Java 21+ (JDK)
- Node.js 18+
- npm 10+
- MySQL 8+
- Maven 3.8+ (Maven Wrapper included in each service)

### **Step 1: Clone/Setup Project**
```bash
cd c:\Users\rohit.f.roy\OneDrive - Accenture\Desktop\dev\springboot\springboot-ecommerce-management-microservice-architecture
```

### **Step 2: Database Initialization**
```bash
# Connect to MySQL with admin credentials
mysql -u root -p

# Run the reset script
source db-helper-commands.sql

# Verify databases created
SHOW DATABASES;
```

### **Step 3: Configure Environment Variables**
Set environment variables or create `.env` files in each backend service:
```bash
# For each service (product, order, payment, user):
DB_URL=jdbc:mysql://localhost:3306/ecomv1_<service>_db
DB_USERNAME=root
DB_PASSWORD=<your_mysql_password>
JWT_SECRET=your_secret_key_here
```

### **Step 4A: Automated Startup (Windows)**
```bash
# From project root:
initializer-script.bat

# This starts all services automatically
```

### **Step 4B: Manual Startup**

**Terminal 1 - Start Product Service:**
```bash
cd backend-microservices/productservice
mvnw.cmd spring-boot:run
# Service runs on http://localhost:8081
```

**Terminal 2 - Start Order Service:**
```bash
cd backend-microservices/orderservice
mvnw.cmd spring-boot:run
# Service runs on http://localhost:8082
```

**Terminal 3 - Start Payment Service:**
```bash
cd backend-microservices/paymentservice
mvnw.cmd spring-boot:run
# Service runs on http://localhost:8083
```

**Terminal 4 - Start User Service:**
```bash
cd backend-microservices/userservice
mvnw.cmd spring-boot:run
# Service runs on http://localhost:8084
```

**Terminal 5 - Start API Gateway:**
```bash
cd backend-microservices/apigateway
mvnw.cmd spring-boot:run
# Gateway runs on http://localhost:8080
```

**Terminal 6 - Seed Test Data:**
```bash
cd backend-microservices/dataseeder
mvnw.cmd spring-boot:run
# Runs once and exits after seeding
```

**Terminal 7 - Start Frontend:**
```bash
cd frontend-client
npm install    # First time only
npm run dev
# Frontend runs on http://localhost:5173
```

### **Step 5: Verify Setup**

**Test Endpoints:**
```bash
# Check if gateway is responsive
curl http://localhost:8080/api/products

# Get all products (public endpoint)
curl http://localhost:8080/api/products

# Test login to get JWT token
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"customer@example.com","password":"password123"}'
```

**Open Frontend:**
```
http://localhost:5173
```

---

## 📁 Key Configuration Files

### **Backend Services:**
- `application.yaml` / `application.properties` - Service configuration (port, database)
- `pom.xml` - Maven dependencies per service
- `SecurityConfig.java` - Spring Security setup (gateway only)
- `JwtAuthenticationFilter.java` - JWT validation filter (gateway only)
- `JwtService.java` - JWT token operations (gateway only)

### **Frontend:**
- `vite.config.js` - Vite server port (5173)
- `axios.js` - API base URL configuration
- `.env` / `.env.local` - Environment variables for API endpoints

### **Database:**
- `db-helper-commands.sql` - Database creation & reset script

### **Startup:**
- `initializer-script.bat` - One-click startup for Windows (starts all services + frontend)

---

## 🔍 Important Files & References

| File | Location | Purpose |
|------|----------|---------|
| Project Overview | [README.md](README.md) | High-level project summary |
| Service Details | [service_information.txt](service_information.txt) | API routes & authorization |
| JWT Authorization | [dev-project-files/api-gateway-authorization-understanding](dev-project-files/api-gateway-authorization-understanding) | JWT flow explanation |
| Database Setup | [db-helper-commands.sql](db-helper-commands.sql) | MySQL initialization |
| Initialization Script | [initializer-script.bat](initializer-script.bat) | One-click startup |

---

## ⚙️ Common Development Tasks

### **Build a Service:**
```bash
cd backend-microservices/<service_name>
mvnw.cmd clean install
```

### **Run Tests:**
```bash
cd backend-microservices/<service_name>
mvnw.cmd test
```

### **View Service Logs:**
- Check console output in the terminal where service is running
- Configure logging in `application.yaml` with `logging.level.*`

### **Test API with Postman/Curl:**
1. Register a new user: `POST /api/users/register`
2. Login: `POST /api/users/login` (get JWT token)
3. Use token in `Authorization: Bearer <token>` header for protected endpoints

### **Frontend Build:**
```bash
cd frontend-client
npm run build  # Creates dist/ folder for production
```

---

## 🎯 Project Features

### **Customer Features:**
- Browse products with search and filtering
- View product details and availability
- Add items to shopping cart
- Place orders
- View order history and status
- Track payments

### **Seller Features:**
- Create and manage products
- Set pricing and inventory
- View orders for their products
- Track sales metrics and performance
- Access seller dashboard

### **Admin Features:**
- Full access to all products and orders
- User management
- View platform-wide analytics
- Manage payment transactions
- System monitoring via actuator endpoints

---

## 📝 Notes

- **JWT Secret:** Configure in gateway's `application.yaml`
- **CORS:** Gateway is configured to accept `http://localhost:5173` (frontend origin)
- **Database Isolation:** Each service has its own database for scalability
- **Stateless Architecture:** No server sessions; all auth via JWT tokens
- **Port Convention:** Gateway (8080), Services (8081-8084), Frontend (5173)

---

## 🆘 Troubleshooting

**Issue: Database connection refused**
- Solution: Ensure MySQL is running: `mysql -u root -p`

**Issue: JWT token validation fails**
- Solution: Verify JWT secret in `application.yaml` matches across services

**Issue: Frontend cannot reach backend**
- Solution: Check CORS configuration in gateway and ensure frontend URL is whitelisted

**Issue: Service won't start on port (port already in use)**
- Solution: Change port in `application.yaml` or kill the process using the port:
```bash
# Windows
netstat -ano | findstr :8081
taskkill /PID <process_id> /F
```

---

## 📞 Quick Reference

| Component | Port | Status Check |
|-----------|------|--------------|
| Frontend | 5173 | `http://localhost:5173` |
| API Gateway | 8080 | `http://localhost:8080/actuator/health` |
| Product Service | 8081 | `http://localhost:8081/actuator/health` |
| Order Service | 8082 | `http://localhost:8082/actuator/health` |
| Payment Service | 8083 | `http://localhost:8083/actuator/health` |
| User Service | 8084 | `http://localhost:8084/actuator/health` |
| MySQL | 3306 | `mysql -u root -p` |

---

**Last Updated:** 2026-06-30
**Project Version:** 0.0.1-SNAPSHOT
