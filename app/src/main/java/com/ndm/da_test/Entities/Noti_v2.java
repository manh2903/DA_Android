package com.ndm.da_test.Entities;

import java.util.List;


public class Noti_v2 {

    private Data data;
    private String time;
    private List<String> token;

    public Noti_v2(Data data, String time, List<String> token) {
        this.data = data;
        this.time = time;
        this.token = token;
    }

    public Noti_v2() {
    }

    // Getters and setters
    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public List<String> getTokens() {
        return token;
    }

    public void setTokens(List<String> token) {
        this.token = token;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Noti_v2{" +
                "data=" + data +
                ", time='" + time + '\'' +
                ", token=" + token +
                '}';
    }
}
