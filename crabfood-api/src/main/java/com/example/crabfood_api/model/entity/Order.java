package com.example.crabfood_api.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.crabfood_api.model.enums.OrderPaymentMethod;
import com.example.crabfood_api.model.enums.OrderPaymentStatus;
import com.example.crabfood_api.model.enums.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_status", columnList = "orderStatus"),
    @Index(name = "idx_order_created", columnList = "createdAt"),
    @Index(name = "idx_order_customer", columnList = "customerId")
})
public class Order extends MasterDataBaseEntity{
    
    @Column(nullable = false, unique = true)
    private String orderNumber;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customerId", nullable = false)
    private User customer;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendorId", nullable = false)
    private Vendor vendor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "riderId")
    private User rider;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deliveryAddressId")
    private CustomerAddress deliveryAddress;
    
    private String deliveryAddressText;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
    private String deliveryNotes;
    
    @Column(nullable = false)
    private double subtotal;
    
    @Column(nullable = false)
    private double deliveryFee;
    
    @Builder.Default
    private double discountAmount = 0.0;
    
    @Column(nullable = false)
    private double totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderPaymentMethod paymentMethod;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private OrderPaymentStatus paymentStatus = OrderPaymentStatus.PENDING;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.PENDING;
    
    private String cancellationReason;
    
    @ManyToOne
    @JoinColumn(name = "couponId")
    private Coupon coupon;
    
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime actualDeliveryTime;
    
    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderFood> orderFoods = new ArrayList<>();
    
    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderStatusHistory> statusHistory = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<VNPayTransaction> vnPayTransactions = new ArrayList<>();
}