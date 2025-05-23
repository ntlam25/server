package com.example.crabfood_api.model.entity;

import java.time.LocalDateTime;

import com.example.crabfood_api.model.enums.OrderStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orderStatusHistory")
public class OrderStatusHistory extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "orderId", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changedBy")
    private User changedBy;

    private String notes;
    
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}