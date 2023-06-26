package com.example.smartwallet.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.smartwallet.R;
import com.example.smartwallet.databinding.ActivityAccountBinding;
import com.example.smartwallet.utils.SessionManager;
import com.example.smartwallet.view.fragment.HomeFragment;
import com.example.smartwallet.view.fragment.WelcomeFragment;

public class AccountActivity extends AppCompatActivity {
    private final WelcomeFragment welcomeFragment = new WelcomeFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAccountBinding activityMainBinding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        SessionManager sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, welcomeFragment).commit();
        }
    }
}