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
@Table(name = "optionChoices")
public class OptionChoice extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "optionId", nullable = false)
    private FoodOption option;
    
    @Column(nullable = false)
    private String name;
    
    @Builder.Default
    private double priceAdjustment = 0.0;
    @Builder.Default
    private boolean isDefault = false;
}