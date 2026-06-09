package com.tsb.service;

import com.tsb.dto.CustomOrderRequest;
import com.tsb.model.CustomOrder;
import com.tsb.model.User;
import com.tsb.repository.CustomOrderRepository;
import com.tsb.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class CustomOrderService {
    private final UserRepository userRepository;
    private final CustomOrderRepository customOrderRepository;

    public CustomOrderService(UserRepository userRepository, CustomOrderRepository customOrderRepository) {
        this.userRepository = userRepository;
        this.customOrderRepository = customOrderRepository;
    }

    public CustomOrder submitRequest(String userEmail, CustomOrderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Custom request details are required");
        }
        if (request.getDesiredSize() == null || request.getDesiredSize().isBlank()) {
            throw new IllegalArgumentException("Size is required");
        }
        if (request.getDesiredColor() == null || request.getDesiredColor().isBlank()) {
            throw new IllegalArgumentException("Color is required");
        }
        if ((request.getRequestedText() == null || request.getRequestedText().isBlank())
                && (request.getLogoUrl() == null || request.getLogoUrl().isBlank())
                && (request.getNotes() == null || request.getNotes().isBlank())) {
            throw new IllegalArgumentException("Add logo, text, or design notes for the custom request");
        }
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("User not found"));
        CustomOrder customOrder = new CustomOrder();
        customOrder.setUser(user);
        customOrder.setDesiredSize(request.getDesiredSize());
        customOrder.setDesiredColor(request.getDesiredColor());
        customOrder.setLogoUrl(request.getLogoUrl());
        customOrder.setRequestedText(request.getRequestedText());
        customOrder.setNotes(request.getNotes());
        customOrder.setEstimatedPrice(request.getEstimatedPrice());
        customOrder.setStatus("SUBMITTED");
        customOrder.setCreatedAt(Instant.now());
        return customOrderRepository.save(customOrder);
    }

    public List<CustomOrder> listRequests(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return customOrderRepository.findByUser(user);
    }

    public List<CustomOrder> listAllRequests() {
        return customOrderRepository.findAll();
    }

    public CustomOrder updateStatus(Long id, String status) {
        CustomOrder order = customOrderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Custom order not found"));
        String nextStatus = status == null ? "" : status.toUpperCase();
        if (!List.of("SUBMITTED", "REVIEWED", "APPROVED", "REJECTED").contains(nextStatus)) {
            throw new IllegalArgumentException("Unsupported custom request status");
        }
        order.setStatus(nextStatus);
        return customOrderRepository.save(order);
    }
}
