package com.ndm.da_test.Entities;

import java.io.Serializable;


public class Data implements Serializable {

    private String type;
    private String title;
    private String body;
    private double latitude;
    private double longitude;
    private String soucre;
    private String time;
    private String list_question;


    public Data(String type, String title, String body, String time, String source) {
        this.type = type;
        this.title = title;
        this.body = body;
        this.time = time;
        this.soucre = source;
    }


    public Data(String type, String title, String body, double latitude, double longitude,String time) {
        this.type = type;
        this.title = title;
        this.body = body;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }


    public Data() {
    }

    // Getters and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getSource() {
        return soucre;
    }

    public void setSource(String soucre) {
        this.soucre = soucre;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getList_question() {
        return list_question;
    }

    public void setList_question(String list_question) {
        this.list_question = list_question;
    }

    @Override
    public String toString() {
        return "Data{" +
                "type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
