package com.tsb.service;

import com.tsb.dto.AddressRequest;
import com.tsb.model.Address;
import com.tsb.model.Role;
import com.tsb.model.User;
import com.tsb.repository.AddressRepository;
import com.tsb.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AddressServiceTest {
    @Test
    void makesFirstAddressDefaultAutomatically() {
        AddressRepository addressRepository = mock(AddressRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        AddressService service = new AddressService(addressRepository, userRepository);
        User user = new User("Shopper", "shopper@example.com", "hash", Role.ROLE_USER);

        when(userRepository.findByEmail("shopper@example.com")).thenReturn(Optional.of(user));
        when(addressRepository.findByUser(user)).thenReturn(List.of());
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Address saved = service.create("shopper@example.com", request());

        assertThat(saved.getDefaultAddress()).isTrue();
    }

    private AddressRequest request() {
        AddressRequest request = new AddressRequest();
        request.setFullName("Shweta Rai");
        request.setPhone("9999999999");
        request.setLine1("MG Road");
        request.setCity("Bengaluru");
        request.setState("Karnataka");
        request.setPostalCode("560001");
        request.setCountry("India");
        request.setDefaultAddress(false);
        return request;
    }
}
