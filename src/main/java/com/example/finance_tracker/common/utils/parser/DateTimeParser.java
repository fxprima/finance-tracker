package com.example.finance_tracker.common.utils.parser;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeParser {
    private static final ZoneId ZONE = ZoneId.of("Asia/Jakarta");

    public static LocalDateTime convertUtcToLocalDateTime(String utcString) {
        if (utcString == null || utcString.isBlank())
            return null;


        return Instant.parse(utcString.trim())
                .atZone(ZONE)
                .toLocalDateTime();
    }

}
