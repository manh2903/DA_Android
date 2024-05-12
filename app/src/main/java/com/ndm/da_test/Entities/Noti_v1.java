package com.ndm.da_test.Entities;

import java.util.List;

public class Noti_v1 {
    private String title;
    private String body;
    private List<String> token;
    private double longitude;
    private double latitude;

    public Noti_v1(String title, String body, List<String> token, double longitude, double latitude) {
        this.title = title;
        this.body = body;
        this.token = token;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Noti_v1() {
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getToken() {
        return token;
    }

    public void setToken(List<String> registrationTokens) {
        this.token = registrationTokens;
    }

    @Override
    public String toString() {
        return "Noti{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", token=" + token +
                '}';
    }
}
