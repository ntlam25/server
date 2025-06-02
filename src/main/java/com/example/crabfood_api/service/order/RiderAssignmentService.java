package com.example.crabfood_api.service.order;

import com.example.crabfood_api.model.entity.RiderProfile;
import com.example.crabfood_api.model.entity.Vendor;
import com.example.crabfood_api.model.enums.RiderStatus;
import com.example.crabfood_api.repository.RiderProfileRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiderAssignmentService {
    private final RiderProfileRepository riderProfileRepository;

    // Constants for scoring weights
    private static final double DISTANCE_WEIGHT = 0.4;
    private static final double RATING_WEIGHT = 0.3;
    private static final double LOAD_WEIGHT = 0.3;
    private static final double MAX_DISTANCE = 10.0; // Maximum distance in km
    private static final double MAX_LOAD = 5.0; // Maximum number of orders

    public RiderProfile findBestRider(Vendor vendor) {
        List<RiderProfile> availableRiders = riderProfileRepository.findByStatusAndOnlineStatusTrue(RiderStatus.AVAILABLE);
        
        if (availableRiders.isEmpty()) {
            return null;
        }

        return availableRiders.stream()
                .filter(rider -> isWithinRange(rider, vendor))
                .map(rider -> {
                    double score = calculatePriorityScore(rider, vendor);
                    rider.setPriorityScore(score);
                    return rider;
                })
                .max(Comparator.comparingDouble(RiderProfile::getPriorityScore))
                .orElse(null);
    }

    private boolean isWithinRange(RiderProfile rider, Vendor vendor) {
        if (rider.getCurrentLatitude() == null || rider.getCurrentLongitude() == null || vendor.getLocation() == null) {
            return false;
        }

        double distance = calculateDistance(
                rider.getCurrentLatitude(),
                rider.getCurrentLongitude(),
                vendor.getLocation().getY(), // latitude
                vendor.getLocation().getX()  // longitude
        );

        return distance <= MAX_DISTANCE;
    }

    private double calculatePriorityScore(RiderProfile rider, Vendor vendor) {
        double distanceScore = calculateDistanceScore(rider, vendor);
        double ratingScore = calculateRatingScore(rider);
        double loadScore = calculateLoadScore(rider);

        return (distanceScore * DISTANCE_WEIGHT) +
               (ratingScore * RATING_WEIGHT) +
               (loadScore * LOAD_WEIGHT);
    }

    private double calculateDistanceScore(RiderProfile rider, Vendor vendor) {
        double distance = calculateDistance(
                rider.getCurrentLatitude(),
                rider.getCurrentLongitude(),
                vendor.getLocation().getY(), // latitude
                vendor.getLocation().getX()  // longitude
        );

        // Normalize distance score (closer = higher score)
        return 1.0 - (distance / MAX_DISTANCE);
    }

    private double calculateRatingScore(RiderProfile rider) {
        // Normalize rating score (5.0 = 1.0, 0.0 = 0.0)
        return rider.getRating() / 5.0;
    }

    private double calculateLoadScore(RiderProfile rider) {
        // Normalize load score (fewer orders = higher score)
        return 1.0 - (rider.getTotalDeliveries() / MAX_LOAD);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
} 