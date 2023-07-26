package com.example.smartwallet.view.fragment.transaction;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.smartwallet.GlobalFunction;
import com.example.smartwallet.R;
import com.example.smartwallet.adapter.TransactionAdapter;
import com.example.smartwallet.databinding.FragmentTransactionBinding;
import com.example.smartwallet.model.Category;
import com.example.smartwallet.model.Transaction;
import com.example.smartwallet.model.Wallet;
import com.example.smartwallet.utils.SessionManager;
import com.example.smartwallet.viewmodel.CategoryViewModel;
import com.example.smartwallet.viewmodel.TransactionViewModel;
import com.example.smartwallet.viewmodel.WalletViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class TransactionFragment extends Fragment {
    private FragmentTransactionBinding fragmentTransactionBinding;
    private TransactionAdapter transactionAdapter;
    private TransactionViewModel transactionViewModel;
    private RecyclerView recyclerViewTransaction;
    private ArrayList<Wallet> walletList;
    private ArrayList<Category> categoryList;
    private ArrayList<Transaction> transactionList;
    private Transaction longPressedTransaction = new Transaction();
    private SessionManager sessionManager;
    private WalletViewModel walletViewModel;
    private CategoryViewModel categoryViewModel;

    public TransactionFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sessionManager = new SessionManager(requireContext());
        fragmentTransactionBinding = FragmentTransactionBinding.inflate(inflater, container, false);

        renderListTransaction();
        initListener();
        registerForContextMenu(recyclerViewTransaction);

        fragmentTransactionBinding.imgSearch.setOnClickListener(v -> GlobalFunction.hideSoftKeyboard(requireActivity()));

        return fragmentTransactionBinding.getRoot();
    }

    private void initListener() {
        fragmentTransactionBinding.edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                renderListTransactionsByHint(strKey, sessionManager.getUsername());
            }
        });
    }

    private void renderListTransaction() {
        recyclerViewTransaction = fragmentTransactionBinding.recyclerTransaction;
        recyclerViewTransaction.setLayoutManager(new GridLayoutManager(requireActivity(), 1));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        recyclerViewTransaction.addItemDecoration(dividerItemDecoration);

        walletViewModel = new ViewModelProvider(this).get(WalletViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        transactionViewModel.getAllTransactions(sessionManager.getUsername()).observe(getViewLifecycleOwner(), transactionArrayList -> {
            transactionList = new ArrayList<>(transactionArrayList);
            // Sort list by Timestamp
            Comparator<Transaction> descendingComparator = Comparator.comparing(Transaction::getDate).reversed();
            transactionList.sort(descendingComparator);

            if (transactionList.size() == 0) {
                fragmentTransactionBinding.textViewDataStatus.setVisibility(View.VISIBLE);
            } else {
                fragmentTransactionBinding.recyclerTransaction.setVisibility(View.VISIBLE);
                fragmentTransactionBinding.textViewDataStatus.setVisibility(View.GONE);

                // Observe wallet and category data to retrieve the lists
                walletViewModel.getAllWallets(sessionManager.getUsername()).observe(getViewLifecycleOwner(), wallets -> {
                    if (wallets != null) {
                        walletList = new ArrayList<>(wallets);

                        categoryViewModel.getAllCategories(sessionManager.getUsername()).observe(getViewLifecycleOwner(), categories -> {
                            if (categories != null) {
                                categoryList = new ArrayList<>(categories);
                                transactionAdapter = new TransactionAdapter(transactionList, (transaction, isLongClick) -> {
                                    if (!isLongClick) {
                                        switchToTransactionDetailFragment(transaction);
                                    }
                                    if (isLongClick) {
                                        showContextMenu(transaction);
                                    }
                                }, walletList, categoryList);
                                recyclerViewTransaction.setAdapter(transactionAdapter);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.hold_menu, menu);
        menu.setHeaderTitle(longPressedTransaction.getDetail());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit_item) {
            switchToUpdateTransactionFragment();
        } else if (item.getItemId() == R.id.delete_item) {
            transactionViewModel.deleteTransaction(longPressedTransaction).observe(getViewLifecycleOwner(), result -> {
                updateWalletBalance(longPressedTransaction);
                if (result) {
                    Toast.makeText(requireContext(), "Xoá thành công " + longPressedTransaction.getDetail() + "!", Toast.LENGTH_SHORT).show();
                    renderListTransaction();
                } else {
                    Toast.makeText(requireContext(), "Xảy ra lỗi khi xoá " + longPressedTransaction.getDetail() + "!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return super.onContextItemSelected(item);
    }

    private void showContextMenu(Transaction transaction) {
        longPressedTransaction = transaction;
        recyclerViewTransaction.showContextMenu();
    }

    private void switchToTransactionDetailFragment(Transaction transaction) {
        TransactionDetailFragment transactionDetailFragment = new TransactionDetailFragment();

        Bundle bundle = new Bundle();
        if (transaction.getId() != null) {
            bundle.putString("id", transaction.getId());
            bundle.putString("userId", transaction.getUserId());
            bundle.putString("detail", transaction.getDetail());
            bundle.putString("categoryId", transaction.getCategoryId());
            bundle.putString("walletId", transaction.getWalletId());
            bundle.putFloat("amount", transaction.getAmount());
            bundle.putBoolean("type", transaction.getType());
            bundle.putSerializable("date", transaction.getDate().toDate());
        }
        transactionDetailFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction().replace(R.id.container, transactionDetailFragment).addToBackStack(null).commit();
    }

    private void switchToUpdateTransactionFragment() {
        UpdateTransactionFragment updateTransactionFragment = new UpdateTransactionFragment();

        Bundle bundle = new Bundle();
        if (longPressedTransaction.getId() != null) {
            bundle.putString("id", longPressedTransaction.getId());
            bundle.putString("userId", longPressedTransaction.getUserId());
            bundle.putString("detail", longPressedTransaction.getDetail());
            bundle.putString("categoryId", longPressedTransaction.getCategoryId());
            bundle.putString("walletId", longPressedTransaction.getWalletId());
            bundle.putFloat("amount", longPressedTransaction.getAmount());
            bundle.putBoolean("type", longPressedTransaction.getType());
            bundle.putSerializable("date", longPressedTransaction.getDate().toDate());
        }
        updateTransactionFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction().replace(R.id.container, updateTransactionFragment).addToBackStack(null).commit();
    }

    private void updateWalletBalance(Transaction changedTransaction) {
        walletViewModel.getAllWallets(sessionManager.getUsername()).observe(getViewLifecycleOwner(), listWallets -> {
            Wallet updatedWallet;
            for (Wallet wallet : listWallets) {
                if (Objects.equals(wallet.getId(), changedTransaction.getWalletId())) {
                    if (changedTransaction.getType()) {
                        updatedWallet = new Wallet(wallet.getId(), wallet.getBalance() + changedTransaction.getAmount(), wallet.getName(), wallet.getUserId());
                    } else {
                        updatedWallet = new Wallet(wallet.getId(), wallet.getBalance() - changedTransaction.getAmount(), wallet.getName(), wallet.getUserId());
                    }
                    walletViewModel.updateWallet(updatedWallet).observe(getViewLifecycleOwner(), result -> {
                        if (result) {
                            Log.d("Log", "Updated complete");
                        } else {
                            Log.d("Log", "Update failed");
                        }
                    });
                }
            }
        });
    }

    private void renderListTransactionsByHint(String input, String userId) {
        transactionViewModel.getTransactionsByHint(input, userId).observe(getViewLifecycleOwner(), transactionArrayList -> {
            transactionList.clear();
            transactionList = new ArrayList<>(transactionArrayList);
            // Sort list by Timestamp
            Comparator<Transaction> descendingComparator = Comparator.comparing(Transaction::getDate).reversed();
            transactionList.sort(descendingComparator);

            if (transactionList.size() == 0) {
                fragmentTransactionBinding.textViewDataStatus.setVisibility(View.VISIBLE);
            } else {
                fragmentTransactionBinding.recyclerTransaction.setVisibility(View.VISIBLE);
                fragmentTransactionBinding.textViewDataStatus.setVisibility(View.GONE);

                // Observe wallet and category data to retrieve the lists
                walletViewModel.getAllWallets(sessionManager.getUsername()).observe(getViewLifecycleOwner(), wallets -> {
                    if (wallets != null) {
                        walletList = new ArrayList<>(wallets);

                        categoryViewModel.getAllCategories(sessionManager.getUsername()).observe(getViewLifecycleOwner(), categories -> {
                            if (categories != null) {
                                categoryList = new ArrayList<>(categories);
                                transactionAdapter = new TransactionAdapter(transactionList, (transaction, isLongClick) -> {
                                    if (!isLongClick) {
                                        switchToTransactionDetailFragment(transaction);
                                    }
                                    if (isLongClick) {
                                        showContextMenu(transaction);
                                    }
                                }, walletList, categoryList);
                                recyclerViewTransaction.setAdapter(transactionAdapter);
                            }
                        });
                    }
                });
            }
        });
    }
}
