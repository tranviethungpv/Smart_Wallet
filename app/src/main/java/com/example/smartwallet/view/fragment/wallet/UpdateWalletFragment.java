package com.example.smartwallet.view.fragment.wallet;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.smartwallet.databinding.FragmentUpdateWalletBinding;
import com.example.smartwallet.model.Wallet;
import com.example.smartwallet.utils.SessionManager;
import com.example.smartwallet.viewmodel.WalletViewModel;

public class UpdateWalletFragment extends Fragment {
    private FragmentUpdateWalletBinding fragmentUpdateWalletBinding;
    private WalletViewModel walletViewModel;
    private EditText inputBalance;
    private EditText inputName;
    private Wallet selectedWallet;
    private SessionManager sessionManager;


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        walletViewModel = new ViewModelProvider(this).get(WalletViewModel.class);
        sessionManager = new SessionManager(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentUpdateWalletBinding = FragmentUpdateWalletBinding.inflate(inflater, container, false);

        inputBalance = fragmentUpdateWalletBinding.editTextNumberDecimal;
        inputName = fragmentUpdateWalletBinding.editTextInputDetail;
        Button buttonUpdate = fragmentUpdateWalletBinding.buttonAdd;
        Button buttonCancel = fragmentUpdateWalletBinding.buttonCancel;

        getData();
        fillData();

        buttonUpdate.setOnClickListener(item -> {
            if (checkInput()) {
                Wallet wallet = new Wallet(selectedWallet.getId(), Float.parseFloat(inputBalance.getText().toString().trim()), inputName.getText().toString().trim(), sessionManager.getUsername());
                walletViewModel.updateWallet(wallet).observe(getViewLifecycleOwner(), result -> {
                    if (result) {
                        Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        buttonCancel.setOnClickListener(item -> getParentFragmentManager().popBackStack());

        return fragmentUpdateWalletBinding.getRoot();
    }

    private void getData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String id = bundle.getString("id");
            String userId = bundle.getString("userId");
            String name = bundle.getString("name");
            float balance = bundle.getFloat("balance");
            selectedWallet = new Wallet(id, balance, name, userId);
        }
    }

    @SuppressLint("SetTextI18n")
    public void fillData() {
        fragmentUpdateWalletBinding.editTextInputDetail.setText(selectedWallet.getName());
        fragmentUpdateWalletBinding.editTextNumberDecimal.setText(selectedWallet.getBalance().toString());
    }

    private Boolean checkInput() {
        String money = inputBalance.getText().toString().trim();
        String detail = inputName.getText().toString().trim();

        if (TextUtils.isEmpty(money)) {
            inputBalance.setError("Hãy nhập số tiền");
            inputBalance.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(detail)) {
            inputName.setError("Hãy nhập chi tiết");
            inputName.requestFocus();
            return false;
        }
        return true;
    }
}