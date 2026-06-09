# TeeShirtBazz Demo Checklist

## Customer Flow
- Register a new user.
- Login and browse products.
- Apply filters: category, size, color, age group, price, in-stock.
- Open product details and add an item to cart.
- Update quantity, remove an item, clear cart, and verify totals.
- Add an item again and continue to checkout.
- Add or select shipping address.
- Place order with COD, UPI, or card simulation.
- Confirm order reference on the order confirmation page.
- Open Orders and verify tracking timeline.
- Submit a return request.
- Open Returns and verify status.
- Submit a custom t-shirt request with uploaded logo or logo URL.
- Ask FAQ chatbot about shipping, returns, size, custom orders, and payment.

## Admin Flow
- Login with `admin@teeshirtbazz.com` / `Admin@123`.
- Open Admin Workspace.
- Add a product and verify it appears in catalog.
- Edit product stock and active status.
- Update order status through confirmed, shipped, and delivered.
- Approve or reject a return request.
- Review and approve/reject custom t-shirt requests.
- Add or edit FAQ content and verify chatbot answer.

## Final Smoke
- Run `docker compose up --build`.
- Verify frontend can call `/api` through Nginx.
- Verify uploaded custom logo is visible from `/uploads/...`.
- Verify no generated build artifacts are staged in Git.
