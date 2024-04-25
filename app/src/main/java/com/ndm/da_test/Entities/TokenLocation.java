package com.ndm.da_test.Entities;

public class TokenLocation {
    private String deviceToken;
    private double latitude;
    private double longitude;

    public TokenLocation() {
        // Required default constructor for Firebase
    }

    public TokenLocation(String deviceToken, double latitude, double longitude) {
        this.deviceToken = deviceToken;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}

