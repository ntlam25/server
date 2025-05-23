package com.example.crabfood_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
    private Long id;
    private String label;
    private String fullAddress;
    private Double latitude;
    private Double longitude;
    private Boolean isDefault;
}