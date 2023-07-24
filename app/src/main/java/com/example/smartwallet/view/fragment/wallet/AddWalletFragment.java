package com.example.smartwallet.view.fragment.wallet;

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

import com.example.smartwallet.databinding.FragmentAddWalletBinding;
import com.example.smartwallet.model.Wallet;
import com.example.smartwallet.utils.SessionManager;
import com.example.smartwallet.viewmodel.WalletViewModel;

public class AddWalletFragment extends Fragment {
    private EditText inputBalance;
    private EditText inputName;
    private WalletViewModel walletViewModel;
    private SessionManager sessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        walletViewModel = new ViewModelProvider(this).get(WalletViewModel.class);
        sessionManager = new SessionManager(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        com.example.smartwallet.databinding.FragmentAddWalletBinding fragmentAddWalletBinding = FragmentAddWalletBinding.inflate(inflater, container, false);

        inputBalance = fragmentAddWalletBinding.editTextNumberDecimal;
        inputName = fragmentAddWalletBinding.editTextInputDetail;
        Button addButton = fragmentAddWalletBinding.buttonAdd;
        Button cancelButton = fragmentAddWalletBinding.buttonCancel;

        addButton.setOnClickListener(v -> {
            if (checkInput()) {
                Wallet wallet = new Wallet(Float.parseFloat(inputBalance.getText().toString().trim()), inputName.getText().toString().trim(), sessionManager.getUsername());
                walletViewModel.addWallet(wallet).observe(getViewLifecycleOwner(), result -> {
                    if (result) {
                        Toast.makeText(requireContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                        clearInput();
                    } else {
                        Toast.makeText(requireContext(), "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        cancelButton.setOnClickListener(v -> {
            clearInput();
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return fragmentAddWalletBinding.getRoot();
    }

    private Boolean checkInput() {
        String money = inputBalance.getText().toString().trim();
        String detail = inputName.getText().toString().trim();

        if (TextUtils.isEmpty(money)) {
            inputBalance.setError("Hãy nhập số tiền");
            inputName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(detail)) {
            inputBalance.setError("Hãy nhập chi tiết");
            inputName.requestFocus();
            return false;
        }
        return true;
    }

    private void clearInput() {
        inputName.setText("");
        inputBalance.setText("");
    }
}