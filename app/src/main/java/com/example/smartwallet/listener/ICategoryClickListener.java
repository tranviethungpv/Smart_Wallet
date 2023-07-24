package com.example.smartwallet.listener;

import com.example.smartwallet.model.Category;

public interface ICategoryClickListener {
    void onCategoryClick(Category category, Boolean isLongClick);
}
