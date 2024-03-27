package com.ndm.da_test.Entities;

public class User {

    private String Id;
    private String Email;
    private String FullName;
    private String Token;

    public User(String id,String email, String fullName, String token) {
        Id = id;
        Email = email;
        FullName = fullName;
        Token = token;
    }

    public User(String id, String email)
    {
        Id = id;
        Email = email;
    }
    public User() {
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getToken() {
        return Token;
    }

    public void setPassWord(String passWord) {
        Token = passWord;
    }
}
