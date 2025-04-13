package com.example.crabfood_api.util;

import com.example.crabfood_api.dto.response.UserResponse;
import com.example.crabfood_api.model.entity.User;

public class Mapper {
    private Mapper() {
    }
    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().name())
                .emailVerified(user.isEmailVerified())
                .phoneVerified(user.isPhoneVerified())
                .isActive(user.isActive())
                .lastLogin(user.getLastLogin())
                .build();
    }
}
