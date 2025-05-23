package com.example.crabfood_api.dto.response;

import com.example.crabfood_api.model.entity.UserRole;
import com.example.crabfood_api.model.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private Long userId;
    private String username;
    private String email;
    private List<Role> roles;
}