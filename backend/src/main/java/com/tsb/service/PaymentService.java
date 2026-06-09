package com.tsb.service;

import com.tsb.dto.PaymentRequest;
import com.tsb.model.Order;
import com.tsb.model.Payment;
import com.tsb.model.Role;
import com.tsb.model.User;
import com.tsb.repository.OrderRepository;
import com.tsb.repository.PaymentRepository;
import com.tsb.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public PaymentService(OrderRepository orderRepository, PaymentRepository paymentRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Payment processPayment(String userEmail, PaymentRequest request) {
        if (request == null || request.getOrderId() == null) {
            throw new IllegalArgumentException("Order is required");
        }
        User user = user(userEmail);
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        assertCanAccessOrder(user, order);
        String method = request.getMethod() == null ? "" : request.getMethod().toUpperCase();
        if (!method.equals("UPI") && !method.equals("CARD") && !method.equals("COD")) {
            throw new IllegalArgumentException("Payment method must be UPI, CARD, or COD");
        }

        Payment payment = paymentRepository.findByOrderId(order.getId()).orElseGet(Payment::new);
        if ("SUCCESS".equalsIgnoreCase(payment.getStatus())) {
            return payment;
        }
        payment.setOrder(order);
        payment.setMethod(method);
        payment.setAmount(order.getTotalAmount());
        payment.setTransactionId(method + "-" + UUID.randomUUID());
        payment.setUpdatedAt(Instant.now());
        if (payment.getCreatedAt() == null) {
            payment.setCreatedAt(Instant.now());
        }

        if (Boolean.TRUE.equals(request.getSimulateFailure())) {
            payment.setStatus("FAILED");
            payment.setFailureReason("Simulated payment failure for demo handling");
            order.setStatus("PAYMENT_FAILED");
        } else {
            payment.setStatus("SUCCESS");
            payment.setFailureReason(null);
            order.setStatus("CONFIRMED");
        }

        orderRepository.save(order);
        return paymentRepository.save(payment);
    }

    public Payment getPayment(String userEmail, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        assertCanAccessOrder(user(userEmail), payment.getOrder());
        return payment;
    }

    public Payment getPaymentByOrder(String userEmail, Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        assertCanAccessOrder(user(userEmail), payment.getOrder());
        return payment;
    }

    private User user(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private void assertCanAccessOrder(User user, Order order) {
        boolean sameUser = order.getUser().getId() != null && order.getUser().getId().equals(user.getId())
                || order.getUser().getEmail().equalsIgnoreCase(user.getEmail());
        if (!sameUser && user.getRole() != Role.ROLE_ADMIN) {
            throw new IllegalArgumentException("Payment not found");
        }
    }
}
