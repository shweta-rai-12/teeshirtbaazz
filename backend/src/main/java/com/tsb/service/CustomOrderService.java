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
        order.setStatus(status);
        return customOrderRepository.save(order);
    }
}
