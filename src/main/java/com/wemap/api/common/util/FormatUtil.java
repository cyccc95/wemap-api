package com.wemap.api.common.util;

import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
public class FormatUtil {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static LocalDate formatBirthDayStringToLocalDate(String birthday) {
        return LocalDate.parse(birthday, dateFormatter);
    }
}
