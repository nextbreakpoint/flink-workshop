package com.nextbreakpoint.flink.common;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Dates {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy'-'MM'-'dd'T'HH:mm:ss.SSS'Z'");

    private Dates() {}

    public static long parseEpochMilli(CharSequence date) {
        return LocalDateTime.from(TIME_FORMATTER.parse(date)).toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public static long parseEpochMilliAtStartOfDay(CharSequence date) {
        return LocalDateTime.from(TIME_FORMATTER.parse(date)).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }
}
