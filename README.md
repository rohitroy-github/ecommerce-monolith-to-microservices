# E-Commerce Microservices (ecomv1)

A Spring Boot microservices e-commerce backend with a React (Vite) frontend.

## Tech Stack

- Java + Spring Boot microservices
- Spring Cloud Gateway
- MySQL (separate DB per service)
- React + Vite frontend
- Maven Wrapper (`mvnw.cmd`) per backend service

## Services and Ports

- API Gateway: `8080`
- Product Service: `8081`
- Order Service: `8082`
- Payment Service: `8083`
- User Service: `8084`
- Frontend (Vite dev server): `5173` (default)
- DataSeeder: CLI runner (no HTTP port)

## Project Structure

- `backend-microservices/`
  - `apigateway/`
  - `productservice/`
  - `orderservice/`
  - `paymentservice/`
  - `userservice/`
  - `dataseeder/`
- `frontend-client/`
- `dev-project-files/`
  - helper scripts, SQL reset utilities, and notes
- `initializer-script.bat`

## Prerequisites

- Java 17+ (or the version required by your local setup)
- Node.js 18+
- npm
- MySQL server

## Database Setup

Use the reset script in:

- `dev-project-files/sql-reset-script.sql`

This script drops and recreates:

- `ecomv1_order_db`
- `ecomv1_payment_db`
- `ecomv1_product_db`
- `ecomv1_user_db`

Then make sure service environment variables are configured (`*.env` / shell env) for DB URLs, usernames, passwords, and JWT secret.

## Run the Full Stack

### Option 1: One-click startup (Windows)

From project root, run:

```bat
initializer-script.bat
```

This starts backend services in sequence, then frontend.

### Option 2: Manual startup

Start each backend service in its folder:

```bat
mvnw.cmd spring-boot:run
```

Suggested order:

1. `productservice`
2. `orderservice`
3. `paymentservice`
4. `userservice`
5. `apigateway`
6. `frontend-client` (`npm install` once, then `npm run dev`)

## Seed Test Data

Run DataSeeder:

```bat
cd backend-microservices\dataseeder
mvnw.cmd spring-boot:run
```

Seeder behavior includes presence checks for users/products to avoid recreating existing seed records.

## Gateway API Prefixes

All API calls go through gateway (`http://localhost:8080`):

- `/api/products/**`
- `/api/orders/**`
- `/api/payments/**`
- `/api/users/**`

## Useful Files

- `dev-project-files/service_information.txt`: Service brief, routes, and authorization levels
- `dev-project-files/sql-reset-script.sql`: DB reset + table listing queries
- `dev-project-files/db-test-data-starter-script.sql`: Test data helper SQL

## Notes

- CORS is configured in gateway for frontend origin `http://localhost:5173`.
- Register and login endpoints are public; most other routes are JWT-protected via gateway filter + role permissions.
