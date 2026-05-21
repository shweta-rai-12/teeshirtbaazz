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

}
