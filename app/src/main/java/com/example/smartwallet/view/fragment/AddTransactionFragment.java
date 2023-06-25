package com.example.smartwallet.view.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartwallet.databinding.FragmentAddTransactionBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class AddTransactionFragment extends Fragment {
    private TextInputEditText dateTimeEditText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentAddTransactionBinding fragmentAddTransactionBinding = FragmentAddTransactionBinding.inflate(inflater, container, false);
        dateTimeEditText = fragmentAddTransactionBinding.dateEditText;
        dateTimeEditText.setOnClickListener(v -> showDateTimePickerDialog());
        return fragmentAddTransactionBinding.getRoot();
    }

    private void showDateTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year1, month1, dayOfMonth) -> {
            String selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth + "T";
            showTimePickerDialog(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePickerDialog(final String selectedDate) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minute1) -> {
            String selectedTime = hourOfDay + ":" + minute1;
            String selectedDateTime = selectedDate + selectedTime;
            dateTimeEditText.setText(selectedDateTime);
        }, hour, minute, true);

        timePickerDialog.show();
    }
}