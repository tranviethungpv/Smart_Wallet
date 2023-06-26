package com.example.smartwallet.database.firebase;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.smartwallet.model.Transaction;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TransactionFirebase {
    private final FirebaseFirestore firestore;
    private final MutableLiveData<Boolean> addedCompletely = new MutableLiveData<>();

    public TransactionFirebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<Boolean> addTransaction(Transaction transaction) {

        String transactionId = firestore.collection("transactions").document().getId();

        transaction.setId(transactionId);

        firestore.collection("transactions").document(transactionId).set(transaction).addOnSuccessListener(aVoid -> addedCompletely.setValue(true)).addOnFailureListener(e -> addedCompletely.setValue(false));
        return addedCompletely;
    }

    public MutableLiveData<ArrayList<Transaction>> getAllTransactions() {
        MutableLiveData<ArrayList<Transaction>> transactionsLiveData = new MutableLiveData<>();
        CollectionReference docRef = firestore.collection("transactions");
        ArrayList<Transaction> transactions = new ArrayList<>();

        docRef.get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                Transaction transaction = snapshot.toObject(Transaction.class);
                transactions.add(transaction);
            }
            transactionsLiveData.setValue(transactions);
        }).addOnFailureListener(e -> Log.d(TAG, "get failed with ", e));

        return transactionsLiveData;
    }
}
