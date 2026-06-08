# TeeShirtBazz

TeeShirtBazz is a full-stack t-shirt e-commerce platform with secure authentication, product filtering, cart and order management, simulated COD/UPI/card payments, return workflow, custom t-shirt requests, FAQ chatbot support, Docker deployment, and CI/CD.

## Main Features
- Customer registration/login with JWT.
- Product catalog with search, filters, sorting, stock, and product detail pages.
- Cart, checkout, saved addresses, order confirmation, and payment simulation.
- Order tracking and return request workflow.
- Admin workspace for products, stock, orders, returns, FAQs, and custom requests.
- Rule-based FAQ chatbot backed by editable FAQ content.

## Setup

1. Copy environment defaults if needed:
   ```bash
   copy .env.example .env
   ```
2. Start Docker services:
   ```bash
   docker compose up --build
   ```
3. Backend API: http://localhost:8080
4. Frontend app: http://localhost:5173
5. Swagger UI: http://localhost:8080/swagger-ui/index.html

## Demo Login
- Admin: `admin@teeshirtbazz.com`
- Password: `Admin@123`

The admin account and demo products are seeded automatically on a fresh database.

## Backend
- Java 17 target
- Spring Boot
- Spring Data JPA
- Spring Security with JWT
- MySQL database
- Springdoc OpenAPI

## Frontend
- React
- Vite
- Axios
- React Router
- Nginx proxy for Dockerized `/api` calls

## Development
- Backend: `cd backend && mvn spring-boot:run`
- Frontend: `cd frontend && npm install && npm run dev`
- Full stack: `docker compose up --build`

## Project Plan
See `docs/projectPlan.md` for the 10-day roadmap, APIs, database entities, testing plan, deployment plan, and tooling notes.
