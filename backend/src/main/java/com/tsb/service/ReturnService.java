package com.tsb.service;

import com.tsb.dto.ReturnRequestDto;
import com.tsb.model.Order;
import com.tsb.model.ReturnRequest;
import com.tsb.model.User;
import com.tsb.repository.OrderRepository;
import com.tsb.repository.ReturnRequestRepository;
import com.tsb.repository.UserRepository;
import org.springframework.stereotype.Service;

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

    public ReturnRequest submitRequest(String userEmail, ReturnRequestDto requestDto) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Order order = orderRepository.findById(requestDto.getOrderId()).orElseThrow(() -> new IllegalArgumentException("Order not found"));

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

    public ReturnRequest updateStatus(Long requestId, String status) {
        ReturnRequest request = returnRequestRepository.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Return request not found"));
        request.setStatus(status);
        request.setResolvedAt(Instant.now());
        return returnRequestRepository.save(request);
    }
}
