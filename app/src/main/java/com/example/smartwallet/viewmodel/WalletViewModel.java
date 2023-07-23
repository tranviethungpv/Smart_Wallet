package com.example.smartwallet.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartwallet.model.Wallet;
import com.example.smartwallet.repository.WalletRepository;

import java.util.ArrayList;

public class WalletViewModel extends ViewModel {
    private final WalletRepository walletRepository;

    public WalletViewModel() {
        walletRepository = new WalletRepository();
    }

    public MutableLiveData<ArrayList<Wallet>> getAllWallets(String userId) {
        return walletRepository.getAllWallets(userId);
    }

    public MutableLiveData<Boolean> addWallet(Wallet wallet) {
        return walletRepository.addWallet(wallet);
    }

    public MutableLiveData<Boolean> deleteWallet(Wallet wallet) {
        return walletRepository.deleteWallet(wallet);
    }

    public MutableLiveData<Boolean> updateWallet(Wallet wallet) {
        return walletRepository.updateWallet(wallet);
    }
}
