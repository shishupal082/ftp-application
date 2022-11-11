package com.project.ftp.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StrUtils {
    private final static Logger logger = LoggerFactory.getLogger(StrUtils.class);
    public StrUtils() {}
    public String replaceLast(String find, String replace, String string) {
        int lastIndex = string.lastIndexOf(find);
        if (lastIndex == -1) {
            return string;
        }
        String beginString = string.substring(0, lastIndex);
        String endString = string.substring(lastIndex + find.length());
        return beginString + replace + endString;
    }
    public String replaceBackSlashToSlash(String str) {
        return this.replaceChar(str, "\\\\", "/");
    }
    public String replaceDynamicPathDir(String filePath, String replacement) {
        if (filePath == null) {
            return null;
        }
        filePath = filePath.replaceAll("/\\./", replacement);
        return filePath.replaceAll("/\\.\\./", replacement);
    }
    public String replaceChar(String str, String find, String replace) {
        if (str == null || find == null || replace == null) {
            return null;
        }
        String result = "";
        char strChar;
        char findChar = find.charAt(0);
        for(int i=0; i<str.length(); i++) {
            strChar = str.charAt(i);
            if (strChar == findChar) {
                result = result.concat(replace);
                continue;
            }
            result = result + strChar;
        }
        return result;
    }
    public String replaceString(String str, String find, String replace) {
        if (str == null || find == null || replace == null) {
            return null;
        }
        return str.replaceAll(find, replace);
    }
    public boolean isInValidString(String str) {
        if (str == null) {
            return true;
        }
        str = str.trim();
        return str.isEmpty();
    }
    public String[] stringSplit(String str, String regex, int limit) {
        if (str == null) {
            return null;
        }
        return str.split(regex, limit);
    }
    public int strToInt(String str) {
        int result = 0;
        try {
            result = Integer.parseInt(str);
        } catch (Exception ignored) {};
        return result;
    }
}
