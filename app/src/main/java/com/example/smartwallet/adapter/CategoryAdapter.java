package com.example.smartwallet.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartwallet.R;
import com.example.smartwallet.databinding.ItemCategoryBinding;
import com.example.smartwallet.listener.ICategoryClickListener;
import com.example.smartwallet.model.Category;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private final ArrayList<Category> categoryArrayList;
    private final ICategoryClickListener categoryClickListener;

    public CategoryAdapter(ArrayList<Category> categoryArrayList, ICategoryClickListener categoryClickListener) {
        this.categoryArrayList = categoryArrayList;
        this.categoryClickListener = categoryClickListener;
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryAdapter.CategoryViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryViewHolder holder, int position) {
        Category currentItem = categoryArrayList.get(position);
        holder.textViewWalletName.setText(currentItem.getName());
        holder.imageViewIcon.setImageResource(R.drawable.package_icon);

        holder.itemView.setOnClickListener(v -> categoryClickListener.onCategoryClick(currentItem, false));
        holder.itemView.setOnLongClickListener(v -> {
            categoryClickListener.onCategoryClick(currentItem, true);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewWalletName;
        private final ImageView imageViewIcon;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ItemCategoryBinding itemCategoryBinding = ItemCategoryBinding.bind(itemView);
            textViewWalletName = itemCategoryBinding.textViewCategoryName;
            imageViewIcon = itemCategoryBinding.imageViewIcon;
        }
    }
}
