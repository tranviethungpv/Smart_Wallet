package com.example.smartwallet.view.fragment.account;

import android.content.Intent;
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
import com.example.smartwallet.databinding.FragmentLoginBinding;
import com.example.smartwallet.utils.SessionManager;
import com.example.smartwallet.view.activity.MainActivity;
import com.example.smartwallet.viewmodel.UserViewModel;

public class LoginFragment extends Fragment {
    private EditText emailEditText;
    private EditText passwordEditText;
    private UserViewModel userViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentLoginBinding fragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        emailEditText = fragmentLoginBinding.editTextUsername;
        passwordEditText = fragmentLoginBinding.editTextPassword;
        Button loginButton = fragmentLoginBinding.buttonLogin;
        TextView signUp = fragmentLoginBinding.textViewSignUp;
        TextView forgotPassword = fragmentLoginBinding.textViewForgotPassword;

        loginButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            userViewModel.loginUser(email, password).observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    Toast.makeText(requireContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                    SessionManager sessionManager = new SessionManager(requireContext());
                    sessionManager.saveCredentials(user.getEmail(), user.getPassword());

                    startActivity(new Intent(getActivity(), MainActivity.class));
                } else {
                    Toast.makeText(requireContext(), "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        });

        signUp.setOnClickListener(view -> replaceFragment(new SignUpFragment()));

        forgotPassword.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();

            if (!email.isEmpty()) {
                resetPassword(email);
            } else {
                Toast.makeText(requireContext(), "Hãy nhập Email", Toast.LENGTH_SHORT).show();
            }
        });

        return fragmentLoginBinding.getRoot();
    }

    private void resetPassword(String email) {
        userViewModel.resetPassword(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireContext(), "Đã gửi Email đặt lại mật khẩu", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Lỗi khi gửi Email đặt lại mật khẩu", Toast.LENGTH_SHORT).show();
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