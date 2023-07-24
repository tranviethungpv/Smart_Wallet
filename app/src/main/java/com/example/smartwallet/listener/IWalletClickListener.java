package com.example.smartwallet.listener;

import com.example.smartwallet.model.Wallet;

public interface IWalletClickListener {
    void onTransactionClick(Wallet wallet, Boolean isLongClick);
}
