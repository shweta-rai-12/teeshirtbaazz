package com.tsb.controller;

import com.tsb.dto.CustomOrderRequest;
import com.tsb.model.CustomOrder;
import com.tsb.service.CustomOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/custom-orders")
public class CustomOrderController {

}
