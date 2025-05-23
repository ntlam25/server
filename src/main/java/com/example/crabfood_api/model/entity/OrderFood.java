package com.example.crabfood_api.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "orderFoods")
public class OrderFood extends BaseEntity{
    
    @ManyToOne
    @JoinColumn(name = "orderId", nullable = false)
    private Order order;
    
    @ManyToOne
    @JoinColumn(name = "foodId", nullable = false)
    private Food food;
    
    @Column(nullable = false)
    private String foodName;
    
    @Builder.Default
    @Column(nullable = false)
    private int quantity = 1;
    
    @Column(nullable = false)
    private double foodPrice;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Builder.Default
    @OneToMany(mappedBy = "orderFood", cascade = CascadeType.ALL)
    private List<OrderFoodChoice> choices = new ArrayList<>();
}