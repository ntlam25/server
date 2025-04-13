package com.example.crabfood_api.dto.response;

import com.example.crabfood_api.model.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponse {
    private String token;
    private Long id;
    private String username;
    private String email;
    private Role role;
}
