package com.example.crabfood_api.model.entity;

import java.util.ArrayList;
import java.util.List;

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
@Table(name = "foodOptions")
public class FoodOption extends BaseEntity{
    
    @ManyToOne
    @JoinColumn(name = "foodId", nullable = false)
    private Food food;
    
    @Column(nullable = false)
    private String name;
    
    @Builder.Default
    private boolean isRequired = false;
    @Builder.Default
    private int minSelection = 1;
    @Builder.Default
    private int maxSelection = 1;
    
    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OptionChoice> choices = new ArrayList<>();
}
