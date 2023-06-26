package com.example.smartwallet.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartwallet.adapter.TransactionAdapter;
import com.example.smartwallet.databinding.FragmentTransactionBinding;
import com.example.smartwallet.listener.ITransactionClickListener;
import com.example.smartwallet.model.Category;
import com.example.smartwallet.model.Transaction;
import com.example.smartwallet.model.Wallet;
import com.example.smartwallet.viewmodel.CategoryViewModel;
import com.example.smartwallet.viewmodel.TransactionViewModel;
import com.example.smartwallet.viewmodel.WalletViewModel;

import java.util.ArrayList;
import java.util.Comparator;

public class TransactionFragment extends Fragment {
    private TransactionAdapter transactionAdapter;
    private RecyclerView recyclerViewTransaction;
    private ArrayList<Wallet> walletList;
    private ArrayList<Category> categoryList;

    public TransactionFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentTransactionBinding fragmentTransactionBinding = FragmentTransactionBinding.inflate(inflater, container, false);

        recyclerViewTransaction = fragmentTransactionBinding.recyclerTransaction;
        recyclerViewTransaction.setLayoutManager(new GridLayoutManager(requireActivity(), 1));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        recyclerViewTransaction.addItemDecoration(dividerItemDecoration);

        WalletViewModel walletViewModel = new ViewModelProvider(this).get(WalletViewModel.class);
        CategoryViewModel categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        TransactionViewModel transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        transactionViewModel.getAllTransactions().observe(getViewLifecycleOwner(), transactionArrayList -> {
            // Sort list by Timestamp
            Comparator<Transaction> descendingComparator = Comparator.comparing(Transaction::getDate).reversed();
            transactionArrayList.sort(descendingComparator);

            // Observe wallet and category data to retrieve the lists
            walletViewModel.getAllWallets().observe(getViewLifecycleOwner(), wallets -> {
                if (wallets != null) {
                    walletList = new ArrayList<>(wallets);

                    categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
                        if (categories != null) {
                            categoryList = new ArrayList<>(categories);

                            // Both walletList and categoryList have data
                            transactionAdapter = new TransactionAdapter(transactionArrayList, new ITransactionClickListener() {
                                @Override
                                public void onTransactionClick(Transaction transaction) {

                                }
                            }, walletList, categoryList);
                            recyclerViewTransaction.setAdapter(transactionAdapter);
                        }
                    });
                }
            });
        });

        return fragmentTransactionBinding.getRoot();
    }
}