package com.example.crabfood_api.service.order;

import com.example.crabfood_api.dto.request.LocationUpdateRequest;
import com.example.crabfood_api.dto.response.LocationUpdateResponse;
import com.example.crabfood_api.dto.response.OrderTrackingInfo;
import com.example.crabfood_api.exception.ResourceNotFoundException;
import com.example.crabfood_api.model.entity.LocationUpdate;
import com.example.crabfood_api.model.entity.Order;
import com.example.crabfood_api.model.entity.RiderProfile;
import com.example.crabfood_api.model.entity.User;
import com.example.crabfood_api.model.enums.OrderStatus;
import com.example.crabfood_api.model.enums.OrderTrackingStatus;
import com.example.crabfood_api.repository.LocationUpdateRepository;
import com.example.crabfood_api.repository.OrderRepository;
import com.example.crabfood_api.repository.RiderProfileRepository;
import com.example.crabfood_api.repository.UserRepository;
import com.example.crabfood_api.util.LocationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderTrackingService {

    private final LocationUpdateRepository locationUpdateRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RiderProfileRepository riderProfileRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public void updateRiderLocation(LocationUpdateRequest request) {
        User rider = userRepository.findById(request.getRiderId())
                .orElseThrow(() -> new ResourceNotFoundException("Rider not found"));

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        LocationUpdate locationUpdate = LocationUpdate.builder()
                .rider(rider)
                .order(order)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .bearing(request.getBearing())
                .speed(request.getSpeed())
                .timestamp(LocalDateTime.now())
                .build();

        locationUpdateRepository.save(locationUpdate);

        // Calculate estimated time of arrival
        int estimatedMinutes = calculateETA(order, request.getLatitude(), request.getLongitude());
        String estimatedTime = LocalDateTime.now().plusMinutes(estimatedMinutes).format(TIME_FORMATTER);

        LocationUpdateResponse response = LocationUpdateResponse.builder()
                .riderId(rider.getId())
                .orderId(order.getId())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .bearing(request.getBearing())
                .speed(request.getSpeed())
                .timestamp(locationUpdate.getTimestamp())
                .estimatedArrivalTime(estimatedTime)
                .estimatedMinutes(estimatedMinutes)
                .status(mapOrderStatusToTrackingStatus(order.getOrderStatus()))
                .build();

        // Send update to customer via WebSocket
        messagingTemplate.convertAndSend("/topic/location/" + order.getId(), response);

        // Update the rider's current location in their profile
        updateRiderLocation(rider.getId(), request.getLatitude(), request.getLongitude());
    }

    public OrderTrackingInfo getOrderTrackingInfo(Long orderId, Long customerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Ensure the customer is authorized to view this order
        if (!order.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("Not authorized to view this order");
        }

        if (order.getRider() == null) {
            throw new IllegalStateException("No rider assigned to this order yet");
        }

        User rider = order.getRider();
        RiderProfile riderProfile = riderProfileRepository.findById(rider.getRiderProfile().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Rider profile not found"));

        // Get latest location
        Optional<LocationUpdate> latestLocation = locationUpdateRepository.findLatestByOrderId(orderId);

        Double currentLat = null;
        Double currentLon = null;

        if (latestLocation.isPresent()) {
            currentLat = latestLocation.get().getLatitude();
            currentLon = latestLocation.get().getLongitude();
        } else {
            // Fall back to the rider's last known location from their profile
            currentLat = riderProfile.getCurrentLatitude();
            currentLon = riderProfile.getCurrentLongitude();
        }

        // Calculate estimated time
        int estimatedMinutes = calculateETA(order, currentLat, currentLon);
        String estimatedTime = LocalDateTime.now().plusMinutes(estimatedMinutes).format(TIME_FORMATTER);

        return OrderTrackingInfo.builder()
                .orderId(order.getId())
                .riderId(rider.getId())
                .riderName(rider.getFullName())
                .riderRating(riderProfile.getRating())
                .riderImageUrl(rider.getAvatarUrl())
                .status(mapOrderStatusToTrackingStatus(order.getOrderStatus()))
                .sourceLatitude(LocationHelper.getLatitude(order.getVendor().getLocation()))
                .sourceLongitude(LocationHelper.getLongitude(order.getVendor().getLocation()))
                .destinationLatitude(order.getDeliveryLatitude())
                .destinationLongitude(order.getDeliveryLongitude())
                .currentLatitude(currentLat)
                .currentLongitude(currentLon)
                .estimatedDeliveryTime(estimatedTime)
                .restaurantName(order.getVendor().getName())
                .build();
    }

    private void updateRiderLocation(Long riderId, Double latitude, Double longitude) {
        RiderProfile riderProfile = riderProfileRepository.findById(riderId)
                .orElseThrow(() -> new ResourceNotFoundException("Rider profile not found"));

        riderProfile.setCurrentLatitude(latitude);
        riderProfile.setCurrentLongitude(longitude);
        riderProfile.setLastActive(LocalDateTime.now());

        riderProfileRepository.save(riderProfile);
    }

    // Simple ETA calculation based on straight-line distance
    private int calculateETA(Order order, Double currentLat, Double currentLon) {
        if (currentLat == null || currentLon == null || order.getDeliveryLatitude() == null || order.getDeliveryLongitude() == null) {
            return 15; // Default 15 minutes if we don't have coordinates
        }

        // Calculate distance using Haversine formula
        double distance = calculateDistance(
                currentLat, currentLon,
                order.getDeliveryLatitude(), order.getDeliveryLongitude()
        );

        // Assume average speed of 20 km/h for delivery in urban areas
        double averageSpeedKmh = 20.0;

        // Convert distance (in km) to minutes
        double timeInHours = distance / averageSpeedKmh;
        int timeInMinutes = (int) Math.ceil(timeInHours * 60);

        // Add a buffer of 5 minutes
        return Math.max(1, timeInMinutes + 5);
    }

    // Haversine formula to calculate distance between two points
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Distance in km
    }

    private OrderTrackingStatus mapOrderStatusToTrackingStatus(OrderStatus status) {
        return switch (status) {
            case PENDING -> OrderTrackingStatus.PENDING;
            case ACCEPTED -> OrderTrackingStatus.ACCEPTED;
            case PREPARING -> OrderTrackingStatus.PREPARING;
            case READY -> OrderTrackingStatus.READY_FOR_PICKUP;
            case PICKED_UP -> OrderTrackingStatus.PICKED_UP;
            case ON_THE_WAY -> OrderTrackingStatus.ON_THE_WAY;
            case DELIVERED -> OrderTrackingStatus.DELIVERED;
            case CANCELLED -> OrderTrackingStatus.CANCELLED;
            default -> OrderTrackingStatus.PENDING;
        };
    }
}
