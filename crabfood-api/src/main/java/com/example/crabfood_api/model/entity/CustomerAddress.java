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
@Table(name = "customerAddresses")
public class CustomerAddress extends MasterDataBaseEntity {
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String label;
    
    @Column(nullable = false)
    private String addressLine;
    
    private double latitude;
    
    private double longitude;
    
    @Builder.Default
    private boolean isDefault = false;
}