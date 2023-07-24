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
import com.example.smartwallet.databinding.ItemWalletBinding;
import com.example.smartwallet.listener.IWalletClickListener;
import com.example.smartwallet.model.Wallet;

import java.util.ArrayList;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.WalletViewHolder> {
    private final ArrayList<Wallet> walletArrayList;
    private final IWalletClickListener walletClickListener;

    public WalletAdapter(ArrayList<Wallet> walletArrayList, IWalletClickListener walletClickListener) {
        this.walletArrayList = walletArrayList;
        this.walletClickListener = walletClickListener;
    }

    @NonNull
    @Override
    public WalletAdapter.WalletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallet, parent, false);
        return new WalletAdapter.WalletViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WalletAdapter.WalletViewHolder holder, int position) {
        Wallet currentItem = walletArrayList.get(position);
        holder.textViewWalletName.setText(currentItem.getName());
        holder.textViewWalletBalance.setText(currentItem.getBalance().toString() + " đ");
        holder.imageViewIcon.setImageResource(R.drawable.wallet);

        holder.itemView.setOnClickListener(v -> walletClickListener.onTransactionClick(currentItem, false));
        holder.itemView.setOnLongClickListener(v -> {
            walletClickListener.onTransactionClick(currentItem, true);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return walletArrayList.size();
    }

    public static class WalletViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewWalletBalance;
        private final TextView textViewWalletName;
        private final ImageView imageViewIcon;

        public WalletViewHolder(@NonNull View itemView) {
            super(itemView);
            ItemWalletBinding itemWalletBinding = ItemWalletBinding.bind(itemView);
            textViewWalletName = itemWalletBinding.textViewWalletName;
            textViewWalletBalance = itemWalletBinding.textViewWalletBalance;
            imageViewIcon = itemWalletBinding.imageViewIcon;
        }
    }
}
