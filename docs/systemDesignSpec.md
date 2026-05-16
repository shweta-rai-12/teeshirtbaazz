# TeeShirtBazz System Design Spec

## 1. Project Summary
TeeShirtBazz is a web-based t-shirt e-commerce platform built with React frontend and Spring Boot backend. It supports user registration, product browsing, cart management, order placement, payment simulation, returns, admin product management, custom t-shirt requests, and a simple FAQ chatbot. The system uses REST APIs, MySQL data storage, JWT authentication, Docker deployment, and AWS hosting.

## 2. Technology Stack
- Frontend: React
- Backend: Spring Boot (Java)
- Database: MySQL
- ORM: Spring Data JPA (Hibernate)
- Authentication: JWT
- Hosting: AWS EC2, AWS RDS, AWS S3
- CI/CD: GitHub Actions
- Containerization: Docker

## 3. Core Features
1. User Registration & Login
2. Product catalog with filtering by category, color, size, and price
3. Cart management with add, update quantity, and remove actions
4. Transactional order placement with stock validation and rollback
5. Payment simulation for UPI, card, and COD methods
6. Order tracking and status updates
7. Return request submission and admin review process
8. Admin product management (create, update, delete)
9. Custom t-shirt request workflow for personalized orders
10. FAQ chatbot for user assistance

## 4. Architecture Overview
- React frontend consumes backend REST APIs.
- Spring Boot backend exposes API endpoints and applies business logic.
- Backend persists data in MySQL using JPA entities.
- Authentication uses JWT tokens for protected routes.
- Docker containers package frontend and backend.
- AWS hosts services: EC2 for backend, RDS for database, and S3 for design/upload assets.

## 5. Data Model
### User
- id: primary key
- name
- email
- password (hashed)
- role: USER or ADMIN

### Product
- id: primary key
- name
- category
- price
- color
- size
- stock
- description
- imageUrl

### Cart and CartItem
- Cart: id, user
- CartItem: id, cart, product, quantity
- Cart is user-specific and holds current selections before checkout.

### Order and OrderItem
- Order: id, user, totalAmount, status, createdAt, updatedAt, shippingAddress
- OrderItem: id, order, product, quantity, price
- Order status values: PENDING, CONFIRMED, SHIPPED, DELIVERED, RETURN_REQUESTED, RETURNED

### Payment
- id: primary key
- order
- method: UPI, CARD, COD
- status: INITIATED, SUCCESS, FAILED
- transactionId
- amount

### ReturnRequest
- id: primary key
- order
- user
- reason
- status: REQUESTED, APPROVED, REJECTED, COMPLETED
- createdAt, resolvedAt

### CustomOrder
- id: primary key
- user
- desiredSize
- desiredColor
- logoUrl
- textOrDesignNotes
- status: SUBMITTED, REVIEWED, APPROVED, REJECTED
- estimatedPrice

## 6. API Endpoints
### Authentication
- POST /auth/register: register new users
- POST /auth/login: return JWT token

### Products
- GET /products: list products with optional filters
- GET /products/{id}: get product detail
- POST /products: admin creates product
- PUT /products/{id}: admin updates product
- DELETE /products/{id}: admin deletes product

### Cart
- POST /cart/add: add item to cart
- GET /cart: get current cart items
- PUT /cart/update: change quantity
- DELETE /cart/remove/{itemId}: remove item

### Orders
- POST /orders: place an order from cart
- GET /orders: list user orders
- GET /orders/{id}: order details
- PUT /orders/{id}/status: admin update status

### Payments
- POST /payments: simulate payment processing for an order
- GET /payments/{id}: payment status

### Returns
- POST /returns: submit a return request
- GET /returns: list user return requests
- PUT /returns/{id}: admin approve/reject

### Custom Orders
- POST /custom-orders: create custom t-shirt request
- GET /custom-orders: list requests for user or admin
- PUT /custom-orders/{id}: admin review and update status

### Chatbot
- POST /chat: submit question and return FAQ-style answer

## 7. Business Flow
### User flow
- Register/Login
- Browse products and apply filters
- Add products to cart
- Checkout and place order
- Simulate payment
- Track the order
- Request a return if needed
- Submit custom t-shirt requests
- Ask FAQ chatbot questions

### Order transaction flow
- Validate the current cart
- Confirm each product has enough stock
- Deduct stock for purchased items
- Create order and order items
- Process payment simulation
- If any step fails, rollback transaction and restore stock

### Return workflow
- User submits return request
- Admin reviews the request
- Admin approves or rejects
- Update request status and order record
- Complete refund or return handling in the system

### Custom order workflow
- User submits custom order details
- System stores request and notifies admin
- Admin reviews the request
- Admin approves, rejects, or requests clarification
- Convert approved request into a normal order when ready

## 8. Security and Validation
- Secure endpoints with JWT authentication
- Role-based access: regular users vs admin endpoints
- Hash passwords before storage
- Validate incoming payloads for required fields and types
- Protect against invalid stock updates and duplicated orders

## 9. Deployment Plan
- Build backend and frontend as Docker images
- Use Docker Compose for local development
- Deploy backend to AWS EC2
- Use AWS RDS for MySQL database
- Use AWS S3 for storing product/custom design images
- Configure environment variables for secrets, DB connection, and JWT secret

## 10. CI/CD Pipeline
- GitHub Actions workflow triggers on push
- Steps:
  1. Checkout code
  2. Build backend and frontend
  3. Run unit tests
  4. Build Docker images
  5. Publish images or deploy to AWS

## 11. Differentiators
- Focused t-shirt marketplace with custom order flow
- Return request and approval workflow for reliability
- Lightweight chatbot for FAQs and guidance
- Clean separation of frontend, backend, and cloud services

## 12. Implementation Notes
- Keep backend controllers thin, move business logic to services
- Use DTOs for request/response models
- Use JPA repositories for data access
- Use React hooks and context for auth and cart state
- Support responsive UI and clear error handling
- Keep the API consistent and RESTful
