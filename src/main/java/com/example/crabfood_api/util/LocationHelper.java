package com.example.crabfood_api.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

public class LocationHelper {
    private LocationHelper() {
    }
    public static Double getLatitude(Point location) {
        return location != null ? location.getY() : null;
    }

    public static Double getLongitude(Point location) {
        return location != null ? location.getX() : null;
    }

    public static Point createPoint(Double longitude, Double latitude) {
        if (latitude == null || longitude == null) {
            return null;
        }
        if (longitude < -180 || longitude > 180 ||
                latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Invalid coordinates");
        }
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}