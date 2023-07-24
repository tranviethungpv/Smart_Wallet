package com.example.smartwallet.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.smartwallet.database.firebase.CategoryFirebase;
import com.example.smartwallet.model.Category;

import java.util.ArrayList;

public class CategoryRepository {
    private final CategoryFirebase categoryFirebase;

    public CategoryRepository() {
        this.categoryFirebase = new CategoryFirebase();
    }

    public MutableLiveData<ArrayList<Category>> getAllCategories(String userId) {
        return categoryFirebase.getAllCategories(userId);
    }

    public MutableLiveData<Boolean> addCategory(Category category) {
        return categoryFirebase.addCategory(category);
    }

    public MutableLiveData<Boolean> deleteCategory(Category category) {
        return categoryFirebase.deleteCategory(category);
    }

    public MutableLiveData<Boolean> updateCategory(Category category) {
        return categoryFirebase.updateCategory(category);
    }
}
