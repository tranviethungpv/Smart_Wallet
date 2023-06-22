package com.example.smartwallet.model;

public class Wallet {
    private Float balance;
    private String name;
    private String userId;

    public Wallet() {

    }

    public Wallet(Float balance, String name, String userId) {
        this.balance = balance;
        this.name = name;
        this.userId = userId;
    }

    public Float getBalance() {
        return balance;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
