package com.example.smartwallet.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.smartwallet.R;
import com.example.smartwallet.databinding.ActivityMainBinding;
import com.example.smartwallet.view.fragment.transaction.AddTransactionFragment;
import com.example.smartwallet.view.fragment.overview.HomeFragment;
import com.example.smartwallet.view.fragment.InformationFragment;
import com.example.smartwallet.view.fragment.ManageFragment;
import com.example.smartwallet.view.fragment.transaction.TransactionFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private final HomeFragment homeFragment = new HomeFragment();
    private final TransactionFragment transactionFragment = new TransactionFragment();
    private final ManageFragment manageFragment = new ManageFragment();
    private final InformationFragment informationFragment = new InformationFragment();
    private final AddTransactionFragment addTransactionFragment = new AddTransactionFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
        BottomNavigationView bottomNavigationView = activityMainBinding.bottomNavigationView;
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.itemHome) {
                replaceFragment(homeFragment);
                return true;
            } else if (item.getItemId() == R.id.itemTransaction) {
                replaceFragment(transactionFragment);
                return true;
            } else if (item.getItemId() == R.id.itemStatistic) {
                replaceFragment(manageFragment);
                return true;
            } else if (item.getItemId() == R.id.itemAccount) {
                replaceFragment(informationFragment);
                return true;
            }
            return false;
        });

        FloatingActionButton addButton = activityMainBinding.floatingActionButton;
        addButton.setOnClickListener(view -> replaceFragment(addTransactionFragment));
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}