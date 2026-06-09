package com.tsb.controller;

import com.tsb.dto.PaymentRequest;
import com.tsb.model.Payment;
import com.tsb.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Payment> processPayment(@AuthenticationPrincipal UserDetails user,
                                                  @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(user.getUsername(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(@AuthenticationPrincipal UserDetails user,
                                              @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPayment(user.getUsername(), id));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Payment> getPaymentByOrder(@AuthenticationPrincipal UserDetails user,
                                                     @PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrder(user.getUsername(), orderId));
    }
}
