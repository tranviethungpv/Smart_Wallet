package com.example.smartwallet.model;

public class User {
    private String id;
    private String email;
    private String password;

    public User() {

    }

    public User(String id, String email) {
        this.id = id;
        this.email = email;
    }

    public User(String id, String email, String passWord) {
        this.id = id;
        this.email = email;
        this.password = passWord;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
