package com.example.smartwallet.view.fragment.overview;

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
import com.example.smartwallet.utils.SessionManager;
import com.example.smartwallet.viewmodel.TransactionViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class TransactionReportFragment extends Fragment {
    private FragmentTransactionReportBinding fragmentTransactionReportBinding;
    private TransactionViewModel transactionViewModel;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentTransactionReportBinding = FragmentTransactionReportBinding.inflate(inflater, container, false);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        sessionManager = new SessionManager(requireContext());

        generateDateTime();
        processOverview();
        generateChart();

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
        transactionViewModel.calculateTotalAmountByMonth(sessionManager.getUsername(), true).observe(getViewLifecycleOwner(), listAmountByMonth -> {
            Map<String, Double> fullExpenditureData = GlobalFunction.generateBlankValue(listAmountByMonth);

            NavigableMap<String, Double> sortedMap = new TreeMap<>(new GlobalFunction.MonthYearComparator());
            sortedMap.putAll(fullExpenditureData);

            Calendar rightNow = Calendar.getInstance();
            int month = rightNow.get(Calendar.MONTH) + 1;
            int year = rightNow.get(Calendar.YEAR);
            @SuppressLint("DefaultLocale") String key = String.format("%02d-%04d", month, year);

            Double value = sortedMap.get(key);

            TextView textViewPrice = fragmentTransactionReportBinding.textViewPrice;
            TextView textViewTotalPaidValue = fragmentTransactionReportBinding.textViewTotalPaidValue;
            TextView textViewDataStatus = fragmentTransactionReportBinding.textViewDataStatus;

            if (listAmountByMonth.size() == 0) {
                textViewTotalPaidValue.setVisibility(View.GONE);
                textViewDataStatus.setVisibility(View.VISIBLE);
            }

            if (value != null) {
                textViewPrice.setText(String.valueOf(value));
                Double previousValue = sortedMap.get(sortedMap.lowerKey(key));

                if (value == 0) {
                    if (previousValue == null) {
                        previousValue = 0.0;
                    }
                    if (previousValue == 0.0) {
                        textViewTotalPaidValue.setText(" bằng tháng trước");
                    } else if (previousValue > 0) {
                        textViewTotalPaidValue.setText(" giảm 100%");
                        textViewTotalPaidValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
                    }
                } else if (value > 0) {
                    if (previousValue == null) {
                        previousValue = 0.0;
                    }
                    if (previousValue == 0.0) {
                        textViewTotalPaidValue.setText(" tăng 100%");
                        textViewTotalPaidValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_special));
                    } else if (previousValue > 0) {
                        int percent = (int) (Math.round((Math.abs(previousValue - value) / previousValue) * 100));
                        if (value > previousValue) {
                            textViewTotalPaidValue.setText(" tăng " + percent + "%");
                            textViewTotalPaidValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_special));
                        } else if (value < previousValue) {
                            textViewTotalPaidValue.setText(" giảm " + percent + "%");
                            textViewTotalPaidValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
                        } else {
                            textViewTotalPaidValue.setText(" bằng tháng trước");
                        }
                    }
                }
            }
        });
    }

    private void generateChart() {
        // Get the HorizontalBarChart view from the layout
        BarChart barChart = fragmentTransactionReportBinding.chartReportByMonth;

        // Create a list to hold the bar entries
        List<BarEntry> barEntries = new ArrayList<>();

        transactionViewModel.calculateTotalAmountByMonth(sessionManager.getUsername(), true).observe(getViewLifecycleOwner(), listAmountByMonth -> {
            Map<String, Double> fullExpenditureData = GlobalFunction.generateBlankValue(listAmountByMonth);

            //Sort totalExpenditureMap
            NavigableMap<String, Double> sortedTotalExpenditureMap = new TreeMap<>(new GlobalFunction.MonthYearComparator());
            sortedTotalExpenditureMap.putAll(fullExpenditureData);

            Calendar rightNow = Calendar.getInstance();
            int month = rightNow.get(Calendar.MONTH) + 1;
            int year = rightNow.get(Calendar.YEAR);
            @SuppressLint("DefaultLocale") String key = String.format("%02d-%04d", month, year);


            String prevKey = sortedTotalExpenditureMap.lowerKey(key);
            // Add data to the bar entries list
            int index = 0;
            for (Map.Entry<String, Double> entry : sortedTotalExpenditureMap.entrySet()) {
                String monthYear = entry.getKey();
                float expenditure = entry.getValue().floatValue();

                if (monthYear.equals(key) || monthYear.equals(prevKey)) {
                    barEntries.add(new BarEntry(index++, expenditure));
                }
            }

            // Create a dataset from the bar entries
            BarDataSet dataSet = new BarDataSet(barEntries, "Tổng đã chi");
            dataSet.setColor(ContextCompat.getColor(requireContext(), R.color.green_special)); // Set the color of the bars

            // Create the bar data object with the datasets
            BarData barData = new BarData(dataSet);
            barData.setBarWidth(0.3f); // Set the width of the bars


            ArrayList<String> labels = new ArrayList<>();
            labels.add("Tháng trước");
            labels.add("Tháng này");

            // Customize the x-axis labels
            barChart.getXAxis().setEnabled(true);
            barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            barChart.getXAxis().setLabelCount(2);
            barChart.getXAxis().setDrawGridLines(false);
            // Setup XAxis only shows 2 values in labels
            barChart.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    int index = (int) value;
                    if (index == 0) {
                        return labels.get(0);
                    } else if (index == 1) {
                        return labels.get(1);
                    }
                    return "";
                }
            });

            float maxValue = Float.MIN_VALUE;
            for (BarEntry barEntry : barEntries) {
                float value = barEntry.getY();
                if (value > maxValue) {
                    maxValue = value;
                }
            }

            // Customize the y-axis
            barChart.getAxisLeft().setEnabled(true);
            barChart.getAxisLeft().setDrawGridLines(false);
            barChart.getAxisRight().setEnabled(false);
            barChart.getAxisLeft().setAxisMinimum(0);
            barChart.getAxisLeft().setAxisMaximum(maxValue);


            barChart.getDescription().setEnabled(false);

            // Set the bar chart data and refresh the chart
            barChart.setData(barData);
            barChart.invalidate();
        });
    }
}