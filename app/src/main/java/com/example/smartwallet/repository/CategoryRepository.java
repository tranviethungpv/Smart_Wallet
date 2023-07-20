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

    public MutableLiveData<ArrayList<Category>> getAllCategories() {
        return categoryFirebase.getAllCategories();
    }
}
