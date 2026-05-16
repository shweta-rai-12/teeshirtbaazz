# TeeShirtBazz

TeeShirtBazz is a full-stack t-shirt e-commerce project with a React frontend and Spring Boot backend.

## Setup

1. Start Docker services:
   ```bash
   docker compose up --build
   ```
2. Backend API: http://localhost:8080
3. Frontend app: http://localhost:5173

## Backend
- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security with JWT
- MySQL database

## Frontend
- React
- Vite
- Axios
- React Router

## Development
- Backend: `cd backend && mvn spring-boot:run`
- Frontend: `cd frontend && npm install && npm run dev`
