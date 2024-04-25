package com.ndm.da_test.Entities;

public class DistanceCalculator {
    private final int EARTH_RADIUS = 6371;

    public float calculateDistance(double startLat, double startLng, double endLat, double endLng) {
        // Chuyển đổi độ sang radian
        double dLat = Math.toRadians(endLat - startLat);
        double dLng = Math.toRadians(endLng - startLng);

        // Tính toán theo công thức haversine
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endLat)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Khoảng cách dựa trên đường kính Trái Đất
        float distance = (float) (EARTH_RADIUS * c);
        return distance;
    }
}