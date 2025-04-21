package com.example.crabfood_api.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.crabfood_api.exception.ResourceNotFoundException;
import com.example.crabfood_api.model.entity.User;
import com.example.crabfood_api.repository.UserRepository;

@Component
public class UserUtil {
    private final UserRepository userRepository;

    public UserUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User getCurrentUser() {
        String identifier = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmailOrUsername(identifier, identifier)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with identifier: " + identifier));
    }
}
