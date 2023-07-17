package com.example.smartwallet.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.smartwallet.GlobalFunction;
import com.example.smartwallet.R;
import com.example.smartwallet.databinding.FragmentTransactionReportBinding;
import com.example.smartwallet.viewmodel.TransactionViewModel;
import java.util.Calendar;
import java.util.NavigableMap;
import java.util.TreeMap;

public class TransactionReportFragment extends Fragment {
    private FragmentTransactionReportBinding fragmentTransactionReportBinding;
    private TransactionViewModel transactionViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentTransactionReportBinding = FragmentTransactionReportBinding.inflate(inflater, container, false);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        generateDateTime();
        processOverview();

        return fragmentTransactionReportBinding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    private void generateDateTime() {
        Calendar rightNow = Calendar.getInstance();
        int month = rightNow.get(Calendar.MONTH) + 1;
        int year = rightNow.get(Calendar.YEAR);
        TextView monthYear = fragmentTransactionReportBinding.textViewDate;
        monthYear.setText("Tháng " + month + " năm " + year);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    private void processOverview() {
        transactionViewModel.calculateTotalAmountByMonth().observe(getViewLifecycleOwner(), listAmountByMonth -> {
            NavigableMap<String, Double> sortedMap = new TreeMap<>(new GlobalFunction.MonthYearComparator());
            sortedMap.putAll(listAmountByMonth);

            Calendar rightNow = Calendar.getInstance();
            int month = rightNow.get(Calendar.MONTH) + 1;
            int year = rightNow.get(Calendar.YEAR);
            String key = month + "-" + year;

            Double value = sortedMap.get(key);

            TextView textViewPrice = fragmentTransactionReportBinding.textViewPrice;
            TextView textViewTotalPaidValue = fragmentTransactionReportBinding.textViewTotalPaidValue;
            if (value != null) {
                textViewPrice.setText(String.valueOf(value));

                Double previousValue = sortedMap.get(sortedMap.lowerKey(key));

                if (previousValue == null) {
                    previousValue = 0.0;
                }

                int percent = (int)(Math.round((Math.abs(previousValue - value)/previousValue) * 100));
                if (previousValue > value) {
                    textViewTotalPaidValue.setText(" giảm " + percent + "%");
                    textViewTotalPaidValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
                } else if (previousValue < value) {
                    textViewTotalPaidValue.setText(" tăng " + percent + "%");
                    textViewTotalPaidValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_special));
                } else {
                    textViewTotalPaidValue.setText(" bằng tháng trước");
                }
            } else {
                textViewPrice.setText("0");
                value = 0.0;

                Double previousValue = sortedMap.get(sortedMap.lastKey());

                if (previousValue == null) {
                    previousValue = 0.0;
                }

                int percent = (int)(Math.round((Math.abs(previousValue - value)/previousValue) * 100));
                if (previousValue > value) {
                    textViewTotalPaidValue.setText(" giảm " + percent + "%");
                    textViewTotalPaidValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
                } else if (previousValue < value) {
                    textViewTotalPaidValue.setText(" tăng " + percent + "%");
                    textViewTotalPaidValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_special));
                } else {
                    textViewTotalPaidValue.setText(" bằng tháng trước");
                }
            }
        });
    }
}