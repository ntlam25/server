package com.example.crabfood_api.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private String orderNumber;
    private Long vendorId;
    private Long customerId;
    private Long deliveryAddressId;
    private String deliveryAddressText;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
    private String deliveryNotes;
    private Double subtotal;
    private Double deliveryFee;
    private Double discountAmount;
    private Double totalAmount;
    private String paymentMethod;
    private String orderStatus;
    private String couponCode;
    private String recipientName;
    private String recipientPhone;
    private List<CartItemDTO> items;
}
