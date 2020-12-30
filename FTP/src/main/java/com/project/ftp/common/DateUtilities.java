package com.project.ftp.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtilities {
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
}
