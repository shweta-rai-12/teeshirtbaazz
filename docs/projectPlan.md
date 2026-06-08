# TeeShirtBazz Project Plan

## Vision
TeeShirtBazz is a focused full-stack t-shirt e-commerce platform. It is intentionally not a broad marketplace: the product identity is clean t-shirt shopping with smart filtering, order management, simulated payments, returns, custom t-shirt requests, and FAQ chatbot support.

The 10-day implementation target is a runnable portfolio MVP with React, Spring Boot, MySQL, JWT authentication, Docker Compose, CI, and clear deployment notes.

## Features
- Customer registration, login, JWT session handling, profile, and saved shipping addresses.
- Product catalog for Men, Women, and Kids with filtering by search, category, size, color, age group, price, and sorting.
- Cart management with add, update quantity, remove, clear, and total calculation.
- Checkout with saved address selection, order placement, and simulated COD/UPI/card payment.
- Order history, order tracking status, return requests, and return status tracking.
- Admin product CRUD, stock management, order status updates, return review, FAQ management, and custom request review.
- Custom t-shirt request workflow with size, color, logo URL, print text, notes, budget, and admin approval status.
- FAQ chatbot backed by editable FAQ content.

## Modules
- Authentication and user management.
- Profile and address management.
- Product catalog and search/filter/sort.
- Cart, checkout, orders, and simulated payments.
- Return request workflow.
- Admin operations.
- Custom t-shirt requests.
- FAQ chatbot and FAQ content management.

## Database Entities
- `User`: id, name, email, password hash, role, createdAt.
- `Address`: id, user, fullName, phone, line1, line2, city, state, postalCode, country, defaultAddress.
- `Product`: id, name, category, ageGroup, color, size, description, imageUrl, price, stock, active, createdAt.
- `Cart` and `CartItem`: user cart, selected products, quantity, line totals, cart total.
- `Order` and `OrderItem`: user, shipping address snapshot, purchased products, totalAmount, status, timestamps.
- `Payment`: order, method, status, transactionId, failureReason, amount.
- `ReturnRequest`: order, user, reason, status, createdAt, resolvedAt.
- `CustomOrder`: user, desiredSize, desiredColor, logoUrl, requestedText, notes, status, estimatedPrice, createdAt.
- `FaqItem`: category, question, answer, active.

## API Endpoints
- Auth: `POST /api/auth/register`, `POST /api/auth/login`.
- Users: `GET /api/users/me`, `PUT /api/users/me`.
- Addresses: `GET /api/addresses`, `POST /api/addresses`, `PUT /api/addresses/{id}`, `PUT /api/addresses/{id}/default`, `DELETE /api/addresses/{id}`.
- Products: `GET /api/products`, `GET /api/products/{id}`, admin `POST /api/products`, `PUT /api/products/{id}`, `DELETE /api/products/{id}`.
- Cart: `GET /api/cart`, `POST /api/cart/add`, `PUT /api/cart/update`, `DELETE /api/cart/remove/{id}`, `DELETE /api/cart/clear`.
- Orders: `POST /api/orders`, `GET /api/orders`, `GET /api/orders/{id}`, admin `GET /api/admin/orders`, `PUT /api/orders/{id}/status`.
- Payments: `POST /api/payments`, `GET /api/payments/{id}`, `GET /api/payments/order/{orderId}`.
- Returns: `POST /api/returns`, `GET /api/returns`, admin `GET /api/admin/returns`, `PUT /api/returns/{id}`.
- Custom requests: `POST /api/custom-orders`, `GET /api/custom-orders`, admin `GET /api/admin/custom-orders`, `PUT /api/custom-orders/{id}`.
- FAQ chatbot: `POST /api/chat`, admin `GET /api/admin/faqs`, `POST /api/admin/faqs`, `PUT /api/admin/faqs/{id}`, `DELETE /api/admin/faqs/{id}`.

## Phase-Wise Roadmap
- Day 1: Clean Git ignores, remove build artifacts from Git tracking, stabilize frontend/backend startup, document environment variables.
- Day 2: Complete JWT auth, role handling, profile, addresses, and protected frontend routes.
- Day 3: Complete product catalog, product details, filtering, sorting, stock visibility, and seed products.
- Day 4: Complete cart quantity updates, remove, clear, totals, and login-aware behavior.
- Day 5: Build checkout with address selection, transactional stock validation, and order creation.
- Day 6: Add simulated COD/UPI/card payments, payment reference IDs, success/failure states, and order confirmation.
- Day 7: Add order tracking and return request workflow with admin approval/rejection.
- Day 8: Add admin workspace for products, stock, orders, returns, FAQ, and custom requests.
- Day 9: Polish responsiveness, error/loading states, custom request UX, FAQ answers, and demo data.
- Day 10: Run available tests/builds, verify Docker configuration, finish README and deployment notes.

## Special Features
- T-shirt-only catalog and filters for category, size, color, age group, and price.
- Custom t-shirt request workflow with admin review.
- Return management workflow with user and admin status handling.
- Rule-based FAQ chatbot backed by editable FAQ records.
- Simulated payment handling for COD, UPI, and card, including failure simulation.

## Deployment Plan
- Local development: run backend with Spring Boot, frontend with Vite, and MySQL through Docker or a local MySQL server.
- Docker Compose: `docker compose up --build` should start MySQL, backend, and Nginx-served frontend. The frontend Nginx config proxies `/api` to the backend service.
- CI: GitHub Actions should build backend and frontend, then optionally build Docker images.
- AWS target: EC2 for containers, RDS MySQL for production database, and S3 for future uploaded image/logo storage.
- Secrets: keep DB URL, DB credentials, JWT secret, frontend origin, and upload settings in environment variables.

## Testing Plan
- Backend service tests for auth, filtering, cart totals, order stock deduction, payment simulation, and return eligibility.
- Backend integration tests for protected routes, admin-only routes, cart checkout, and order placement.
- Frontend tests for auth flow, product filtering, cart updates, checkout, orders, returns, and admin product form.
- Manual smoke test: register, login, browse/filter, add to cart, checkout, simulate payment, view order, request return, admin approves return, submit custom request, FAQ bot answers.

## Tooling Notes
- Required tools: Java 17 or compatible JDK, Maven, Node 20 LTS, npm, Docker Desktop, Git, optional MySQL CLI, optional AWS CLI.
- Current local gaps found during planning: Maven, Docker, AWS CLI, and MySQL CLI were not available on PATH.
- Demo admin account seeded on fresh database: `admin@teeshirtbazz.com` / `Admin@123`.
