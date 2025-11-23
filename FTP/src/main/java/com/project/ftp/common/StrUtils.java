package com.project.ftp.common;

import com.project.ftp.config.AppConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class StrUtils {
    private final static Logger logger = LoggerFactory.getLogger(StrUtils.class);
    public StrUtils() {}
    public String formatString(String str) {
        if (str == null) {
            return "";
        }
        return str.trim();
    }
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
    public ArrayList<String> tokenizePath(String str, boolean checkBackSlash,
                                          boolean skipEmpty, boolean removeDynamic,
                                          boolean removeTrailingEmpty) {
        if (str == null) {
            return null;
        }
        if (checkBackSlash) {
            str = this.replaceBackSlashToSlash(str);
        }
        str = str.trim();
        String[] temp = this.stringSplit(str, "/", -1);
        ArrayList<String> result = this.getTokens(skipEmpty, removeDynamic, temp);
        if (removeTrailingEmpty) {
            result = this.replaceTrailingEmpty(result);
        }
        return result;
    }

    public ArrayList<String> tokenizePathV2(String str) {
        return this.tokenizePath(str, false, false, false, false);
    }
    private ArrayList<String> getTokens(boolean skipEmpty, boolean removeDynamic, String[] temp) {
        ArrayList<String> result = new ArrayList<>();
        String token;
        for (int i = 0; i< temp.length; i++) {
            token = temp[i].trim();
            if (skipEmpty) {
                if (AppConstant.EmptyStr.equals(token)) {
                    continue;
                }
            }
            if (removeDynamic) {
                if (".".equals(token)) {
                    continue;
                }
                if ("..".equals(token)) {
                    continue;
                }
            }
            result.add(token);
        }
        return result;
    }

    private ArrayList<String> replaceTrailingEmpty(ArrayList<String> result) {
        ArrayList<String> result2 = new ArrayList<>();
        int end = -1;
        String s;
        for(int i = result.size()-1; i>=0; i--) {
            s = result.get(i);
            if (s != null && !s.isEmpty()) {
                end = i;
                break;
            }
        }
        if (end >= 0) {
            for (int j=0; j<=end; j++) {
                s = result.get(j);
                if (s != null) {
                    result2.add(s);
                }
            }
        }
        return result2;
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
            return str;
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
    // limit = 0; (Default), Trailing empty strings will be discarded from the result
    // limit = < 0; (i.e. -1), Trailing empty strings will be included in the result
    // limit = > 0; (i.e. 1 or 2), Final result size will be 1 or 2 or ...
    public String[] stringSplit(String str, String regex, int limit) {
        if (str == null) {
            return null;
        }
        return str.split(regex, limit);
    }
    public String joinArrayList(ArrayList<String> strings, String seprator) {
        String result = "";
        if (seprator == null) {
            seprator = ",";
        }
        if (strings != null) {
            for(int i=0; i<strings.size(); i++) {
                if (i==0) {
                    result = strings.get(i);
                } else {
                    result = result.concat(seprator + strings.get(i));
                }
            }
        }
        return result;
    }
    public int strToInt(String str) {
        int result = 0;
        try {
            result = Integer.parseInt(str);
        } catch (Exception ignored) {}
        return result;
    }
}
