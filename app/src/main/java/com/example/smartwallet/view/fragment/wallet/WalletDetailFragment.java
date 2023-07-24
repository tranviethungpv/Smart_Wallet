package com.example.smartwallet.view.fragment.wallet;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.smartwallet.R;
import com.example.smartwallet.databinding.FragmentWalletDetailBinding;
import com.example.smartwallet.model.Wallet;
import com.example.smartwallet.viewmodel.WalletViewModel;

public class WalletDetailFragment extends Fragment {
    private FragmentWalletDetailBinding fragmentWalletDetailBinding;
    private Wallet selectedWallet = new Wallet();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentWalletDetailBinding = FragmentWalletDetailBinding.inflate(inflater, container, false);

        getData();
        fillData();

        Button editButton = fragmentWalletDetailBinding.buttonEdit;
        Button deleteButton = fragmentWalletDetailBinding.buttonDelete;

        editButton.setOnClickListener(view -> switchToUpdateWalletFragment());

        deleteButton.setOnClickListener(view -> deleteWallet());

        return fragmentWalletDetailBinding.getRoot();
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
        fragmentWalletDetailBinding.editTextInputDetail.setText(selectedWallet.getName());
        fragmentWalletDetailBinding.editTextNumberDecimal.setText(selectedWallet.getBalance().toString() + " đ");
    }

    private void switchToUpdateWalletFragment() {
        UpdateWalletFragment updateWalletFragment = new UpdateWalletFragment();

        Bundle bundle = new Bundle();
        if (selectedWallet.getId() != null) {
            bundle.putString("id", selectedWallet.getId());
            bundle.putString("userId", selectedWallet.getUserId());
            bundle.putString("name", selectedWallet.getName());
            bundle.putFloat("balance", selectedWallet.getBalance());
        }
        updateWalletFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction().replace(R.id.container, updateWalletFragment).addToBackStack(null).commit();
    }

    private void deleteWallet() {
        WalletViewModel walletViewModel = new ViewModelProvider(this).get(WalletViewModel.class);
        walletViewModel.deleteWallet(selectedWallet).observe(getViewLifecycleOwner(), result -> {
            if (result) {
                Toast.makeText(requireContext(), "Xoá thành công " + selectedWallet.getName() + "!", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().beginTransaction().replace(R.id.container, new WalletFragment()).addToBackStack(null).commit();
            } else {
                Toast.makeText(requireContext(), "Xảy ra lỗi khi xoá " + selectedWallet.getName() + "!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}