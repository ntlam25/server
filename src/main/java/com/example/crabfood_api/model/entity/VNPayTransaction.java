package com.example.crabfood_api.model.entity;

import java.time.LocalDateTime;

import com.example.crabfood_api.model.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    private LocalDateTime payDate;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    private String responseCode;
    private String transactionInfo; 
}
