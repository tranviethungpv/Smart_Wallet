package com.example.smartwallet.database.firebase;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.smartwallet.model.Category;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CategoryFirebase {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MutableLiveData<ArrayList<Category>> getAllCategories() {
        MutableLiveData<ArrayList<Category>> categoriesLiveData = new MutableLiveData<>();
        CollectionReference docRef = db.collection("categories");
        ArrayList<Category> categories = new ArrayList<>();

        docRef.get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                Category category = snapshot.toObject(Category.class);
                assert category != null;
                category.setId(snapshot.getId());
                categories.add(category);
            }
            categoriesLiveData.setValue(categories);
        }).addOnFailureListener(e -> Log.d(TAG, "get failed with ", e));

        return categoriesLiveData;
    }

}
