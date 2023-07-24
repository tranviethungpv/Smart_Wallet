package com.example.smartwallet.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartwallet.R;
import com.example.smartwallet.databinding.FragmentManageBinding;
import com.example.smartwallet.view.fragment.category.CategoryFragment;
import com.example.smartwallet.view.fragment.wallet.WalletFragment;

public class ManageFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentManageBinding fragmentManageBinding = FragmentManageBinding.inflate(inflater, container, false);

        fragmentManageBinding.layoutButtonManageWallet.setOnClickListener(view -> replaceFragment(new WalletFragment()));
        fragmentManageBinding.layoutButtonManageCategory.setOnClickListener(view -> replaceFragment(new CategoryFragment()));

        return fragmentManageBinding.getRoot();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}