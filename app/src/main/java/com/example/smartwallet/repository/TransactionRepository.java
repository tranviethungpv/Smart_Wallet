package com.example.smartwallet.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.smartwallet.database.firebase.TransactionFirebase;
import com.example.smartwallet.model.Transaction;

import java.util.ArrayList;
import java.util.Map;

public class TransactionRepository {
    private final TransactionFirebase transactionFirebase;

    public TransactionRepository() {
        transactionFirebase = new TransactionFirebase();
    }

    public MutableLiveData<Boolean> addTransaction(Transaction transaction) {
        return transactionFirebase.addTransaction(transaction);
    }

    public MutableLiveData<ArrayList<Transaction>> getAllTransactions(String userId) {
        return transactionFirebase.getAllTransactions(userId);
    }

    public MutableLiveData<Boolean> deleteTransaction(Transaction transaction) {
        return transactionFirebase.deleteTransaction(transaction);
    }

    public MutableLiveData<Boolean> updateTransaction(Transaction transaction) {
        return transactionFirebase.updateTransaction(transaction);
    }

    public MutableLiveData<Map<String, Double>> calculateTotalAmountByMonth(String userId) {
        return transactionFirebase.calculateTotalAmountByMonth(userId);
    }

    public MutableLiveData<ArrayList<Transaction>> getTransactionsByHint(String input, String userId) {
        return transactionFirebase.getTransactionsByHint(input, userId);
    }
}