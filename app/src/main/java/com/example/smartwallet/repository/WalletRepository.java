package com.example.smartwallet.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.smartwallet.database.firebase.WalletFirebase;
import com.example.smartwallet.model.Wallet;

import java.util.ArrayList;

public class WalletRepository {
    private final WalletFirebase walletFirebase;

    public WalletRepository() {
        this.walletFirebase = new WalletFirebase();
    }

    public MutableLiveData<ArrayList<Wallet>> getAllWallets(String userId) {
        return walletFirebase.getAllWallets(userId);
    }

    public MutableLiveData<Boolean> addWallet(Wallet wallet) {
        return walletFirebase.addWallet(wallet);
    }

    public MutableLiveData<Boolean> deleteWallet(Wallet wallet) {
        return walletFirebase.deleteWallet(wallet);
    }

    public MutableLiveData<Boolean> updateWallet(Wallet wallet) {
        return walletFirebase.updateWallet(wallet);
    }
}
