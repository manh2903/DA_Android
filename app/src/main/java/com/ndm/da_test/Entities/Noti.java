package com.ndm.da_test.Entities;

import java.util.List;

public class Noti {
    private String title;
    private String body;
    private List<String> token;

    public Noti(String title, String body, List<String> registrationTokens) {
        this.title = title;
        this.body = body;
        this.token = registrationTokens;
    }

    public Noti() {
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
