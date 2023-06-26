package com.example.smartwallet.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartwallet.model.Category;
import com.example.smartwallet.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

public class CategoryViewModel extends ViewModel {
    private final CategoryRepository categoryRepository;

    public CategoryViewModel() {
        categoryRepository = new CategoryRepository();
    }

    public MutableLiveData<ArrayList<Category>> getAllCategories() {
        return categoryRepository.getAllCategories();
    }
}
