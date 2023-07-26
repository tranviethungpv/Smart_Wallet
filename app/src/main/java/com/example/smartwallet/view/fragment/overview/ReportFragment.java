package com.example.smartwallet.view.fragment.overview;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.smartwallet.GlobalFunction;
import com.example.smartwallet.R;
import com.example.smartwallet.databinding.FragmentReportBinding;
import com.example.smartwallet.model.Category;
import com.example.smartwallet.model.Transaction;
import com.example.smartwallet.utils.SessionManager;
import com.example.smartwallet.viewmodel.CategoryViewModel;
import com.example.smartwallet.viewmodel.TransactionViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

public class ReportFragment extends Fragment {
    private FragmentReportBinding fragmentReportBinding;
    private Spinner monthPickerSpinner;
    private Spinner typePickerSpinner;
    private PieChart chart;
    private TransactionViewModel transactionViewModel;
    private CategoryViewModel categoryViewModel;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentReportBinding = FragmentReportBinding.inflate(inflater, container, false);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        sessionManager = new SessionManager(requireContext());

        monthPickerSpinner = fragmentReportBinding.spinnerMonth;
        typePickerSpinner = fragmentReportBinding.spinnerType;
        chart = fragmentReportBinding.pieChart;

        fillMonthSpinner();
        fillTypeSpinner();

        initListener();

