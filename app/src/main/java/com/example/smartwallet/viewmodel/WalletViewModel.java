package com.example.smartwallet.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartwallet.model.Wallet;
import com.example.smartwallet.repository.WalletRepository;

import java.util.ArrayList;

public class WalletViewModel extends ViewModel {
    private final WalletRepository WalletRepository;

    public WalletViewModel() {
        WalletRepository = new WalletRepository();
    }

    public MutableLiveData<ArrayList<Wallet>> getAllWallets() {
        return WalletRepository.getAllWallets();
    }
}
