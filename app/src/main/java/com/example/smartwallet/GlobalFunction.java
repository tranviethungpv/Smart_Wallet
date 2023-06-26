package com.example.smartwallet;

import com.google.firebase.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

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
        return String.format(Locale.getDefault(), "tháng " + month + " năm "+ year);
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
}
