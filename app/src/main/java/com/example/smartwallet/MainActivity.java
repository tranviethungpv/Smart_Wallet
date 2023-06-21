package com.example.smartwallet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.smartwallet.databinding.ActivityMainBinding;
import com.example.smartwallet.view.fragment.WelcomeFragment;

public class MainActivity extends AppCompatActivity {
    private final WelcomeFragment welcomeFragment = new WelcomeFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        getSupportFragmentManager().beginTransaction().replace(R.id.container, welcomeFragment).commit();
    }
}