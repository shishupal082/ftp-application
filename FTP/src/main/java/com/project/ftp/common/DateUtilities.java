package com.project.ftp.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DateUtilities {
    public DateUtilities(){}
    public String getDateStrFromDateObj(String format, Date dateObj) {
        if (format == null || dateObj == null) {
            return "";
        }
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(dateObj);
    }
    public String getDateStrFromTimeMs(String format, Long timeInMs) {
        if (format == null || timeInMs == null) {
            return "";
        }
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(timeInMs);
    }
    public String getDateStrFromPattern(String pattern) {
        String result = "";
        if (pattern == null) {
            return result;
        }
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        result = dateFormat.format(currentDate);
        return result;
    }
    public boolean isValidDate(String pattern, String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
    public String getDateStrInNewPattern(String newPattern, String oldPattern,
                                         String dateStr, String defaultDate) {
        if (newPattern == null || oldPattern == null || dateStr == null) {
            return defaultDate;
        }
        if (newPattern.isEmpty() || oldPattern.isEmpty() || dateStr.isEmpty()) {
            return defaultDate;
        }
        String result;
        try {
            Date date = new SimpleDateFormat(oldPattern).parse(dateStr);
            DateFormat newDateFormat = new SimpleDateFormat(newPattern);
            result = newDateFormat.format(date);
        } catch (ParseException e) {
            result = defaultDate;
        }
        return result;
    }
}
