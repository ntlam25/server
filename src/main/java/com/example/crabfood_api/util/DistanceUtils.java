package com.example.crabfood_api.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

public class DistanceUtils {
    private DistanceUtils() {
    }
    private static final double EARTH_RADIUS_KM = 6371.0;

    // Tính khoảng cách bằng công thức Haversine (đơn vị: km)
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    // Tính khoảng cách từ Point đến Vendor (nếu Vendor có trường location là kiểu Geometry)
    public static double calculateDistance(Point userPoint, Point destinationPoint) {
        Coordinate destinationCoord = destinationPoint.getCoordinate();
        return calculateDistance(
            userPoint.getY(), userPoint.getX(), // Lat, Lon
            destinationCoord.getY(), destinationCoord.getX()
        );
    }
}