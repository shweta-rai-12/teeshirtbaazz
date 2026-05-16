package com.tsb.controller;

import com.tsb.dto.ReturnRequestDto;
import com.tsb.model.ReturnRequest;
import com.tsb.service.ReturnService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/returns")
public class ReturnController {
    private final ReturnService returnService;

    public ReturnController(ReturnService returnService) {
        this.returnService = returnService;
    }

    @PostMapping
    public ResponseEntity<ReturnRequest> submitReturn(@AuthenticationPrincipal UserDetails user,
                                                       @RequestBody ReturnRequestDto request) {
        return ResponseEntity.ok(returnService.submitRequest(user.getUsername(), request));
    }

    @GetMapping
    public ResponseEntity<List<ReturnRequest>> listRequests(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(returnService.listRequests(user.getUsername()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ReturnRequest> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(returnService.updateStatus(id, status));
    }
}
