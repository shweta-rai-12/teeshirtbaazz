package com.tsb.service;

import com.tsb.dto.PaymentRequest;
import com.tsb.model.Order;
import com.tsb.model.Payment;
import com.tsb.repository.OrderRepository;
import com.tsb.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(OrderRepository orderRepository, PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    public Payment processPayment(PaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(request.getMethod());
        payment.setAmount(order.getTotalAmount());
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setStatus("SUCCESS");

        order.setStatus("CONFIRMED");
        orderRepository.save(order);
        return paymentRepository.save(payment);
    }
}
