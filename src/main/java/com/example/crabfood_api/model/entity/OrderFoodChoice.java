package com.example.crabfood_api.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "orderFoodChoices")
public class OrderFoodChoice extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "orderFoodId", nullable = false)
    private OrderFood orderFood;

    @Column(nullable = false)
    private Long optionId;

    @Column(nullable = false)
    private String optionName;
    
    @Column(nullable = false)
    private String choiceName;
    
    @Builder.Default
    private double priceAdjustment = 0.0;
}