package com.example.smartwallet.view.fragment.wallet;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartwallet.R;
import com.example.smartwallet.adapter.WalletAdapter;
import com.example.smartwallet.databinding.FragmentWalletBinding;
import com.example.smartwallet.model.Wallet;
import com.example.smartwallet.utils.SessionManager;
import com.example.smartwallet.viewmodel.WalletViewModel;


public class WalletFragment extends Fragment {
    private FragmentWalletBinding fragmentWalletBinding;
    private WalletAdapter walletAdapter;
    private WalletViewModel walletViewModel;
    private RecyclerView recyclerViewWallet;
    private Wallet longPressedWallet = new Wallet();
    private SessionManager sessionManager;

    public WalletFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sessionManager = new SessionManager(requireContext());
        fragmentWalletBinding = FragmentWalletBinding.inflate(inflater, container, false);

        renderListWallet();
        registerForContextMenu(recyclerViewWallet);

        TextView addButton = fragmentWalletBinding.textView;
        addButton.setOnClickListener(v ->
                getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new AddWalletFragment())
                .addToBackStack(null).commit());
        return fragmentWalletBinding.getRoot();
    }

    private void renderListWallet() {
        recyclerViewWallet = fragmentWalletBinding.recyclerWallet;
        recyclerViewWallet.setLayoutManager(new GridLayoutManager(requireActivity(), 1));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        recyclerViewWallet.addItemDecoration(dividerItemDecoration);

        walletViewModel = new ViewModelProvider(this).get(WalletViewModel.class);

        walletViewModel.getAllWallets(sessionManager.getUsername()).observe(getViewLifecycleOwner(), walletArrayList -> {
            if (walletArrayList.size() == 0) {
                fragmentWalletBinding.textViewDataStatus.setVisibility(View.VISIBLE);
            } else {
                fragmentWalletBinding.recyclerWallet.setVisibility(View.VISIBLE);
                walletAdapter = new WalletAdapter(walletArrayList, (wallet, isLongClick) -> {
                    if (!isLongClick) {
                        switchToWalletDetailFragment(wallet);
                    }
                    if (isLongClick) {
                        showContextMenu(wallet);
                    }
                });
                recyclerViewWallet.setAdapter(walletAdapter);
            }
        });
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.hold_menu, menu);
        menu.setHeaderTitle(longPressedWallet.getName());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit_item) {
            switchToUpdateWalletFragment();
        } else if (item.getItemId() == R.id.delete_item) {
            walletViewModel.deleteWallet(longPressedWallet).observe(getViewLifecycleOwner(), result -> {
                if (result) {
                    Toast.makeText(requireContext(), "Xoá thành công " + longPressedWallet.getName() + "!", Toast.LENGTH_SHORT).show();
                    renderListWallet();
                } else {
                    Toast.makeText(requireContext(), "Xảy ra lỗi khi xoá " + longPressedWallet.getName() + "!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return super.onContextItemSelected(item);
    }

    private void showContextMenu(Wallet Wallet) {
        longPressedWallet = Wallet;
        recyclerViewWallet.showContextMenu();
    }

    private void switchToUpdateWalletFragment() {
        UpdateWalletFragment updateWalletFragment = new UpdateWalletFragment();

        Bundle bundle = new Bundle();
        if (longPressedWallet.getId() != null) {
            bundle.putString("id", longPressedWallet.getId());
            bundle.putString("userId", longPressedWallet.getUserId());
            bundle.putString("name", longPressedWallet.getName());
            bundle.putFloat("balance", longPressedWallet.getBalance());
        }
        updateWalletFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction().replace(R.id.container, updateWalletFragment).addToBackStack(null).commit();
    }

    private void switchToWalletDetailFragment(Wallet wallet) {
        WalletDetailFragment walletDetailFragment = new WalletDetailFragment();

        Bundle bundle = new Bundle();
        if (wallet.getId() != null) {
            bundle.putString("id", wallet.getId());
            bundle.putString("userId", wallet.getUserId());
            bundle.putString("name", wallet.getName());
            bundle.putFloat("balance", wallet.getBalance());
        }
        walletDetailFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction().replace(R.id.container, walletDetailFragment).addToBackStack(null).commit();
    }
}