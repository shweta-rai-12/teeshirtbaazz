package com.tsb.service;

import com.tsb.dto.PaymentRequest;
import com.tsb.model.Order;
import com.tsb.model.Payment;
import com.tsb.model.Role;
import com.tsb.model.User;
import com.tsb.repository.OrderRepository;
import com.tsb.repository.PaymentRepository;
import com.tsb.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PaymentServiceTest {
    @Test
    void createsReferenceAndConfirmsOrderForSuccessfulPayment() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        PaymentRepository paymentRepository = mock(PaymentRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        PaymentService service = new PaymentService(orderRepository, paymentRepository, userRepository);
        User user = new User("Shopper", "shopper@example.com", "hash", Role.ROLE_USER);
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(499.0);
        order.setStatus("PENDING");

        PaymentRequest request = new PaymentRequest();
        request.setOrderId(42L);
        request.setMethod("UPI");

        when(userRepository.findByEmail("shopper@example.com")).thenReturn(Optional.of(user));
        when(orderRepository.findById(42L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(42L)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment payment = service.processPayment("shopper@example.com", request);

        assertThat(payment.getStatus()).isEqualTo("SUCCESS");
        assertThat(payment.getTransactionId()).startsWith("UPI-");
        assertThat(order.getStatus()).isEqualTo("CONFIRMED");
    }
}
