package com.example.smartwallet.view.fragment.transaction;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.smartwallet.R;
import com.example.smartwallet.databinding.FragmentTransactionDetailBinding;
import com.example.smartwallet.model.Category;
import com.example.smartwallet.model.Transaction;
import com.example.smartwallet.model.Wallet;
import com.example.smartwallet.utils.SessionManager;
import com.example.smartwallet.viewmodel.CategoryViewModel;
import com.example.smartwallet.viewmodel.TransactionViewModel;
import com.example.smartwallet.viewmodel.WalletViewModel;
import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class TransactionDetailFragment extends Fragment {
    private FragmentTransactionDetailBinding fragmentTransactionDetailBinding;
    private Transaction selectedTransaction;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentTransactionDetailBinding = FragmentTransactionDetailBinding.inflate(inflater, container, false);
        
        getData();
        fillData();

        Button editButton = fragmentTransactionDetailBinding.buttonEdit;
        Button deleteButton = fragmentTransactionDetailBinding.buttonDelete;

        editButton.setOnClickListener(view -> switchToUpdateTransactionFragment());

        deleteButton.setOnClickListener(view -> deleteTransaction());

        return fragmentTransactionDetailBinding.getRoot();
    }

    private void getData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String id = bundle.getString("id");
            String userId = bundle.getString("userId");
            String detail = bundle.getString("detail");
            String categoryId = bundle.getString("categoryId");
            String walletId = bundle.getString("walletId");
            float amount = bundle.getFloat("amount");
            boolean type = bundle.getBoolean("type");
            Date date = (Date) bundle.getSerializable("date");
            Timestamp timestamp = new Timestamp(date);
            selectedTransaction = new Transaction(id, userId, walletId, categoryId, amount, detail, type, timestamp);
        }
    }

    @SuppressLint("SetTextI18n")
    public void fillData() {
        fragmentTransactionDetailBinding.textViewDetail.setText(selectedTransaction.getDetail());
        fragmentTransactionDetailBinding.textViewBalance.setText(selectedTransaction.getAmount().toString() + " đ");
        if (!selectedTransaction.getType()) {
            fragmentTransactionDetailBinding.textViewBalance.setTextColor(Color.BLUE);
        } else {
            fragmentTransactionDetailBinding.textViewBalance.setTextColor(Color.RED);
        }
        // Convert Timestamp to java.util.Date
        Date dateTimestamp = selectedTransaction.getDate().toDate();

        // Use Calendar to extract month and year
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTimestamp);
        int month = calendar.get(Calendar.MONTH) + 1; // Adding 1 to get the month in 1-based index
        int year = calendar.get(Calendar.YEAR);
        int date = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String day;
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                day = "Chủ Nhật";
                break;
            case 2:
                day = "Thứ Hai";
                break;
            case 3:
                day = "Thứ Ba";
                break;
            case 4:
                day = "Thứ Tư";
                break;
            case 5:
                day = "Thứ Năm";
                break;
            case 6:
                day = "Thứ Sáu";
                break;
            case 7:
                day = "Thứ Bảy";
                break;
            default:
                day = null;
                break;
        }
        fragmentTransactionDetailBinding.textViewDateTime.setText(day + ", " + date + " tháng " + month + " " + year + ", " + hour + ":" + minute);
        WalletViewModel walletViewModel = new ViewModelProvider(this).get(WalletViewModel.class);
        SessionManager sessionManager = new SessionManager(requireContext());
        walletViewModel.getAllWallets(sessionManager.getUsername()).observe(getViewLifecycleOwner(), listWallets -> {
            for (Wallet wallet: listWallets) {
                if (Objects.equals(wallet.getId(), selectedTransaction.getWalletId())) {
                    fragmentTransactionDetailBinding.textViewWalletName.setText(wallet.getName().trim());
                }
            }
        });
        CategoryViewModel categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        categoryViewModel.getAllCategories(sessionManager.getUsername()).observe(getViewLifecycleOwner(), listCategories -> {
            for (Category category: listCategories) {
                if (Objects.equals(category.getId(), selectedTransaction.getCategoryId())) {
                    fragmentTransactionDetailBinding.textViewCategoryName.setText(category.getName().trim());
                }
            }
        });
    }

    private void switchToUpdateTransactionFragment() {
        UpdateTransactionFragment updateTransactionFragment = new UpdateTransactionFragment();

        Bundle bundle = new Bundle();
        if (selectedTransaction.getId() != null) {
            bundle.putString("id", selectedTransaction.getId());
            bundle.putString("userId", selectedTransaction.getUserId());
            bundle.putString("detail", selectedTransaction.getDetail());
            bundle.putString("categoryId", selectedTransaction.getCategoryId());
            bundle.putString("walletId", selectedTransaction.getWalletId());
            bundle.putFloat("amount", selectedTransaction.getAmount());
            bundle.putBoolean("type", selectedTransaction.getType());
            bundle.putSerializable("date", selectedTransaction.getDate().toDate());
        }
        updateTransactionFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction().replace(R.id.container, updateTransactionFragment).addToBackStack(null).commit();
    }

    private void deleteTransaction() {
        TransactionViewModel transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        transactionViewModel.deleteTransaction(selectedTransaction).observe(getViewLifecycleOwner(), result -> {
            updateWalletBalance(selectedTransaction);
            if (result) {
                Toast.makeText(requireContext(), "Xoá thành công " + selectedTransaction.getDetail() + "!", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().beginTransaction().replace(R.id.container, new TransactionFragment()).addToBackStack(null).commit();
            } else {
                Toast.makeText(requireContext(), "Xảy ra lỗi khi xoá " + selectedTransaction.getDetail() + "!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWalletBalance(Transaction changedTransaction) {
        WalletViewModel walletViewModel = new ViewModelProvider(this).get(WalletViewModel.class);
        SessionManager sessionManager = new SessionManager(requireContext());
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

}