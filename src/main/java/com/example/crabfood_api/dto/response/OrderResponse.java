package com.example.crabfood_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.crabfood_api.model.enums.OrderPaymentStatus;
import com.example.crabfood_api.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private VendorResponse vendorResponse;
    private Long riderId;
    private Long customerId;
    private Long deliveryAddressId;
    private String deliveryAddressText;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
    private String deliveryNotes;
    private Double subtotal;
    private Double deliveryFee;
    private Double discountAmount;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private OrderStatus orderStatus;
    private OrderPaymentStatus paymentStatus;
    private String couponCode;
    private String cancelReason;
    private LocalDateTime orderDateTime;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime actualDeliveryTime;
    private String recipientName;
    private String recipientPhone;
    private List<OrderFoodResponse> items;
}
