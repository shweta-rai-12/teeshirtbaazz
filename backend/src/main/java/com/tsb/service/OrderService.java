package com.tsb.service;

import com.tsb.dto.OrderRequest;
import com.tsb.model.*;
import com.tsb.repository.CartItemRepository;
import com.tsb.repository.CartRepository;
import com.tsb.repository.OrderRepository;
import com.tsb.repository.ProductRepository;
import com.tsb.repository.UserRepository;
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

    public OrderService(UserRepository userRepository,
                        CartRepository cartRepository,
                        OrderRepository orderRepository,
                        CartItemRepository cartItemRepository,
                        ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
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
        order.setShippingAddress(request.getShippingAddress());
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(Instant.now());

        double total = 0;
        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
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

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    public Order updateStatus(Long orderId, String status) {
        Order order = getOrder(orderId);
        order.setStatus(status);
        order.setUpdatedAt(Instant.now());
        return orderRepository.save(order);
    }
}
