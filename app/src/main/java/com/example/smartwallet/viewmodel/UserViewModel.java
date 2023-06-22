package com.example.smartwallet.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartwallet.model.User;
import com.example.smartwallet.repository.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class UserViewModel extends ViewModel {
    private final UserRepository userRepository;

    public UserViewModel() {
        userRepository = new UserRepository();
    }

    public LiveData<User> loginUser(String email, String password) {
        return userRepository.loginUser(email, password);
    }

    public Task<Void> resetPassword(String email) {
        return userRepository.resetPassword(email);
    }

    public Task<AuthResult> signUp(String email, String password) {
        return userRepository.signUp(email, password);
    }
}
