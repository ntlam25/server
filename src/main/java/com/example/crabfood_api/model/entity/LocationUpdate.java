package com.example.crabfood_api.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "location_updates")
public class LocationUpdate extends MasterDataBaseEntity {

    @ManyToOne
    @JoinColumn(name = "riderId", nullable = false)
    private User rider;

    @ManyToOne
    @JoinColumn(name = "orderId", nullable = false)
    private Order order;

    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
    private Double bearing;
    private Double speed;
}
