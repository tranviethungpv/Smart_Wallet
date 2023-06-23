package com.example.smartwallet.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.smartwallet.R;
import com.example.smartwallet.databinding.ActivityMainBinding;
import com.example.smartwallet.view.fragment.HomeFragment;
import com.example.smartwallet.view.fragment.InformationFragment;
import com.example.smartwallet.view.fragment.StatisticFragment;
import com.example.smartwallet.view.fragment.TransactionFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private final HomeFragment homeFragment = new HomeFragment();
    private final TransactionFragment transactionFragment = new TransactionFragment();
    private final StatisticFragment statisticFragment = new StatisticFragment();
    private final InformationFragment informationFragment = new InformationFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
        BottomNavigationView bottomNavigationView = activityMainBinding.bottomNavigationView;
        bottomNavigationView.setOnItemSelectedListener((NavigationBarView.OnItemSelectedListener) item -> {
            if (item.getItemId() == R.id.itemHome) {
                replaceFragment(homeFragment);
                return true;
            }
            else if (item.getItemId() == R.id.itemTransaction) {
                replaceFragment(transactionFragment);
                return true;
            }
            else if (item.getItemId() == R.id.itemStatistic) {
                replaceFragment(statisticFragment);
                return true;
            }
            else if (item.getItemId() == R.id.itemAccount) {
                replaceFragment(informationFragment);
                return true;
            }
            return false;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}