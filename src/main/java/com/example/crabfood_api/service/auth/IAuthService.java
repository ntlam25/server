package com.example.crabfood_api.service.auth;

import com.example.crabfood_api.dto.request.LoginRequest;
import com.example.crabfood_api.dto.request.SignupRequest;
import com.example.crabfood_api.dto.response.AuthResponse;
import com.example.crabfood_api.dto.response.UserResponse;

public interface IAuthService {
    public AuthResponse login(LoginRequest request);

    public UserResponse register(SignupRequest request);

    public String verifyEmail(String token);
}
