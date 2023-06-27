package com.example.smartwallet.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.smartwallet.database.firebase.TransactionFirebase;
import com.example.smartwallet.model.Transaction;

import java.util.ArrayList;

public class TransactionRepository {
    private final TransactionFirebase transactionFirebase;

    public TransactionRepository() {
        transactionFirebase = new TransactionFirebase();
    }

    public MutableLiveData<Boolean> addTransaction(Transaction transaction) {
        return transactionFirebase.addTransaction(transaction);
    }

    public MutableLiveData<ArrayList<Transaction>> getAllTransactions() {
        return transactionFirebase.getAllTransactions();
    }

    public MutableLiveData<Boolean> deleteTransaction(Transaction transaction) {
        return transactionFirebase.deleteTransaction(transaction);
    }

    public MutableLiveData<Boolean> updateTransaction(Transaction transaction) {
        return transactionFirebase.updateTransaction(transaction);
    }
}