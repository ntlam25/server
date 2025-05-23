package com.example.crabfood_api.model.entity;

import java.time.LocalDateTime;

import com.example.crabfood_api.model.enums.PaymentMethod;
import com.example.crabfood_api.model.enums.PaymentStatus;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
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
@Entity
@Table(name = "vnpayTransactions")
public class VNPayTransaction extends MasterDataBaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "orderId", nullable = false)
    private Order order;
    
    @Column(nullable = false)
    private double amount;
    
    private String vnpayTransactionNo;
    private String bankCode;
    private LocalDateTime paymentDate;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    private String responseCode;
    private String transactionInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(length = 100)
    private String transactionId;
}
