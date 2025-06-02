package com.example.crabfood_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorRegistrationResponse {
    private Long vendorId;
    private String username;
    private String email;
    private String vendorName;
    private String status; // PENDING, APPROVED, REJECTED
    private String message;
}