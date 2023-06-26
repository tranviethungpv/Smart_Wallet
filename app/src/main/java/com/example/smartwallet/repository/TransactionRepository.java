package com.example.smartwallet.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.smartwallet.database.firebase.TransactionFirebase;
import com.example.smartwallet.model.Transaction;
import com.google.firebase.firestore.FirebaseFirestore;

public class TransactionRepository {
    private final TransactionFirebase transactionFirebase;

    public TransactionRepository() {
        transactionFirebase = new TransactionFirebase();
    }

    public MutableLiveData<Boolean> addTransaction(Transaction transaction) {
        return transactionFirebase.addTransaction(transaction);
    }
}
