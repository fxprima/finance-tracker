package com.example.finance_tracker.common.utils.formatter;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Locale;

public class LocalDateTimeFormatter {

    public static String formatLocalDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, new Locale("id", "ID"));
        return dateTime.format(formatter);
    }

}
