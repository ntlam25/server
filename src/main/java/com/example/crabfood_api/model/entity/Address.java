package com.example.crabfood_api.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "addresses", indexes = {
        @Index(name = "idx_user_address", columnList = "user_id"),
        @Index(name = "idx_default_address", columnList = "user_id, isDefault")
})
public class Address extends MasterDataBaseEntity {
    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String fullAddress;

    private Double latitude;
    private Double longitude;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDefault = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}