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
import com.example.smartwallet.databinding.FragmentUpdateTransactionBinding;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UpdateTransactionFragment extends Fragment {
    private FragmentUpdateTransactionBinding fragmentUpdateTransactionBinding;
    private TransactionViewModel transactionViewModel;
    private WalletViewModel walletViewModel;
    private CategoryViewModel categoryViewModel;
    private Spinner spinnerCategory;
    private Spinner spinnerWallet;
    private TextInputEditText dateTimeEditText;
    private EditText inputMoney;
    private EditText inputDetail;
    private Boolean selectedRadio;
    private Transaction selectedTransaction;
    private String selectedWalletId;
    private String selectedCategoryId;
    private SessionManager sessionManager;


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        walletViewModel = new ViewModelProvider(this).get(WalletViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        sessionManager = new SessionManager(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentUpdateTransactionBinding = FragmentUpdateTransactionBinding.inflate(inflater, container, false);

        dateTimeEditText = fragmentUpdateTransactionBinding.dateEditText;
        inputMoney = fragmentUpdateTransactionBinding.editTextNumberDecimal;
        inputDetail = fragmentUpdateTransactionBinding.editTextInputDetail;
        Button buttonUpdate = fragmentUpdateTransactionBinding.buttonAdd;
        Button buttonCancel = fragmentUpdateTransactionBinding.buttonCancel;
        RadioGroup radioInOut = fragmentUpdateTransactionBinding.layoutRadio;
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

        getData();
        fillData();

        buttonUpdate.setOnClickListener(item -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Timestamp timestamp = GlobalFunction.convertLocalDateTimeToTimestamp(Objects.requireNonNull(dateTimeEditText.getText()).toString().trim());
                if (checkInput()) {

                    Transaction transaction = new Transaction(selectedTransaction.getId(), sessionManager.getUsername(), selectedWalletId, selectedCategoryId, Float.parseFloat(inputMoney.getText().toString().trim()), inputDetail.getText().toString().trim(), selectedRadio, timestamp);
                    transactionViewModel.updateTransaction(transaction).observe(getViewLifecycleOwner(), result -> {
                        updateWalletBalance(selectedTransaction, transaction);
                        if (result) {
                            Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        buttonCancel.setOnClickListener(item -> getParentFragmentManager().popBackStack());

        return fragmentUpdateTransactionBinding.getRoot();
    }

    private void getData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String id = bundle.getString("id");
            String userId = bundle.getString("userId");
            String detail = bundle.getString("detail");
            String categoryId = bundle.getString("categoryId");
            String walletId = bundle.getString("walletId");
            float amount = bundle.getFloat("amount");
            boolean type = bundle.getBoolean("type");
            Date date = (Date) bundle.getSerializable("date");
            Timestamp timestamp = new Timestamp(date);
            selectedTransaction = new Transaction(id, userId, walletId, categoryId, amount, detail, type, timestamp);
        }
    }

    @SuppressLint("SetTextI18n")
    public void fillData() {
        fragmentUpdateTransactionBinding.editTextInputDetail.setText(selectedTransaction.getDetail());
        fragmentUpdateTransactionBinding.editTextNumberDecimal.setText(selectedTransaction.getAmount().toString());
        if (!selectedTransaction.getType()) {
            fragmentUpdateTransactionBinding.radioButtonIncome.setChecked(true);
            fragmentUpdateTransactionBinding.radioButtonOutcome.setChecked(false);
        } else {
            fragmentUpdateTransactionBinding.radioButtonIncome.setChecked(false);
            fragmentUpdateTransactionBinding.radioButtonOutcome.setChecked(true);
        }
        fragmentUpdateTransactionBinding.dateEditText.setText(GlobalFunction.convertTimestampToFormattedString(selectedTransaction.getDate()));
        fillCategoriesSpinner();
        fillWalletsSpinner();
    }

    private void fillCategoriesSpinner() {
        spinnerCategory = fragmentUpdateTransactionBinding.spinnerCategory;
        categoryViewModel.getAllCategories(sessionManager.getUsername()).observe(getViewLifecycleOwner(), categories -> {
            List<String> categoryNames = new ArrayList<>();
            Map<String, String> categoryIds = new HashMap<>();
            int selectedPosition = 0; // Track the position of the selected category

            for (int i = 0; i < categories.size(); i++) {
                Category category = categories.get(i);
                categoryNames.add(category.getName());
                categoryIds.put(category.getName(), category.getId());

                // Check if the category name matches the selectedTransaction's category name
                if (category.getId().equals(selectedTransaction.getCategoryId())) {
                    selectedPosition = i; // Set the selected position
                }
            }

            ArrayAdapter<String> spinnerAdapterCategory = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryNames);
            spinnerAdapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(spinnerAdapterCategory);

            spinnerCategory.setSelection(selectedPosition); // Set the selected position

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
        spinnerWallet = fragmentUpdateTransactionBinding.spinnerWallet;
        walletViewModel.getAllWallets(sessionManager.getUsername()).observe(getViewLifecycleOwner(), wallets -> {
            List<String> walletNames = new ArrayList<>();
            Map<String, String> walletIds = new HashMap<>();
            int selectedPosition = 0; // Track the position of the selected wallet

            for (int i = 0; i < wallets.size(); i++) {
                Wallet wallet = wallets.get(i);
                if (Objects.equals(wallet.getUserId(), new SessionManager(requireContext()).getUsername())) {
                    walletNames.add(wallet.getName());
                    walletIds.put(wallet.getName(), wallet.getId());

                    // Check if the wallet name matches the selectedTransaction's wallet name
                    if (wallet.getId().equals(selectedTransaction.getWalletId())) {
                        selectedPosition = i; // Set the selected position
                    }
                }
            }

            ArrayAdapter<String> spinnerAdapterWallet = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, walletNames);
            spinnerAdapterWallet.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerWallet.setAdapter(spinnerAdapterWallet);

            spinnerWallet.setSelection(selectedPosition); // Set the selected position

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

    private void updateWalletBalance(Transaction unchangedTransaction, Transaction changedTransaction) {
        walletViewModel.getAllWallets(sessionManager.getUsername()).observe(getViewLifecycleOwner(), listWallets -> {
            Wallet updatedWallet;
            for (Wallet wallet : listWallets) {
                if (Objects.equals(wallet.getId(), changedTransaction.getWalletId())) {
                    Float difference = Math.abs(unchangedTransaction.getAmount() - changedTransaction.getAmount());
                    if (changedTransaction.getType()) {
                        if (changedTransaction.getAmount() < unchangedTransaction.getAmount()) {
                            updatedWallet = new Wallet(wallet.getId(), wallet.getBalance() + difference, wallet.getName(), wallet.getUserId());
                        } else if (changedTransaction.getAmount() > unchangedTransaction.getAmount()) {
                            updatedWallet = new Wallet(wallet.getId(), wallet.getBalance() - difference, wallet.getName(), wallet.getUserId());
                        } else {
                            updatedWallet = new Wallet(wallet.getId(), wallet.getBalance(), wallet.getName(), wallet.getUserId());
                        }
                    } else {
                        if (changedTransaction.getAmount() < unchangedTransaction.getAmount()) {
                            updatedWallet = new Wallet(wallet.getId(), wallet.getBalance() - difference, wallet.getName(), wallet.getUserId());
                        } else if (changedTransaction.getAmount() > unchangedTransaction.getAmount()) {
                            updatedWallet = new Wallet(wallet.getId(), wallet.getBalance() + difference, wallet.getName(), wallet.getUserId());
                        } else {
                            updatedWallet = new Wallet(wallet.getId(), wallet.getBalance(), wallet.getName(), wallet.getUserId());
                        }
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