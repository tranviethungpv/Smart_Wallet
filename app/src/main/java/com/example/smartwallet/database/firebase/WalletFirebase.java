package com.example.smartwallet.database.firebase;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.smartwallet.model.Wallet;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class WalletFirebase {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final MutableLiveData<Boolean> addedCompletely = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deletedCompletely = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updatedCompletely = new MutableLiveData<>();

    public MutableLiveData<ArrayList<Wallet>> getAllWallets(String userId) {
        MutableLiveData<ArrayList<Wallet>> walletsLiveData = new MutableLiveData<>();
        CollectionReference docRef = firestore.collection("wallets");
        ArrayList<Wallet> wallets = new ArrayList<>();

        docRef.whereEqualTo("userId", userId).get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                Wallet wallet = snapshot.toObject(Wallet.class);
                assert wallet != null;
                wallet.setId(snapshot.getId());
                wallets.add(wallet);
            }
            walletsLiveData.setValue(wallets);
        }).addOnFailureListener(e -> Log.d(TAG, "get failed with ", e));

        return walletsLiveData;
    }

    public MutableLiveData<Boolean> addWallet(Wallet wallet) {

        String walletId = firestore.collection("wallets").document().getId();

        wallet.setId(walletId);

        firestore.collection("wallets").document(walletId).set(wallet).addOnSuccessListener(aVoid -> addedCompletely.setValue(true)).addOnFailureListener(e -> addedCompletely.setValue(false));
        return addedCompletely;
    }

    public MutableLiveData<Boolean> deleteWallet(Wallet wallet) {
        String walletId = wallet.getId();

        firestore.collection("wallets").document(walletId).delete().addOnSuccessListener(aVoid -> deletedCompletely.setValue(true)).addOnFailureListener(e -> deletedCompletely.setValue(false));
        return deletedCompletely;
    }

    public MutableLiveData<Boolean> updateWallet(Wallet wallet) {
        String walletId = wallet.getId();
        firestore.collection("wallets").document(walletId).update("userId", wallet.getUserId(), "name", wallet.getName(), "balance", wallet.getBalance()).addOnSuccessListener(aVoid -> updatedCompletely.setValue(true)).addOnFailureListener(e -> updatedCompletely.setValue(false));
        return updatedCompletely;
    }
}