        return fragmentReportBinding.getRoot();
    }

    private void fillMonthSpinner() {
        transactionViewModel.calculateTotalAllAmountByMonth(sessionManager.getUsername()).observe(getViewLifecycleOwner(), listAmountByMonth -> {
            NavigableMap<String, Double> sortedMap = new TreeMap<>(new GlobalFunction.MonthYearComparator());
            sortedMap.putAll(listAmountByMonth);

            ArrayList<String> listMonth = new ArrayList<>();
            for (Map.Entry<String, Double> ignored : sortedMap.entrySet()) {
                listMonth.add(ignored.getKey());
            }

            ArrayAdapter<String> spinnerMonthAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, listMonth);
            spinnerMonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            monthPickerSpinner.setAdapter(spinnerMonthAdapter);
            monthPickerSpinner.setSelection(listMonth.size() - 1);
        });
    }

    private void fillTypeSpinner() {
        Map<String, Boolean> listType = new HashMap<>();
        listType.put("Khoản thu", false);
        listType.put("Khoản chi", true);

        ArrayList<String> listName = new ArrayList<>();
        for (Map.Entry<String, Boolean> ignored : listType.entrySet()) {
            listName.add(ignored.getKey());
        }

        ArrayAdapter<String> spinnerTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, listName);
        spinnerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typePickerSpinner.setAdapter(spinnerTypeAdapter);
        typePickerSpinner.setSelection(listName.size() - 1);
    }

    private void initListener() {
        monthPickerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedMonthYear = adapterView.getItemAtPosition(i).toString();
                handleBothSpinnerSelections(selectedMonthYear, getSelectedTypeSpinner());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        typePickerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedName = adapterView.getItemAtPosition(i).toString();
                handleBothSpinnerSelections(getSelectedMonthSpinner(), selectedName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void drawChartOutcome(String monthYear) {
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        categoryViewModel.getAllCategories(sessionManager.getUsername()).observe(getViewLifecycleOwner(), listCategories -> transactionViewModel.getAllTransactions(sessionManager.getUsername()).observe(getViewLifecycleOwner(), listTransactions -> {
            ArrayList<CategoryAmountByMonth> categoryProportionsIncome = getCategoryAmountByMonth(listTransactions, listCategories, false);
            ArrayList<CategoryAmountByMonth> categoryProportionsOutcome = getCategoryAmountByMonth(listTransactions, listCategories, true);
            GlobalFunction.synchronizeCategoryLists(categoryProportionsOutcome, categoryProportionsIncome);

            //Draw Pie Chart
            ArrayList<PieEntry> listEntry = new ArrayList<>();
            for (CategoryAmountByMonth item : categoryProportionsOutcome) {
                if (Objects.equals(item.getMonth(), monthYear)) {
                    listEntry.add(new PieEntry((float) item.getTotalAmount(), item.getCategoryName()));
                }
            }
            PieDataSet pieDataSet = new PieDataSet(listEntry, "Outcome Pie Chart");
            PieData pieData = new PieData(pieDataSet);

            pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            pieDataSet.setValueTextSize(12f);
            pieDataSet.setValueTextColor(getResources().getColor(R.color.white));

            chart.getDescription().setEnabled(false);
            chart.setData(pieData);
            chart.invalidate();
        }));
    }

    private void drawChartIncome(String monthYear) {
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        categoryViewModel.getAllCategories(sessionManager.getUsername()).observe(getViewLifecycleOwner(), listCategories -> transactionViewModel.getAllTransactions(sessionManager.getUsername()).observe(getViewLifecycleOwner(), listTransactions -> {
            ArrayList<CategoryAmountByMonth> categoryProportionsIncome = getCategoryAmountByMonth(listTransactions, listCategories, false);
            ArrayList<CategoryAmountByMonth> categoryProportionsOutcome = getCategoryAmountByMonth(listTransactions, listCategories, true);
            GlobalFunction.synchronizeCategoryLists(categoryProportionsOutcome, categoryProportionsIncome);

            //Draw Pie Chart
            ArrayList<PieEntry> listEntry = new ArrayList<>();
            for (CategoryAmountByMonth item : categoryProportionsIncome) {
                if (Objects.equals(item.getMonth(), monthYear)) {
                    listEntry.add(new PieEntry((float) item.getTotalAmount(), item.getCategoryName()));
                }
            }
            PieDataSet pieDataSet = new PieDataSet(listEntry, "Income Pie Chart");
            PieData pieData = new PieData(pieDataSet);

            pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            pieDataSet.setValueTextSize(12f);
            pieDataSet.setValueTextColor(getResources().getColor(R.color.white));

            chart.getDescription().setEnabled(false);
            chart.setData(pieData);
            chart.invalidate();
        }));
    }

    public ArrayList<CategoryAmountByMonth> getCategoryAmountByMonth(ArrayList<Transaction> transactions, ArrayList<Category> categories, Boolean type) {
        ArrayList<CategoryAmountByMonth> result = new ArrayList<>();

        // Group transactions by month and category
        Map<String, Map<String, Double>> monthCategoryMap = new HashMap<>();
        for (Transaction transaction : transactions) {
            if (transaction.getType() == type) {
                Date transactionDate = ((Timestamp) transaction.getDate()).toDate();

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(transactionDate);
                int month = calendar.get(Calendar.MONTH) + 1; // Adding 1 to get the month in 1-based index
                int year = calendar.get(Calendar.YEAR);
                @SuppressLint("DefaultLocale") String monthYear = String.format("%02d-%04d", month, year);


                String categoryId = transaction.getCategoryId();
                double amount = transaction.getAmount();

                if (!monthCategoryMap.containsKey(monthYear)) {
                    monthCategoryMap.put(monthYear, new HashMap<>());
                }

                Map<String, Double> categoryAmountMap = monthCategoryMap.computeIfAbsent(monthYear, k -> new HashMap<>());

                if (!categoryAmountMap.containsKey(categoryId)) {
                    categoryAmountMap.put(categoryId, 0.0);
                }

                double totalAmount = categoryAmountMap.get(categoryId);
                totalAmount += amount;
                categoryAmountMap.put(categoryId, totalAmount);
            }
        }

        // Convert the result to the CategoryAmountByMonth object
        for (Map.Entry<String, Map<String, Double>> entry : monthCategoryMap.entrySet()) {
            String month = entry.getKey();
            Map<String, Double> categoryAmountMap = entry.getValue();

            for (Map.Entry<String, Double> categoryEntry : categoryAmountMap.entrySet()) {
                String categoryId = categoryEntry.getKey();
                double totalAmount = categoryEntry.getValue();

                String categoryName = getCategoryNameById(categories, categoryId);
                CategoryAmountByMonth categoryAmount = new CategoryAmountByMonth(month, categoryId, categoryName, totalAmount);
                result.add(categoryAmount);
            }
        }

        return result;
    }

    private String getCategoryNameById(ArrayList<Category> categories, String categoryId) {
        for (Category category : categories) {
            if (category.getId().equals(categoryId)) {
                return category.getName();
            }
        }
        return "";
    }

    @SuppressLint("SetTextI18n")
    private void showAmountByMonth(String monthYear, Boolean type) {
        if (type) {
            transactionViewModel.calculateTotalAmountByMonth(sessionManager.getUsername(), true).observe(getViewLifecycleOwner(), newListAmountOutcomeByMonth -> {
                transactionViewModel.calculateTotalAmountByMonth(sessionManager.getUsername(), false).observe(getViewLifecycleOwner(), newListAmountIncomeByMonth -> {
                    GlobalFunction.synchronizeIncomeAndOutcomeMaps(newListAmountOutcomeByMonth, newListAmountIncomeByMonth);
                    fragmentReportBinding.textViewAmount.setText(Objects.requireNonNull(newListAmountOutcomeByMonth.get(monthYear)) + " đ");
                    fragmentReportBinding.textViewAmount.setTextColor(getResources().getColor(R.color.red));
                });
            });
        } else {
            transactionViewModel.calculateTotalAmountByMonth(sessionManager.getUsername(), true).observe(getViewLifecycleOwner(), newListAmountOutcomeByMonth -> {
                transactionViewModel.calculateTotalAmountByMonth(sessionManager.getUsername(), false).observe(getViewLifecycleOwner(), newListAmountIncomeByMonth -> {
                    GlobalFunction.synchronizeIncomeAndOutcomeMaps(newListAmountOutcomeByMonth, newListAmountIncomeByMonth);
                    fragmentReportBinding.textViewAmount.setText(Objects.requireNonNull(newListAmountIncomeByMonth.get(monthYear)) + " đ");
                    fragmentReportBinding.textViewAmount.setTextColor(getResources().getColor(R.color.blue));
                });
            });
        }
    }

    public static class CategoryAmountByMonth {
        private String month;
        private String categoryId;
        private String categoryName;
        private double totalAmount;

        public CategoryAmountByMonth(String month, String categoryId, String categoryName, double totalAmount) {
            this.month = month;
            this.categoryId = categoryId;
            this.categoryName = categoryName;
            this.totalAmount = totalAmount;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }
    }

    private String getSelectedMonthSpinner() {
        Object selectedItem = monthPickerSpinner.getSelectedItem();
        return selectedItem != null ? selectedItem.toString() : "";
    }

    private String getSelectedTypeSpinner() {
        return typePickerSpinner.getSelectedItem().toString();
    }

    private void handleBothSpinnerSelections(String selectedMonth, String selectedName) {
        if (selectedMonth.isEmpty()) {
            return;
        }
        if (selectedName.equals("Khoản chi")) {
            drawChartOutcome(selectedMonth);
            showAmountByMonth(selectedMonth, true);
        } else if (selectedName.equals("Khoản thu")) {
            drawChartIncome(selectedMonth);
            showAmountByMonth(selectedMonth, false);
        }
    }
}