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

import com.example.smartwallet.adapter.MostTransactionAdapter;
import com.example.smartwallet.databinding.FragmentMostlyTransactionBinding;
import com.example.smartwallet.model.Category;
import com.example.smartwallet.model.Transaction;
import com.example.smartwallet.viewmodel.CategoryViewModel;
import com.example.smartwallet.viewmodel.TransactionViewModel;

import java.util.ArrayList;
import java.util.Comparator;

public class MostlyTransactionFragment extends Fragment {
    private FragmentMostlyTransactionBinding fragmentMostlyTransactionBinding;
    private MostTransactionAdapter mostTransactionAdapter;
    private RecyclerView recyclerViewCategoryByTransaction;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentMostlyTransactionBinding = FragmentMostlyTransactionBinding.inflate(inflater, container, false);

        generateRecyclerView();

        return fragmentMostlyTransactionBinding.getRoot();
    }

    private void generateRecyclerView() {
        recyclerViewCategoryByTransaction = fragmentMostlyTransactionBinding.recyclerMostlyTransaction;
        recyclerViewCategoryByTransaction.setLayoutManager(new GridLayoutManager(requireActivity(), 1));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        recyclerViewCategoryByTransaction.addItemDecoration(dividerItemDecoration);

        CategoryViewModel categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        TransactionViewModel transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), listCategories -> transactionViewModel.getAllTransactions().observe(getViewLifecycleOwner(), listTransactions -> {
            double totalAmount = 0;
            for (Transaction transaction : listTransactions) {
                if (transaction.getType()) {
                    totalAmount += transaction.getAmount();
                }
            }

            ArrayList<CategoryByTransaction> categoryProportions = new ArrayList<>();
            for (Category category : listCategories) {
                double categoryAmount = 0;
                for (Transaction transaction : listTransactions) {
                    if (transaction.getCategoryId().equals(category.getId()) && transaction.getType()) {
                        categoryAmount += transaction.getAmount();
                    }
                }
                int proportion = (int) Math.round((categoryAmount / totalAmount) * 100);
                CategoryByTransaction entry = new CategoryByTransaction(category.getName(), categoryAmount, proportion);

                categoryProportions.add(entry);
            }


            // Sort by Proportion
            Comparator<CategoryByTransaction> comparator = (entry1, entry2) -> {
                // Sort in descending order
                return Double.compare(entry2.getProportion(), entry1.getProportion());
            };
            categoryProportions.sort(comparator);
            ArrayList<CategoryByTransaction> cutList = new ArrayList<>(categoryProportions.subList(0, 6));

            mostTransactionAdapter = new MostTransactionAdapter(cutList);
            recyclerViewCategoryByTransaction.setAdapter(mostTransactionAdapter);
        }));
    }

    public static class CategoryByTransaction {
        private final String name;
        private final Double amount;
        private final Integer proportion;

        public CategoryByTransaction(String name, Double amount, Integer proportion) {
            this.name = name;
            this.amount = amount;
            this.proportion = proportion;
        }

        public String getName() {
            return name;
        }

        public Double getAmount() {
            return amount;
        }

        public Integer getProportion() {
            return proportion;
        }
    }
}