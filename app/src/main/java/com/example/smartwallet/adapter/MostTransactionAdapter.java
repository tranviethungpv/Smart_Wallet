package com.example.smartwallet.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartwallet.R;
import com.example.smartwallet.databinding.CategoryByTransactionItemBinding;
import com.example.smartwallet.view.fragment.overview.MostlyTransactionFragment;

import java.util.ArrayList;

public class MostTransactionAdapter extends RecyclerView.Adapter<MostTransactionAdapter.MostTransactionViewHolder> {
    private final ArrayList<MostlyTransactionFragment.CategoryByTransaction> listCategoryProportions;

    public MostTransactionAdapter(ArrayList<MostlyTransactionFragment.CategoryByTransaction> listCategoryProportions) {
        this.listCategoryProportions = listCategoryProportions;
    }

    @NonNull
    @Override
    public MostTransactionAdapter.MostTransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_by_transaction_item, parent, false);
        return new MostTransactionViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MostTransactionAdapter.MostTransactionViewHolder holder, int position) {
        MostlyTransactionFragment.CategoryByTransaction currentItem = listCategoryProportions.get(position);
        holder.indexView.setText(String.valueOf(position + 1));
        holder.nameView.setText(currentItem.getName());
        holder.amountView.setText(currentItem.getAmount().toString() + "đ");
        holder.proportionView.setText(currentItem.getProportion().toString() + "%");
        holder.proportionView.setTextColor(Color.RED);
    }

    @Override
    public int getItemCount() {
        return listCategoryProportions.size();
    }

    public static class MostTransactionViewHolder extends RecyclerView.ViewHolder {
        private final TextView indexView;
        private final TextView nameView;
        private final TextView amountView;
        private final TextView proportionView;

        public MostTransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            CategoryByTransactionItemBinding categoryByTransactionItemBinding = CategoryByTransactionItemBinding.bind(itemView);
            indexView = categoryByTransactionItemBinding.indexView;
            nameView = categoryByTransactionItemBinding.textViewCategory;
            amountView = categoryByTransactionItemBinding.textViewAmount;
            proportionView = categoryByTransactionItemBinding.textViewProportion;
        }
    }
}
