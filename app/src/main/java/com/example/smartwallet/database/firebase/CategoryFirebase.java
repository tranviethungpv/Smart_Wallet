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
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final MutableLiveData<Boolean> addedCompletely = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deletedCompletely = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updatedCompletely = new MutableLiveData<>();

    public MutableLiveData<ArrayList<Category>> getAllCategories(String userId) {
        MutableLiveData<ArrayList<Category>> categoriesLiveData = new MutableLiveData<>();
        CollectionReference docRef = firestore.collection("categories");
        ArrayList<Category> categories = new ArrayList<>();

        docRef.whereEqualTo("userId", userId).get().addOnSuccessListener(querySnapshot -> {
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

    public MutableLiveData<Boolean> addCategory(Category category) {

        String categoryId = firestore.collection("categories").document().getId();

        category.setId(categoryId);

        firestore.collection("categories").document(categoryId).set(category).addOnSuccessListener(aVoid -> addedCompletely.setValue(true)).addOnFailureListener(e -> addedCompletely.setValue(false));
        return addedCompletely;
    }

    public MutableLiveData<Boolean> deleteCategory(Category category) {
        String categoryId = category.getId();

        firestore.collection("categories").document(categoryId).delete().addOnSuccessListener(aVoid -> deletedCompletely.setValue(true)).addOnFailureListener(e -> deletedCompletely.setValue(false));
        return deletedCompletely;
    }

    public MutableLiveData<Boolean> updateCategory(Category category) {
        String categoryId = category.getId();
        firestore.collection("categories").document(categoryId).update("userId", category.getUserId(), "name", category.getName()).addOnSuccessListener(aVoid -> updatedCompletely.setValue(true)).addOnFailureListener(e -> updatedCompletely.setValue(false));
        return updatedCompletely;
    }
}
