package com.project.javaproject.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {
    
    public static List<String> checkIsDate(String date) {
        List<String> dateErrors = new ArrayList<>();
        Pattern patternDate = Pattern.compile("^([0-9]{4})-([0-9]{2})-([0-9]{2})$");
        Matcher isValidDate = patternDate.matcher(date);

        if (date.isEmpty()) {
            dateErrors.add("Date is required");
            return dateErrors;
        }

        if (isValidDate.find() == false) {
            dateErrors.add("Enter a valid date. Format: YYYY-MM-DD");
            return dateErrors;
        }

        int year = Integer.parseInt(date.split("-")[0]);
        int month = Integer.parseInt(date.split("-")[1]);
        int day = Integer.parseInt(date.split("-")[2]);

        int minimumYear = Integer.parseInt(LocalDate.now().minusYears(1).toString().split("-")[0]);
        int currentYear = Integer.parseInt(LocalDate.now().toString().split("-")[0]);

        if (year < minimumYear || year > currentYear) {
            dateErrors.add(String.format("Year must be between %1$d and %2$d", minimumYear, currentYear));
        }

        if (month == 0 || month > 12) {
            dateErrors.add("Month must be between 1 and 12");
        }

        if (day == 0 || day > 31) {
            dateErrors.add("Day must be between 1 and 31");
        }

        return dateErrors;
    }
}
