package com.example.crabfood_api.repository;

import com.example.crabfood_api.model.entity.LocationUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationUpdateRepository extends JpaRepository<LocationUpdate, Long> {

    List<LocationUpdate> findByOrderIdOrderByTimestampDesc(Long orderId);

    @Query(value = "SELECT l FROM location_updates l WHERE l.orderId = :orderId ORDER BY l.timestamp DESC LIMIT 1"
    , nativeQuery = true)
    Optional<LocationUpdate> findLatestByOrderId(Long orderId);

    @Query("SELECT l FROM LocationUpdate l WHERE l.rider.id = :riderId ORDER BY l.timestamp DESC")
    Optional<LocationUpdate> findLatestByRiderId(Long riderId);
}