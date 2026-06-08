package com.tsb.controller;

import com.tsb.dto.CartItemRequest;
import com.tsb.model.Cart;
import com.tsb.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addItem(@AuthenticationPrincipal UserDetails user,
                                        @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(user.getUsername(), request.getProductId(), request.getQuantity()));
    }

    @GetMapping
    public ResponseEntity<Cart> viewCart(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(cartService.getCart(user.getUsername()));
    }

    @PutMapping("/update")
    public ResponseEntity<Cart> updateItem(@AuthenticationPrincipal UserDetails user,
                                           @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItem(user.getUsername(), request.getProductId(), request.getQuantity()));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Cart> removeItem(@AuthenticationPrincipal UserDetails user,
                                           @PathVariable Long id) {
        return ResponseEntity.ok(cartService.removeItem(user.getUsername(), id));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Cart> clear(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(cartService.clearCart(user.getUsername()));
    }
}
