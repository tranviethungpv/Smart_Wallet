package com.example.smartwallet.view.fragment.transaction;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.smartwallet.GlobalFunction;
import com.example.smartwallet.R;
import com.example.smartwallet.databinding.FragmentAddTransactionBinding;
import com.example.smartwallet.model.Category;
import com.example.smartwallet.model.Transaction;
import com.example.smartwallet.model.Wallet;
import com.example.smartwallet.utils.SessionManager;
import com.example.smartwallet.viewmodel.CategoryViewModel;
import com.example.smartwallet.viewmodel.TransactionViewModel;
import com.example.smartwallet.viewmodel.WalletViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddTransactionFragment extends Fragment {
    private FragmentAddTransactionBinding fragmentAddTransactionBinding;
    private TextInputEditText dateTimeEditText;
    private EditText inputMoney;
    private EditText inputDetail;
    private TransactionViewModel transactionViewModel;
    private CategoryViewModel categoryViewModel;
    private WalletViewModel walletViewModel;
    private Spinner spinnerCategory;
    private Spinner spinnerWallet;
    private String selectedWalletId;
    private String selectedCategoryId;
    private Boolean selectedRadio;
    private SessionManager sessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        walletViewModel = new ViewModelProvider(this).get(WalletViewModel.class);
        sessionManager = new SessionManager(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentAddTransactionBinding = FragmentAddTransactionBinding.inflate(inflater, container, false);


        dateTimeEditText = fragmentAddTransactionBinding.dateEditText;
        inputMoney = fragmentAddTransactionBinding.editTextNumberDecimal;
        inputDetail = fragmentAddTransactionBinding.editTextInputDetail;
        Button addButton = fragmentAddTransactionBinding.buttonAdd;
        Button cancelButton = fragmentAddTransactionBinding.buttonCancel;

        RadioGroup radioInOut = fragmentAddTransactionBinding.layoutRadio;
        selectedRadio = true;
        radioInOut.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonIncome) {
                selectedRadio = false;
            } else if (checkedId == R.id.radioButtonOutcome) {
                selectedRadio = true;
            }
        });

        String formattedDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            formattedDateTime = currentDateTime.format(formatter);
        }
        dateTimeEditText.setText(formattedDateTime);
        dateTimeEditText.setOnClickListener(v -> showDateTimePickerDialog());

        fillCategoriesSpinner();

        fillWalletsSpinner();

        addButton.setOnClickListener(v -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Timestamp timestamp = GlobalFunction.convertLocalDateTimeToTimestamp(Objects.requireNonNull(dateTimeEditText.getText()).toString().trim());
                if (checkInput()) {
                    Transaction transaction = new Transaction(sessionManager.getUsername(), selectedWalletId, selectedCategoryId, Float.parseFloat(inputMoney.getText().toString().trim()), inputDetail.getText().toString().trim(), selectedRadio, timestamp);
                    transactionViewModel.addTransaction(transaction).observe(getViewLifecycleOwner(), result -> {
                        updateWalletBalance(selectedWalletId, Float.parseFloat(inputMoney.getText().toString().trim()));
                        if (result) {
                            Toast.makeText(requireContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                            clearInput();
                        } else {
                            Toast.makeText(requireContext(), "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        cancelButton.setOnClickListener(v -> {
            clearInput();
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return fragmentAddTransactionBinding.getRoot();
    }

    private void showDateTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year1, month1, dayOfMonth) -> {
            @SuppressLint("DefaultLocale") String selectedDate = year1 + "-" + String.format("%02d", month1 + 1) + "-" + String.format("%02d", dayOfMonth) + "T";
            showTimePickerDialog(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePickerDialog(final String selectedDate) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minute1) -> {
            @SuppressLint("DefaultLocale") String selectedTime = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute1);
            String selectedDateTime = selectedDate + selectedTime;
            dateTimeEditText.setText(selectedDateTime);
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void fillCategoriesSpinner() {
        spinnerCategory = fragmentAddTransactionBinding.spinnerCategory;
        categoryViewModel.getAllCategories(sessionManager.getUsername()).observe(getViewLifecycleOwner(), categories -> {
            List<String> categoryNames = new ArrayList<>();
            Map<String, String> categoryIds = new HashMap<>();
            for (Category category : categories) {
                categoryNames.add(category.getName());
                categoryIds.put(category.getName(), category.getId());
            }
            ArrayAdapter<String> spinnerAdapterCategory = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryNames);
            spinnerAdapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(spinnerAdapterCategory);

            spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedCategoryName = categoryNames.get(position);
                    selectedCategoryId = categoryIds.get(selectedCategoryName);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        });
    }

    private void fillWalletsSpinner() {
        spinnerWallet = fragmentAddTransactionBinding.spinnerWallet;
        walletViewModel.getAllWallets(sessionManager.getUsername()).observe(getViewLifecycleOwner(), wallets -> {
            List<String> walletNames = new ArrayList<>();
            Map<String, String> walletIds = new HashMap<>();
            for (Wallet wallet : wallets) {
                if (Objects.equals(wallet.getUserId(), new SessionManager(requireContext()).getUsername())) {
                    walletNames.add(wallet.getName());
                    walletIds.put(wallet.getName(), wallet.getId());
                }
            }
            ArrayAdapter<String> spinnerAdapterWallet = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, walletNames);
            spinnerAdapterWallet.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerWallet.setAdapter(spinnerAdapterWallet);

            spinnerWallet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedWalletName = walletNames.get(position);
                    selectedWalletId = walletIds.get(selectedWalletName);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        });
    }

    private Boolean checkInput() {
        String money = inputMoney.getText().toString().trim();
        String detail = inputDetail.getText().toString().trim();
        String date = Objects.requireNonNull(dateTimeEditText.getText()).toString().trim();

        if (TextUtils.isEmpty(money)) {
            inputMoney.setError("Hãy nhập số tiền");
            inputMoney.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(detail)) {
            inputDetail.setError("Hãy nhập chi tiết");
            inputDetail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(date)) {
            dateTimeEditText.setError("Hãy nhập ngày tháng");
            dateTimeEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void clearInput() {
        inputMoney.setText("");
        inputDetail.setText("");
        String formattedDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            formattedDateTime = currentDateTime.format(formatter);
        }
        dateTimeEditText.setText(formattedDateTime);
    }

    private void updateWalletBalance(String walletId, Float price) {
        walletViewModel.getAllWallets(sessionManager.getUsername()).observe(getViewLifecycleOwner(), listWallets -> {
            Wallet updatedWallet;
            for (Wallet wallet : listWallets) {
                if (Objects.equals(wallet.getId(), walletId)) {
                    if (selectedRadio) {
                        updatedWallet = new Wallet(wallet.getId(), wallet.getBalance() - price, wallet.getName(), wallet.getUserId());
                    } else {
                        updatedWallet = new Wallet(wallet.getId(), wallet.getBalance() + price, wallet.getName(), wallet.getUserId());
                    }
                    walletViewModel.updateWallet(updatedWallet).observe(getViewLifecycleOwner(), result -> {
                        if (result) {
                            Log.d("Log", "Updated complete");
                        } else {
                            Log.d("Log", "Update failed");
                        }
                    });
                }
            }

        });
    }
}