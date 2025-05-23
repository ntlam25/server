package com.example.crabfood_api.dto.response;

import com.example.crabfood_api.model.enums.OrderPaymentMethod;
import com.example.crabfood_api.model.enums.OrderPaymentStatus;
import com.example.crabfood_api.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class VNPayResponse {
  private Long orderId;
  private String message;
  private OrderPaymentStatus status;
  private String paymentUrl;
  private PaymentDetails paymentDetails;

  public VNPayResponse(Long orderId, String message, OrderPaymentStatus status, String paymentUrl) {
    this.orderId = orderId;
    this.message = message;
    this.status = status;
    this.paymentUrl = paymentUrl;
  }

  public VNPayResponse(Long orderId, String message, OrderPaymentStatus status) {
    this.orderId = orderId;
    this.message = message;
    this.status = status;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PaymentDetails {
    private String transactionId;
    private String paymentMethod;
    private Long amount;
    private LocalDateTime paymentDate;
  }
}