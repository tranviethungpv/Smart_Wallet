package com.example.smartwallet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.smartwallet.view.fragment.overview.ReportFragment;
import com.google.firebase.Timestamp;

import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class GlobalFunction {
    public static Timestamp convertLocalDateTimeToTimestamp(String input) {
        //Require API >= 26
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //Convert String to LocalDateTime
            DateTimeFormatter formatter;
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(Objects.requireNonNull(input).trim(), formatter);

            // Get the time zone offset of the local time zone
            ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(ZoneId.systemDefault().getRules().getOffset(dateTime).getTotalSeconds());

            // Adjust the LocalDateTime with the time zone offset
            OffsetDateTime offsetDateTime = dateTime.atOffset(zoneOffset);

            // Convert OffsetDateTime to Instant
            Instant instant = offsetDateTime.toInstant();

            // Create a Timestamp from the Instant
            Date date = Date.from(instant);
            return new Timestamp(date);
        } else return null;
    }

    public static String getDayOfMonthFromTimestamp(Timestamp timestamp) {
        // Convert Timestamp to java.util.Date
        Date date = timestamp.toDate();

        // Use Calendar to extract month and year
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Format to desired format
        return String.format(Locale.getDefault(), "%02d", day);
    }

    public static String getMonthYearFromTimestamp(Timestamp timestamp) {
        // Convert Timestamp to java.util.Date
        Date date = timestamp.toDate();

        // Use Calendar to extract month and year
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH) + 1; // Adding 1 to get the month in 1-based index
        int year = calendar.get(Calendar.YEAR);

        // Format the month and year to desired format
        return String.format(Locale.getDefault(), "tháng " + month + " năm " + year);
    }

    public static String getTimeFromTimestamp(Timestamp timestamp) {
        // Convert Timestamp to java.util.Date
        Date date = timestamp.toDate();

        // Use Calendar to extract month and year
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Format the time to desired format
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
    }

    @SuppressLint("DefaultLocale")
    public static String convertTimestampToFormattedString(Timestamp timestamp) {
        // Create a Calendar instance and set its time to the Timestamp value
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp.toDate());

        // Extract the components from the Calendar instance
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;  // Months are zero-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create the formatted string using the extracted components
        return String.format("%04d-%02d-%02dT%02d:%02d", year, month, day, hour, minute);
    }

    public static class MonthYearComparator implements Comparator<String> {
        @Override
        public int compare(String key1, String key2) {
            String[] parts1 = key1.split("-");
            String[] parts2 = key2.split("-");
            int month1 = Integer.parseInt(parts1[0]);
            int year1 = Integer.parseInt(parts1[1]);
            int month2 = Integer.parseInt(parts2[0]);
            int year2 = Integer.parseInt(parts2[1]);

            if (year1 != year2) {
                return Integer.compare(year1, year2);
            } else {
                return Integer.compare(month1, month2);
            }
        }
    }

    public static Map<String, Double> generateBlankValue(Map<String, Double> listData) {
        int smallestYear = Integer.MAX_VALUE;
        int largestYear = Integer.MIN_VALUE;

        Calendar rightNow = Calendar.getInstance();
        int currentYear = rightNow.get(Calendar.YEAR);
        int currentMonth = rightNow.get(Calendar.MONTH) + 1;

        for (String monthYear : listData.keySet()) {
            String[] parts = monthYear.split("-");
            int year = Integer.parseInt(parts[1]);

            if (year < smallestYear) {
                smallestYear = year;
            }
            if (year > largestYear) {
                largestYear = year;
            }
        }
        if (currentYear > largestYear) {
            if (currentMonth == 1) {
                String key = "12-" + (smallestYear - 1);
                listData.put(key, 0.0);
            }
            for (int year = smallestYear; year <= currentYear; year++) {
                for (int month = 1; month <= 12; month++) {
                    String monthYear = String.format(Locale.US, "%02d-%04d", month, year);
                    if (!listData.containsKey(monthYear)) {
                        listData.put(monthYear, 0.0);
                    }
                }
            }
        } else if (currentYear < smallestYear) {
            if (currentMonth == 1) {
                String key = "12-" + (currentYear - 1);
                listData.put(key, 0.0);
            }
            for (int year = currentYear; year <= largestYear; year++) {
                for (int month = 1; month <= 12; month++) {
                    String monthYear = String.format(Locale.US, "%02d-%04d", month, year);
                    if (!listData.containsKey(monthYear)) {
                        listData.put(monthYear, 0.0);
                    }
                }
            }
        } else {
            if (currentMonth == 1) {
                String key = "12-" + (smallestYear - 1);
                listData.put(key, 0.0);
            }
            for (int year = smallestYear; year <= largestYear; year++) {
                for (int month = 1; month <= 12; month++) {
                    String monthYear = String.format(Locale.US, "%02d-%04d", month, year);
                    if (!listData.containsKey(monthYear)) {
                        listData.put(monthYear, 0.0);
                    }
                }
            }
        }
        return listData;
    }

    public static String getTextSearch(String input) {
        String nfdNormalizedString = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View currentFocus = activity.getCurrentFocus();
            if (currentFocus != null) {
                inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public static void synchronizeCategoryLists(ArrayList<ReportFragment.CategoryAmountByMonth> incomeList, ArrayList<ReportFragment.CategoryAmountByMonth> outcomeList) {
        for (ReportFragment.CategoryAmountByMonth incomeItem : incomeList) {
            String incomeMonth = incomeItem.getMonth();
            boolean found = false;

            for (ReportFragment.CategoryAmountByMonth outcomeItem : outcomeList) {
                if (incomeMonth.equals(outcomeItem.getMonth())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                ReportFragment.CategoryAmountByMonth newOutcomeItem = new ReportFragment.CategoryAmountByMonth(incomeMonth, "", "", 0.0);
                outcomeList.add(newOutcomeItem);
            }
        }

        for (ReportFragment.CategoryAmountByMonth outcomeItem : outcomeList) {
            String outcomeMonth = outcomeItem.getMonth();
            boolean found = false;

            for (ReportFragment.CategoryAmountByMonth incomeItem : incomeList) {
                if (outcomeMonth.equals(incomeItem.getMonth())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                ReportFragment.CategoryAmountByMonth newIncomeItem = new ReportFragment.CategoryAmountByMonth(outcomeMonth, "", "", 0.0);
                incomeList.add(newIncomeItem);
            }
        }
    }

    public static void synchronizeIncomeAndOutcomeMaps(Map<String, Double> incomeMap, Map<String, Double> outcomeMap) {
        for (String month : incomeMap.keySet()) {
            if (!outcomeMap.containsKey(month)) {
                outcomeMap.put(month, 0.0);
            }
        }

        for (String month : outcomeMap.keySet()) {
            if (!incomeMap.containsKey(month)) {
                incomeMap.put(month, 0.0);
            }
        }
    }
}
