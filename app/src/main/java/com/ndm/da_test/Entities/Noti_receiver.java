package com.ndm.da_test.Entities;

import java.io.Serializable;

public class Noti_receiver implements Serializable {

    private Data data;
    private String title;
    private String body;
    private String time;
    private String type;



    public Noti_receiver(String title, String body, String time, String type) {
        this.title = title;
        this.body = body;
        this.time = time;
        this.type = type;
    }

    public Noti_receiver() {
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
