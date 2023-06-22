package com.example.smartwallet.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartwallet.R;
import com.example.smartwallet.adapter.BannerPagerAdapter;
import com.example.smartwallet.databinding.FragmentWelcomeBinding;

import java.util.Arrays;
import java.util.List;

public class WelcomeFragment extends Fragment {
    private final List<Integer> layoutIds = Arrays.asList(R.layout.banner_item_1, R.layout.banner_item_2, R.layout.banner_item_3);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentWelcomeBinding fragmentWelcomeBinding = FragmentWelcomeBinding.inflate(inflater, container, false);
        ViewPager2 viewPager = fragmentWelcomeBinding.viewPager2Banner;
        BannerPagerAdapter adapter = new BannerPagerAdapter(layoutIds);
        viewPager.setAdapter(adapter);
        fragmentWelcomeBinding.indicator3.setViewPager(viewPager);
        viewPager.setCurrentItem(0);
        fragmentWelcomeBinding.buttonSignIn.setOnClickListener(view -> replaceFragment(new LoginFragment()));
        fragmentWelcomeBinding.buttonSignUp.setOnClickListener(view -> replaceFragment(new SignUpFragment()));
        return fragmentWelcomeBinding.getRoot();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        if (fragment != null) {
            transaction.replace(R.id.container, fragment);
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }
}