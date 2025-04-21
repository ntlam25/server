package com.example.crabfood_api.dto.request;

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
public class AddressRequest {
    private String addressLine;
    private String city;
    private String district;
    private String ward;
    private Double latitude;
    private Double longitude;
    private Boolean isDefault;
    private String recipientName;
    private String recipientPhone;
}