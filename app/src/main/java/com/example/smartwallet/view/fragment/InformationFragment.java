package com.example.smartwallet.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartwallet.databinding.FragmentInformationBinding;
import com.example.smartwallet.utils.SessionManager;
import com.example.smartwallet.view.activity.AccountActivity;
import com.example.smartwallet.view.fragment.account.ChangePasswordDialogFragment;

public class InformationFragment extends Fragment {
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        com.example.smartwallet.databinding.FragmentInformationBinding fragmentInformationBinding = FragmentInformationBinding.inflate(inflater, container, false);
        sessionManager = new SessionManager(requireContext());

        fragmentInformationBinding.textViewEmail.setText(sessionManager.getUsername());
        fragmentInformationBinding.buttonDelete.setOnClickListener(v -> logOut());
        fragmentInformationBinding.buttonEdit.setOnClickListener(v -> changePassword());

        return fragmentInformationBinding.getRoot();
    }

    private void changePassword() {
        ChangePasswordDialogFragment changePasswordDialogFragment = new ChangePasswordDialogFragment();
        changePasswordDialogFragment.show(getChildFragmentManager(), "ChangePasswordDialog");
    }

    private void logOut() {
        sessionManager.clearSession();
        startActivity(new Intent(requireActivity(), AccountActivity.class));
    }
}