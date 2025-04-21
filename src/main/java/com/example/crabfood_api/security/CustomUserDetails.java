package com.example.crabfood_api.security;

import java.util.Objects;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.crabfood_api.model.entity.User;

import lombok.Getter;

@Getter
public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
    private final Long userId;
    private final String email;

    public CustomUserDetails(User user) {
        super(user.getUsername(), user.getPasswordHash(),
                user.isEmailVerified(), true,
                true, user.isActive(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole().name()))
                        .toList());

        this.userId = user.getId();
        this.email = user.getEmail();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        if (!super.equals(obj))
            return false;

        CustomUserDetails that = (CustomUserDetails) obj;

        return Objects.equals(userId, that.userId) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId, email);
    }
}
