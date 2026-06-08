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
        String method = request.getMethod() == null ? "" : request.getMethod().toUpperCase();
        if (!method.equals("UPI") && !method.equals("CARD") && !method.equals("COD")) {
            throw new IllegalArgumentException("Payment method must be UPI, CARD, or COD");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(method);
        payment.setAmount(order.getTotalAmount());
        payment.setTransactionId(method + "-" + UUID.randomUUID());

        if (Boolean.TRUE.equals(request.getSimulateFailure())) {
            payment.setStatus("FAILED");
            payment.setFailureReason("Simulated payment failure for demo handling");
            order.setStatus("PAYMENT_FAILED");
        } else {
            payment.setStatus("SUCCESS");
            order.setStatus("CONFIRMED");
        }

        orderRepository.save(order);
        return paymentRepository.save(payment);
    }

    public Payment getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }

    public Payment getPaymentByOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId).orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }
}
