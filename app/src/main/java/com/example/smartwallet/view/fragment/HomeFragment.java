package com.example.smartwallet.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartwallet.databinding.FragmentHomeBinding;
import com.example.smartwallet.model.Wallet;
import com.example.smartwallet.viewmodel.WalletViewModel;
import com.example.smartwallet.R;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding fragmentHomeBinding;
    private TextView totalBalance;
    private ImageView showHide;
    private boolean isShow = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        View rootView = fragmentHomeBinding.getRoot();

        totalBalance = fragmentHomeBinding.textViewBalance;
        showHide = fragmentHomeBinding.imageViewShowHide;

        generateTotalBalance();
        processShowHide();
        displayReport();

        return rootView;
    }

    @SuppressLint("SetTextI18n")
    private void generateTotalBalance() {
        WalletViewModel walletViewModel = new ViewModelProvider(this).get(WalletViewModel.class);

        walletViewModel.getAllWallets().observe(getViewLifecycleOwner(), wallets -> {
            double totalBalanceValue = 0;
            for (Wallet wallet : wallets) {
                totalBalanceValue += wallet.getBalance();
            }

            if (isShow) {
                totalBalance.setText(totalBalanceValue + " đ");
            } else {
                totalBalance.setText("**********");
            }
        });
    }

    private void displayReport() {
        TransactionReportFragment transactionReportFragment = new TransactionReportFragment();
        int layoutReportId = fragmentHomeBinding.layoutReport.getId();
        getChildFragmentManager().beginTransaction().replace(layoutReportId, transactionReportFragment).commit();
    }

    private void processShowHide() {
        showHide.setOnClickListener(view -> {
            if (isShow) {
                showHide.setImageResource(R.drawable.hidden);
            } else {
                showHide.setImageResource(R.drawable.show);
            }

            isShow = !isShow;
            generateTotalBalance();
        });
    }
}
