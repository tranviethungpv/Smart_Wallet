package com.example.smartwallet.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartwallet.model.Transaction;
import com.example.smartwallet.repository.TransactionRepository;

import java.util.ArrayList;
import java.util.Map;

public class TransactionViewModel extends ViewModel {
    private final TransactionRepository transactionRepository;

    public TransactionViewModel() {
        this.transactionRepository = new TransactionRepository();
    }

    public MutableLiveData<ArrayList<Transaction>> getAllTransactions(String userId) {
        return transactionRepository.getAllTransactions(userId);
    }

    public MutableLiveData<Boolean> addTransaction(Transaction transaction) {
        return transactionRepository.addTransaction(transaction);
    }

    public MutableLiveData<Boolean> deleteTransaction(Transaction transaction) {
        return transactionRepository.deleteTransaction(transaction);
    }

    public MutableLiveData<Boolean> updateTransaction(Transaction transaction) {
        return transactionRepository.updateTransaction(transaction);
    }

    public MutableLiveData<Map<String, Double>> calculateTotalAmountByMonth(String userId) {
        return transactionRepository.calculateTotalAmountByMonth(userId);
    }

    public MutableLiveData<ArrayList<Transaction>> getTransactionsByHint(String input, String userId) {
        return transactionRepository.getTransactionsByHint(input, userId);
    }
}
