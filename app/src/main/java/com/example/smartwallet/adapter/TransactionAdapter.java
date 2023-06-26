package com.example.smartwallet.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartwallet.GlobalFunction;
import com.example.smartwallet.R;
import com.example.smartwallet.databinding.ItemTransactionBinding;
import com.example.smartwallet.listener.ITransactionClickListener;
import com.example.smartwallet.model.Category;
import com.example.smartwallet.model.Transaction;
import com.example.smartwallet.model.Wallet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private final ArrayList<Transaction> transactionArrayList;
    private final ITransactionClickListener transactionClickListener;
    private final Map<String, String> walletMap;
    private final Map<String, String> categoryMap;

    public TransactionAdapter(ArrayList<Transaction> transactionArrayList, ITransactionClickListener transactionClickListener, ArrayList<Wallet> walletArrayList, ArrayList<Category> categoryArrayList) {
        this.transactionArrayList = transactionArrayList;
        this.transactionClickListener = transactionClickListener;

        walletMap = new HashMap<>();
        for (Wallet wallet : walletArrayList) {
            walletMap.put(wallet.getId(), wallet.getName());
        }

        categoryMap = new HashMap<>();
        for (Category category : categoryArrayList) {
            categoryMap.put(category.getId(), category.getName());
        }
    }

    @NonNull
    @Override
    public TransactionAdapter.TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.TransactionViewHolder holder, int position) {
        Transaction currentItem = transactionArrayList.get(position);
        holder.textViewDetail.setText(currentItem.getDetail());
        holder.textViewAmount.setText(currentItem.getAmount().toString());
        holder.textViewDate.setText(GlobalFunction.getDayOfMonthFromTimestamp(currentItem.getDate()));
        if (currentItem.getType()) {
            holder.imageViewIcon.setImageResource(R.drawable.down_arrow);
        } else {
            holder.imageViewIcon.setImageResource(R.drawable.up_arrow);
        }
        holder.textViewMonthYear.setText(GlobalFunction.getMonthYearFromTimestamp(currentItem.getDate()));
        holder.textViewHourMinute.setText(GlobalFunction.getTimeFromTimestamp(currentItem.getDate()));

        String walletId = currentItem.getWalletId();
        String walletName = walletMap.get(walletId);
        holder.textViewWallet.setText(walletName);

        String categoryId = currentItem.getCategoryId();
        String categoryName = categoryMap.get(categoryId);
        holder.textViewCategory.setText(categoryName);

        holder.itemView.setOnClickListener(v -> transactionClickListener.onTransactionClick(currentItem));
    }

    @Override
    public int getItemCount() {
        return transactionArrayList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewDetail;
        private final TextView textViewCategory;
        private final TextView textViewDate;
        private final TextView textViewMonthYear;
        private final TextView textViewHourMinute;
        private final TextView textViewAmount;
        private final ImageView imageViewIcon;
        private final TextView textViewWallet;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            ItemTransactionBinding itemTransactionBinding = ItemTransactionBinding.bind(itemView);
            textViewDetail = itemTransactionBinding.textViewDetail;
            textViewCategory = itemTransactionBinding.textViewCategory;
            textViewDate = itemTransactionBinding.textViewDate;
            textViewMonthYear = itemTransactionBinding.textViewTextMonthYear;
            textViewHourMinute = itemTransactionBinding.textViewHourMinute;
            textViewAmount = itemTransactionBinding.textViewAmount;
            imageViewIcon = itemTransactionBinding.imageViewIcon;
            textViewWallet = itemTransactionBinding.textViewWallet;
        }
    }
}
