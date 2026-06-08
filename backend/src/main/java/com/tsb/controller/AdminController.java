package com.tsb.controller;

import com.tsb.dto.FaqItemRequest;
import com.tsb.model.*;
import com.tsb.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {
    private final ProductService productService;
    private final OrderService orderService;
    private final ReturnService returnService;
    private final CustomOrderService customOrderService;
    private final FaqService faqService;

    public AdminController(ProductService productService,
                           OrderService orderService,
                           ReturnService returnService,
                           CustomOrderService customOrderService,
                           FaqService faqService) {
        this.productService = productService;
        this.orderService = orderService;
        this.returnService = returnService;
        this.customOrderService = customOrderService;
        this.faqService = faqService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> products() {
        return ResponseEntity.ok(productService.listAllProductsForAdmin());
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> orders() {
        return ResponseEntity.ok(orderService.listAllOrders());
    }

    @GetMapping("/returns")
    public ResponseEntity<List<ReturnRequest>> returns() {
        return ResponseEntity.ok(returnService.listAllRequests());
    }

    @GetMapping("/custom-orders")
    public ResponseEntity<List<CustomOrder>> customOrders() {
        return ResponseEntity.ok(customOrderService.listAllRequests());
    }

    @GetMapping("/faqs")
    public ResponseEntity<List<FaqItem>> faqs() {
        return ResponseEntity.ok(faqService.list());
    }

    @PostMapping("/faqs")
    public ResponseEntity<FaqItem> createFaq(@RequestBody FaqItemRequest request) {
        return ResponseEntity.ok(faqService.create(request));
    }

    @PutMapping("/faqs/{id}")
    public ResponseEntity<FaqItem> updateFaq(@PathVariable Long id, @RequestBody FaqItemRequest request) {
        return ResponseEntity.ok(faqService.update(id, request));
    }

    @DeleteMapping("/faqs/{id}")
    public ResponseEntity<Void> deleteFaq(@PathVariable Long id) {
        faqService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
