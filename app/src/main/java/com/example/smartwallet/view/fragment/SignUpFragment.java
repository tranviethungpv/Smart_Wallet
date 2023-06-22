package com.example.smartwallet.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartwallet.R;
import com.example.smartwallet.databinding.FragmentSignUpBinding;
import com.example.smartwallet.viewmodel.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class SignUpFragment extends Fragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private UserViewModel userViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentSignUpBinding fragmentSignUpBinding = FragmentSignUpBinding.inflate(inflater, container, false);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        emailEditText = fragmentSignUpBinding.editTextUsername;
        passwordEditText = fragmentSignUpBinding.editTextPassword;
        TextView textViewSignIn = fragmentSignUpBinding.textViewSignUp;
        Button buttonSignUp = fragmentSignUpBinding.buttonLogin;

        textViewSignIn.setOnClickListener(view -> replaceFragment(new LoginFragment()));
        buttonSignUp.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                signUp(email, password);
            } else {
                Toast.makeText(requireContext(), "Hãy nhập đủ email và mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });

        return fragmentSignUpBinding.getRoot();
    }

    private void signUp(String email, String password) {
        userViewModel.signUp(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireContext(), "Đăng ký thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Xảy ra lỗi khi đăng ký", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}