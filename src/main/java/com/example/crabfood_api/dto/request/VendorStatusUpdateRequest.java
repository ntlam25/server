package com.example.crabfood_api.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendorStatusUpdateRequest {
    private Boolean isActive;
}