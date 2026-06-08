package com.tsb.service;

import com.tsb.dto.UserProfileRequest;
import com.tsb.model.User;
import com.tsb.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getProfile(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User updateProfile(String email, UserProfileRequest request) {
        User user = getProfile(email);
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        user.setName(request.getName());
        return userRepository.save(user);
    }
}
