package com.example.crabfood_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.crabfood_api.dto.request.LoginRequest;
import com.example.crabfood_api.dto.request.SignupRequest;
import com.example.crabfood_api.dto.response.AuthResponse;
import com.example.crabfood_api.dto.response.UserResponse;
import com.example.crabfood_api.service.auth.IAuthService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IAuthService service;

    public AuthController(IAuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody SignupRequest request) {
        return new ResponseEntity<>(service.register(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = service.login(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(
            @RequestParam("token") String token) {
        return ResponseEntity.ok(service.verifyEmail(token));
    }

    @GetMapping("/reset-password")
    public ResponseEntity<String> verifyTokenResetPassword(
            @RequestParam("token") String token) {
        return ResponseEntity.ok(service.verifyTokenResetPassword(token));
    }
}