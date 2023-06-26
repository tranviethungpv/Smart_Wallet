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
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MutableLiveData<ArrayList<Wallet>> getAllWallets() {
        MutableLiveData<ArrayList<Wallet>> walletsLiveData = new MutableLiveData<>();
        CollectionReference docRef = db.collection("wallets");
        ArrayList<Wallet> wallets = new ArrayList<>();

        docRef.get().addOnSuccessListener(querySnapshot -> {
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
}
