package com.example.crabfood_api.model.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Table(name = "categories")
public class Category extends MasterDataBaseEntity {

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Column(nullable = false)
    private String name;

    @Column(name = "is_global", nullable = false)
    @Builder.Default
    private boolean isGlobal = false;


    private String description;
    private String imageUrl;
    @Column(unique = true)
    private String slug;

    @Builder.Default
    private int displayOrder = 0;
    @Builder.Default
    private boolean isActive = true;

    @ManyToMany(mappedBy = "categories")
    @Builder.Default
    private Set<Food> foods = new HashSet<>();
}
