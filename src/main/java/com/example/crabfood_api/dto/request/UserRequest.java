package com.example.crabfood_api.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    private String username;
    private String email;
    private String password; // Mã hoá bên service
    private String phone;
    private String fullName;
    private String avatarUrl;

}
