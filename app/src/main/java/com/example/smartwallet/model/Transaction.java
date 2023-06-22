package com.example.smartwallet.model;

import java.time.LocalDateTime;

public class Transaction {
    private String userId;
    private String walletId;
    private Float amount;
    private String detail;
    private Boolean type;
    private LocalDateTime date;

    public Transaction() {

    }

    public Transaction(String userId, String walletId, Float amount, String detail, Boolean type, LocalDateTime date) {
        this.userId = userId;
        this.walletId = walletId;
        this.amount = amount;
        this.detail = detail;
        this.type = type;
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public String getWalletId() {
        return walletId;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
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

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
