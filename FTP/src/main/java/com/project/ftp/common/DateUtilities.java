package com.project.ftp.common;

import com.project.ftp.config.AppConstant;

import java.text.DateFormat;
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
    public String getDateStrFromPattern(String pattern, String defaultStr) {
        String result = defaultStr;
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
        String year, month;
        int yearNum, monthNum;
        DateFormat newDateFormat;
        try {
            Date date = new SimpleDateFormat(oldPattern).parse(dateStr);
            if (newPattern.equals(AppConstant.FinancialYearFormat)) {
                newDateFormat = new SimpleDateFormat("yyyy");
                year = newDateFormat.format(date);
                newDateFormat = new SimpleDateFormat("M");
                month = newDateFormat.format(date);
                yearNum = Integer.parseInt(year);
                monthNum = Integer.parseInt(month);
                if (monthNum <= 3) {
                    result = (yearNum-1) + "-" + yearNum;
                } else {
                    result = yearNum + "-" + (yearNum+1);
                }
            } else {
                newDateFormat = new SimpleDateFormat(newPattern);
                result = newDateFormat.format(date);
            }
        } catch (Exception e) {
            result = defaultDate;
        }
        return result;
    }
}
