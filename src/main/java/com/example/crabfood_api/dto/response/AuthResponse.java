package com.example.crabfood_api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private Long userId;
    private String username;
    private String email;
    private String[] roles;
}