package com.example.smartwallet.database.firebase;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.smartwallet.model.Transaction;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TransactionFirebase {
    private final FirebaseFirestore firestore;
    private final MutableLiveData<Boolean> addedCompletely = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deletedCompletely = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updatedCompletely = new MutableLiveData<>();
    private final MutableLiveData<Long> totalAmount = new MutableLiveData<>();

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

//    public MutableLiveData<Boolean> calculateTotalForMonth(int month) {
//        // Get the Firestore instance
//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//
//        // Specify the collection reference
//        CollectionReference transactionsRef = firestore.collection("transactions");
//
//        // Create a Calendar object to set the start and end dates
//        Calendar startCalendar = Calendar.getInstance();
//        startCalendar.set(Calendar.MONTH, month);
//        startCalendar.set(Calendar.DAY_OF_MONTH, 1);
//        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
//        startCalendar.set(Calendar.MINUTE, 0);
//        startCalendar.set(Calendar.SECOND, 0);
//        startCalendar.set(Calendar.MILLISECOND, 0);
//
//        Calendar endCalendar = Calendar.getInstance();
//        endCalendar.set(Calendar.MONTH, month + 1);
//        endCalendar.set(Calendar.DAY_OF_MONTH, 1);
//        endCalendar.set(Calendar.HOUR_OF_DAY, 0);
//        endCalendar.set(Calendar.MINUTE, 0);
//        endCalendar.set(Calendar.SECOND, 0);
//        endCalendar.set(Calendar.MILLISECOND, 0);
//
//        // Create a query to retrieve the transactions within the desired date range
//        Query query = transactionsRef.whereGreaterThanOrEqualTo("date", startCalendar.getTime())
//                .whereLessThan("date", endCalendar.getTime());
//
//        // Execute the query
//        query.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                QuerySnapshot querySnapshot = task.getResult();
//                if (querySnapshot != null) {
//                    long totalIncome = 0;
//                    long totalOutcome = 0;
//
//                    // Iterate through the query results
//                    List<DocumentSnapshot> documents = querySnapshot.getDocuments();
//                    for (DocumentSnapshot document : documents) {
//                        // Get the transaction data
//                        Transaction transaction = document.toObject(Transaction.class);
//                        if (transaction != null) {
//                            // Add the amount to the appropriate total based on the type
//                            if ("type".equals(transaction.getType())) {
//                                totalIncome += transaction.getAmount();
//                            } else if ("outcome".equals(transaction.getType())) {
//                                totalOutcome += transaction.getAmount();
//                            }
//                        }
//                    }
//
//                    totalAmount.setValue(totalIncome - totalOutcome);
//                }
//            } else {
//                // Handle any errors
//                Exception exception = task.getException();
//                if (exception != null) {
//                    // Handle the exception
//                }
//            }
//        });
//    }
//    }
}
