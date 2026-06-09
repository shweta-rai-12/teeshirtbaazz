package com.tsb.service;

import com.tsb.dto.ReturnRequestDto;
import com.tsb.model.Order;
import com.tsb.model.ReturnRequest;
import com.tsb.model.Role;
import com.tsb.model.User;
import com.tsb.repository.OrderRepository;
import com.tsb.repository.ReturnRequestRepository;
import com.tsb.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReturnServiceTest {
    @Test
    void rejectsDuplicateOpenReturnForSameOrder() {
        UserRepository userRepository = mock(UserRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        ReturnRequestRepository returnRepository = mock(ReturnRequestRepository.class);
        ReturnService service = new ReturnService(userRepository, orderRepository, returnRepository);
        User user = new User("Shopper", "shopper@example.com", "hash", Role.ROLE_USER);
        Order order = new Order();
        order.setUser(user);
        order.setStatus("CONFIRMED");
        ReturnRequest openReturn = new ReturnRequest();
        openReturn.setStatus("REQUESTED");
        ReturnRequestDto dto = new ReturnRequestDto();
        dto.setOrderId(20L);
        dto.setReason("Size issue");

        when(userRepository.findByEmail("shopper@example.com")).thenReturn(Optional.of(user));
        when(orderRepository.findById(20L)).thenReturn(Optional.of(order));
        when(returnRepository.findByOrderAndUser(order, user)).thenReturn(List.of(openReturn));

        assertThatThrownBy(() -> service.submitRequest("shopper@example.com", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already open");
    }
}
