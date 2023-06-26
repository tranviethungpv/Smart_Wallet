package com.example.smartwallet.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.smartwallet.database.firebase.WalletFirebase;
import com.example.smartwallet.model.Wallet;

import java.util.ArrayList;

public class WalletRepository {
    private final WalletFirebase WalletFirebase;

    public WalletRepository() {
        this.WalletFirebase = new WalletFirebase();
    }

    public MutableLiveData<ArrayList<Wallet>> getAllWallets() {
        return WalletFirebase.getAllWallets();
    }
}
