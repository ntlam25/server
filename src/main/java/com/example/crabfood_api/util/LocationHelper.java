package com.example.crabfood_api.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class LocationHelper {
    private LocationHelper() {
    }
    public static Double getLatitude(Point location) {
        return location != null ? location.getY() : null;
    }

    public static Double getLongitude(Point location) {
        return location != null ? location.getX() : null;
    }

    public static Point createPoint(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return null;
        }
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}