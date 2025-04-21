package com.example.crabfood_api.service.auth;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.crabfood_api.dto.request.LoginRequest;
import com.example.crabfood_api.dto.request.SignupRequest;
import com.example.crabfood_api.dto.response.AuthResponse;
import com.example.crabfood_api.dto.response.UserResponse;
import com.example.crabfood_api.exception.AuthException;
import com.example.crabfood_api.exception.EmailException;
import com.example.crabfood_api.exception.ResourceNotFoundException;
import com.example.crabfood_api.model.entity.EmailVerificationToken;
import com.example.crabfood_api.model.entity.User;
import com.example.crabfood_api.model.entity.UserRole;
import com.example.crabfood_api.model.enums.Role;
import com.example.crabfood_api.repository.EmailVerificationTokenRepository;
import com.example.crabfood_api.repository.UserRepository;
import com.example.crabfood_api.security.CustomUserDetails;
import com.example.crabfood_api.service.email.IEmailService;

import jakarta.mail.MessagingException;

@Service
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationTokenRepository tokenRepository;
    private final IEmailService emailService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtService jwtService,
            EmailVerificationTokenRepository tokenRepository, IEmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    @Transactional
    @Override
    public UserResponse register(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException("Email already in use");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AuthException("Username already taken");
        }

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .fullName(request.getFullName())
                .emailVerified(false)
                .isActive(true)
                .build();

        Set<UserRole> userRoles;

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            userRoles = request.getRoles().stream()
                    .map(role -> new UserRole(user, role))
                    .collect(Collectors.toSet());
        } else {
            userRoles = Set.of(new UserRole(user, Role.CUSTOMER));
        }

        user.setRoles(userRoles);

        userRepository.save(user);

        String verificationToken = generateAndSaveVerificationToken(user);
        try {
            emailService.sendVerificationEmail(user.getEmail(), verificationToken);
        } catch (MessagingException e) {
            throw new EmailException("Failed to send verification email");
        }

        return toResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getIdentifier(),
                        request.getPassword()));

        User user = userRepository.findByEmailOrUsername(request.getIdentifier(), request.getIdentifier())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isEmailVerified()) {
            throw new AuthException("Email not verified");
        }

        if (!user.isActive()) {
            throw new AuthException("Account is disabled");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(userDetails);

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles()
                        .stream()
                        .map(role -> role.getRole().name())
                        .toArray(String[]::new))
                .build();
    }

    @Override
    @Transactional
    public String verifyEmail(String verifyToken) {
        EmailVerificationToken token = tokenRepository.findByToken(verifyToken)
                .orElseThrow(() -> new AuthException("Invalid token"));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new AuthException("Token expired");
        }

        User user = token.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        tokenRepository.delete(token);

        return "Email verified successfully";
    }

    private String generateAndSaveVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();

        tokenRepository.save(verificationToken);
        return token;
    }

    private UserResponse toResponse(User savedUser) {
        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());
        response.setFullName(savedUser.getFullName());
        response.setPhone(savedUser.getPhone());
        response.setRoles(savedUser.getRoles()
                .stream()
                .map(role -> role.getRole().name())
                .toArray(String[]::new));
        response.setCreatedAt(savedUser.getCreatedAt());
        response.setUpdatedAt(savedUser.getUpdatedAt());
        response.setAvatarUrl(savedUser.getAvatarUrl());
        response.setLastLogin(savedUser.getLastLogin());
        response.setActive(savedUser.isActive());
        response.setActive(savedUser.isEmailVerified());
        response.setActive(savedUser.isPhoneVerified());
        return response;
    }
}