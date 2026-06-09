package com.tsb.service;

import com.tsb.dto.OrderRequest;
import com.tsb.model.*;
import com.tsb.repository.CartItemRepository;
import com.tsb.repository.CartRepository;
import com.tsb.repository.OrderRepository;
import com.tsb.repository.ProductRepository;
import com.tsb.repository.UserRepository;
import com.tsb.repository.AddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class OrderService {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;

    public OrderService(UserRepository userRepository,
                        CartRepository cartRepository,
                        OrderRepository orderRepository,
                        CartItemRepository cartItemRepository,
                        ProductRepository productRepository,
                        AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional
    public Order placeOrder(String userEmail, OrderRequest request) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }
        Order order = new Order();
        order.setUser(user);
        order.setStatus("PENDING");
        order.setShippingAddress(resolveShippingAddress(user, request));
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(Instant.now());

        double total = 0;
        for (CartItem cartItem : cart.getItems()) {
            if (cartItem.getQuantity() == null || cartItem.getQuantity() < 1) {
                throw new IllegalArgumentException("Cart contains an invalid quantity");
            }
            Product product = productRepository.findLockedById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            if (Boolean.FALSE.equals(product.getActive())) {
                throw new IllegalArgumentException("Product is no longer available: " + product.getName());
            }
            if (product.getStock() == null || product.getStock() <= 0) {
                throw new IllegalArgumentException("Product is out of stock: " + product.getName());
            }
            if (product.getStock() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            order.getItems().add(orderItem);
            total += product.getPrice() * cartItem.getQuantity();
        }

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cartRepository.save(cart);
        return saved;
    }

    public List<Order> listOrders(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return orderRepository.findByUser(user);
    }

    public List<Order> listAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    public Order getOrderForUser(String userEmail, Long orderId) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Order order = getOrder(orderId);
        boolean sameUser = order.getUser().getId() != null && order.getUser().getId().equals(user.getId())
                || order.getUser().getEmail().equalsIgnoreCase(user.getEmail());
        if (!sameUser && user.getRole() != Role.ROLE_ADMIN) {
            throw new IllegalArgumentException("Order not found");
        }
        return order;
    }

    public Order updateStatus(Long orderId, String status) {
        Order order = getOrder(orderId);
        String nextStatus = status == null ? "" : status.toUpperCase();
        if (!List.of("PENDING", "PAYMENT_FAILED", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED", "RETURN_APPROVED", "RETURN_REJECTED", "RETURNED").contains(nextStatus)) {
            throw new IllegalArgumentException("Unsupported order status");
        }
        order.setStatus(nextStatus);
        order.setUpdatedAt(Instant.now());
        return orderRepository.save(order);
    }

    private String resolveShippingAddress(User user, OrderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Shipping address is required");
        }
        if (request.getAddressId() != null) {
            return addressRepository.findByIdAndUser(request.getAddressId(), user)
                    .orElseThrow(() -> new IllegalArgumentException("Address not found"))
                    .toShippingSnapshot();
        }
        if (request.getShippingAddress() == null || request.getShippingAddress().isBlank()) {
            throw new IllegalArgumentException("Shipping address is required");
        }
        return request.getShippingAddress();
    }
}
