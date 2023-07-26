package com.example.smartwallet.view.fragment.account;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.smartwallet.R;
import com.example.smartwallet.databinding.FragmentChangePasswordDialogBinding;
import com.example.smartwallet.utils.SessionManager;
import com.example.smartwallet.viewmodel.UserViewModel;

public class ChangePasswordDialogFragment extends DialogFragment {
    private UserViewModel userViewModel;
    private FragmentChangePasswordDialogBinding fragmentChangePasswordDialogBinding;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_change_password_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext()).setView(view).create();

        fragmentChangePasswordDialogBinding = FragmentChangePasswordDialogBinding.bind(view);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        new SessionManager(requireContext());

        fragmentChangePasswordDialogBinding.buttonEdit.setOnClickListener(v -> {
            if (checkInput()) {
                if (fragmentChangePasswordDialogBinding.editTextInputNewPass.getText().toString().equals(fragmentChangePasswordDialogBinding.editTextReInputNewPass.getText().toString())) {
                    changePassword(fragmentChangePasswordDialogBinding.editTextInputNewPass.getText().toString());
                }
            }
        });

        fragmentChangePasswordDialogBinding.buttonDelete.setOnClickListener(v -> {
            clearInput();
            dismiss();
        });

        return dialog;
    }

    private Boolean checkInput() {
        String currentPass = fragmentChangePasswordDialogBinding.editTextInputCurrentPass.getText().toString().trim();
        String newPass = fragmentChangePasswordDialogBinding.editTextInputNewPass.getText().toString().trim();
        String reNewPass = fragmentChangePasswordDialogBinding.editTextReInputNewPass.getText().toString().trim();

        if (TextUtils.isEmpty(currentPass)) {
            fragmentChangePasswordDialogBinding.editTextInputCurrentPass.setError("Hãy nhập tên danh mục");
            fragmentChangePasswordDialogBinding.editTextInputCurrentPass.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(newPass)) {
            fragmentChangePasswordDialogBinding.editTextInputNewPass.setError("Hãy nhập tên danh mục");
            fragmentChangePasswordDialogBinding.editTextInputNewPass.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(reNewPass)) {
            fragmentChangePasswordDialogBinding.editTextReInputNewPass.setError("Hãy nhập tên danh mục");
            fragmentChangePasswordDialogBinding.editTextReInputNewPass.requestFocus();
            return false;
        }
        if (!fragmentChangePasswordDialogBinding.editTextInputNewPass.getText().toString().equals(fragmentChangePasswordDialogBinding.editTextReInputNewPass.getText().toString())) {
            fragmentChangePasswordDialogBinding.editTextReInputNewPass.setError("Mật khẩu mới không giống nhau!");
            fragmentChangePasswordDialogBinding.editTextReInputNewPass.requestFocus();
            return false;
        }
        return true;
    }

    private void clearInput() {
        fragmentChangePasswordDialogBinding.editTextInputCurrentPass.setText("");
        fragmentChangePasswordDialogBinding.editTextInputNewPass.setText("");
        fragmentChangePasswordDialogBinding.editTextReInputNewPass.setText("");
    }

    private void changePassword(String newPassword) {
        userViewModel.changePassword(newPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                clearInput();
                dismiss();
            } else {
                Toast.makeText(requireContext(), "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
            }
        });
    }
}