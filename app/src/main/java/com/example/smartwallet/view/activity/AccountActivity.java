package com.example.smartwallet.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.smartwallet.R;
import com.example.smartwallet.databinding.ActivityAccountBinding;
import com.example.smartwallet.view.fragment.WelcomeFragment;

public class AccountActivity extends AppCompatActivity {
    private final WelcomeFragment welcomeFragment = new WelcomeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAccountBinding activityMainBinding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        getSupportFragmentManager().beginTransaction().replace(R.id.container, welcomeFragment).commit();
    }
}