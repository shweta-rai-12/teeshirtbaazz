package com.tsb.service;

import com.tsb.dto.ReturnRequestDto;
import com.tsb.model.Order;
import com.tsb.model.ReturnRequest;
import com.tsb.model.User;
import com.tsb.repository.OrderRepository;
import com.tsb.repository.ReturnRequestRepository;
import com.tsb.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ReturnService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ReturnRequestRepository returnRequestRepository;

    public ReturnService(UserRepository userRepository,
                         OrderRepository orderRepository,
                         ReturnRequestRepository returnRequestRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.returnRequestRepository = returnRequestRepository;
    }

    @Transactional
    public ReturnRequest submitRequest(String userEmail, ReturnRequestDto requestDto) {
        if (requestDto == null || requestDto.getOrderId() == null) {
            throw new IllegalArgumentException("Order is required");
        }
        if (requestDto.getReason() == null || requestDto.getReason().isBlank()) {
            throw new IllegalArgumentException("Return reason is required");
        }
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Order order = orderRepository.findById(requestDto.getOrderId()).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (!sameUser(order.getUser(), user)) {
            throw new IllegalArgumentException("Order not found");
        }
        if (!List.of("CONFIRMED", "SHIPPED", "DELIVERED").contains(order.getStatus())) {
            throw new IllegalArgumentException("Return is available only after a confirmed order");
        }
        boolean hasOpenReturn = returnRequestRepository.findByOrderAndUser(order, user).stream()
                .anyMatch(request -> List.of("REQUESTED", "APPROVED").contains(request.getStatus()));
        if (hasOpenReturn) {
            throw new IllegalArgumentException("A return request is already open for this order");
        }

        ReturnRequest request = new ReturnRequest();
        request.setUser(user);
        request.setOrder(order);
        request.setReason(requestDto.getReason());
        request.setStatus("REQUESTED");
        request.setCreatedAt(Instant.now());
        return returnRequestRepository.save(request);
    }

    public List<ReturnRequest> listRequests(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return returnRequestRepository.findByUser(user);
    }

    public List<ReturnRequest> listAllRequests() {
        return returnRequestRepository.findAll();
    }

    @Transactional
    public ReturnRequest updateStatus(Long requestId, String status) {
        ReturnRequest request = returnRequestRepository.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Return request not found"));
        String nextStatus = status == null ? "" : status.toUpperCase();
        if (!List.of("REQUESTED", "APPROVED", "REJECTED", "COMPLETED").contains(nextStatus)) {
            throw new IllegalArgumentException("Unsupported return status");
        }
        request.setStatus(nextStatus);
        request.setResolvedAt(Instant.now());
        Order order = request.getOrder();
        if ("APPROVED".equals(nextStatus)) {
            order.setStatus("RETURN_APPROVED");
        } else if ("REJECTED".equals(nextStatus)) {
            order.setStatus("RETURN_REJECTED");
        } else if ("COMPLETED".equals(nextStatus)) {
            order.setStatus("RETURNED");
        }
        orderRepository.save(order);
        return returnRequestRepository.save(request);
    }

    private boolean sameUser(User orderUser, User user) {
        return orderUser.getId() != null && orderUser.getId().equals(user.getId())
                || orderUser.getEmail().equalsIgnoreCase(user.getEmail());
    }
}
