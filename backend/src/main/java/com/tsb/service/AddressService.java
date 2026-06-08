package com.tsb.service;

import com.tsb.dto.AddressRequest;
import com.tsb.model.Address;
import com.tsb.model.User;
import com.tsb.repository.AddressRepository;
import com.tsb.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public List<Address> list(String userEmail) {
        return addressRepository.findByUser(user(userEmail));
    }

    @Transactional
    public Address create(String userEmail, AddressRequest request) {
        User owner = user(userEmail);
        boolean firstAddress = addressRepository.findByUser(owner).isEmpty();
        Address address = new Address();
        address.setUser(owner);
        apply(address, request);
        if (firstAddress) {
            address.setDefaultAddress(true);
        }
        Address saved = addressRepository.save(address);
        if (Boolean.TRUE.equals(saved.getDefaultAddress()) && saved.getId() != null) {
            setDefault(userEmail, saved.getId());
        }
        return saved;
    }

    @Transactional
    public Address update(String userEmail, Long id, AddressRequest request) {
        Address address = ownedAddress(userEmail, id);
        apply(address, request);
        Address saved = addressRepository.save(address);
        if (Boolean.TRUE.equals(saved.getDefaultAddress()) && saved.getId() != null) {
            setDefault(userEmail, saved.getId());
        }
        return saved;
    }

    @Transactional
    public Address setDefault(String userEmail, Long id) {
        User user = user(userEmail);
        Address selected = ownedAddress(userEmail, id);
        for (Address address : addressRepository.findByUser(user)) {
            address.setDefaultAddress(address.getId().equals(id));
            addressRepository.save(address);
        }
        selected.setDefaultAddress(true);
        return selected;
    }

    public void delete(String userEmail, Long id) {
        Address address = ownedAddress(userEmail, id);
        boolean wasDefault = Boolean.TRUE.equals(address.getDefaultAddress());
        addressRepository.delete(address);
        List<Address> remaining = addressRepository.findByUser(user(userEmail));
        if (wasDefault && !remaining.isEmpty()) {
            setDefault(userEmail, remaining.get(0).getId());
        }
    }

    public Address ownedAddress(String userEmail, Long id) {
        return addressRepository.findByIdAndUser(id, user(userEmail))
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));
    }

    private void apply(Address address, AddressRequest request) {
        require(request.getFullName(), "Full name is required");
        require(request.getPhone(), "Phone is required");
        require(request.getLine1(), "Address line is required");
        require(request.getCity(), "City is required");
        require(request.getState(), "State is required");
        require(request.getPostalCode(), "Postal code is required");
        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setLine1(request.getLine1());
        address.setLine2(request.getLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry() == null || request.getCountry().isBlank() ? "India" : request.getCountry());
        address.setDefaultAddress(Boolean.TRUE.equals(request.getDefaultAddress()));
    }

    private void require(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    private User user(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
