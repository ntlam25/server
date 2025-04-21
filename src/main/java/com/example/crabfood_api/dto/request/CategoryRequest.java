package com.example.crabfood_api.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {

    private Long vendorId;

    private String name;

    private Integer displayOrder;

    private Boolean isActive;
}
