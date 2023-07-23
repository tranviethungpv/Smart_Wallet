package com.example.smartwallet.database.firebase;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.smartwallet.model.Transaction;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TransactionFirebase {
    private final FirebaseFirestore firestore;
    private final MutableLiveData<Boolean> addedCompletely = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deletedCompletely = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updatedCompletely = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Double>> totalAmountByMonth = new MutableLiveData<>();

    public TransactionFirebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<Boolean> addTransaction(Transaction transaction) {

        String transactionId = firestore.collection("transactions").document().getId();

        transaction.setId(transactionId);

        firestore.collection("transactions").document(transactionId).set(transaction).addOnSuccessListener(aVoid -> addedCompletely.setValue(true)).addOnFailureListener(e -> addedCompletely.setValue(false));
        return addedCompletely;
    }

    public MutableLiveData<ArrayList<Transaction>> getAllTransactions(String userId) {
        MutableLiveData<ArrayList<Transaction>> transactionsLiveData = new MutableLiveData<>();
        CollectionReference docRef = firestore.collection("transactions");
        ArrayList<Transaction> transactions = new ArrayList<>();

        docRef.whereEqualTo("userId", userId).get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                Transaction transaction = snapshot.toObject(Transaction.class);
                transactions.add(transaction);
            }
            transactionsLiveData.setValue(transactions);
        }).addOnFailureListener(e -> Log.d(TAG, "get failed with ", e));

        return transactionsLiveData;
    }

    public MutableLiveData<Boolean> deleteTransaction(Transaction transaction) {
        String transactionId = transaction.getId();

        firestore.collection("transactions").document(transactionId).delete().addOnSuccessListener(aVoid -> deletedCompletely.setValue(true)).addOnFailureListener(e -> deletedCompletely.setValue(false));
        return deletedCompletely;
    }

    public MutableLiveData<Boolean> updateTransaction(Transaction transaction) {
        String transactionId = transaction.getId();
        firestore.collection("transactions").document(transactionId).update("userId", transaction.getUserId(), "detail", transaction.getDetail(), "categoryId", transaction.getCategoryId(), "walletId", transaction.getWalletId(), "amount", transaction.getAmount(), "type", transaction.getType(), "date", transaction.getDate()).addOnSuccessListener(aVoid -> updatedCompletely.setValue(true)).addOnFailureListener(e -> updatedCompletely.setValue(false));
        return updatedCompletely;
    }

    public MutableLiveData<Map<String, Double>> calculateTotalAmountByMonth(String userId) {
        CollectionReference docRef = firestore.collection("transactions");

        docRef.whereEqualTo("userId", userId).get().addOnSuccessListener(task -> {
            Map<String, Double> monthlyTotals = new HashMap<>();

            for (DocumentSnapshot snapshot : task.getDocuments()) {
                // Get the timestamp field
                Object timestampObj = snapshot.get("date");

                if (timestampObj != null) {
                    // Convert the timestamp to a Date object
                    Date transactionDate = ((Timestamp) timestampObj).toDate();

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(transactionDate);
                    int month = calendar.get(Calendar.MONTH) + 1; // Adding 1 to get the month in 1-based index
                    int year = calendar.get(Calendar.YEAR);

                    // Get the amount field
                    Object amountObj = snapshot.get("amount");
                    // Get the type field
                    Object typeObj = snapshot.get("type");
                    Boolean type = (Boolean) typeObj;
                    if (Boolean.TRUE.equals(type)) {
                        if (amountObj != null) {
                            double amount = (double) amountObj;

                            // Update the total amount for the corresponding month
                            @SuppressLint("DefaultLocale") String monthKey = String.format("%02d-%04d", month, year);
                            Double currentTotal = monthlyTotals.get(monthKey);
                            if (currentTotal != null) {
                                currentTotal += amount;
                            } else {
                                currentTotal = amount;
                            }
                            monthlyTotals.put(monthKey, currentTotal);
                        }
                    }
                }
            }
            totalAmountByMonth.setValue(monthlyTotals);
        }).addOnFailureListener(e -> Log.d(TAG, "get failed with ", e));
        return totalAmountByMonth;
    }
}
