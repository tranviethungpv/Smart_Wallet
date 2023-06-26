package com.example.smartwallet.model;

import com.google.firebase.Timestamp;

public class Transaction {
    private String id;
    private String userId;
    private String walletId;
    private String categoryId;
    private Float amount;
    private String detail;
    private Boolean type;
    private Timestamp date;

    public Transaction() {

    }

    public Transaction(String userId, String walletId, String categoryId, Float amount, String detail, Boolean type, Timestamp date) {
        this.userId = userId;
        this.walletId = walletId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.detail = detail;
        this.type = type;
        this.date = date;
    }

    public Transaction(String id, String userId, String walletId, String categoryId, Float amount, String detail, Boolean type, Timestamp date) {
        this.id = id;
        this.userId = userId;
        this.walletId = walletId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.detail = detail;
        this.type = type;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getWalletId() {
        return walletId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public Float getAmount() {
        return amount;
    }

    public String getDetail() {
        return detail;
    }

    public Boolean getType() {
        return type;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
