package com.example.smartwallet.database.firebase;

import androidx.lifecycle.MutableLiveData;

import com.example.smartwallet.model.Transaction;
import com.google.firebase.firestore.FirebaseFirestore;

public class TransactionFirebase {
    private final FirebaseFirestore firestore;
    private final MutableLiveData<Boolean> addedCompletely = new MutableLiveData<>();

    public TransactionFirebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<Boolean> addTransaction(Transaction transaction) {

        String transactionId = firestore.collection("transactions").document().getId();

        transaction.setId(transactionId);

        firestore.collection("transactions").document(transactionId).set(transaction).addOnSuccessListener(aVoid -> {
            addedCompletely.setValue(true);
        }).addOnFailureListener(e -> {
            addedCompletely.setValue(false);
        });
        return addedCompletely;
    }
}
