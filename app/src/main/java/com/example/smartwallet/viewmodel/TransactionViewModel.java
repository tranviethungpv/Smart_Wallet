package com.example.smartwallet.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartwallet.model.Transaction;
import com.example.smartwallet.repository.TransactionRepository;

import java.util.ArrayList;

public class TransactionViewModel extends ViewModel {
    private final TransactionRepository transactionRepository;

    public TransactionViewModel() {
        this.transactionRepository = new TransactionRepository();
    }

    public MutableLiveData<Boolean> addTransaction(Transaction transaction) {
        return transactionRepository.addTransaction(transaction);
    }

    public MutableLiveData<ArrayList<Transaction>> getAllTransactions() {
        return transactionRepository.getAllTransactions();
    }
}
