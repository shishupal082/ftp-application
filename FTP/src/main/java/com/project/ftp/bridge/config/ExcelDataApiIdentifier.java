package com.project.ftp.bridge.config;

import com.project.ftp.config.ApiIdentifier;

import java.util.HashMap;

public enum ExcelDataApiIdentifier {
    GET_MYSQL_TABLE_DATA(ApiIdentifier.GET_MYSQL_TABLE_DATA.getApiName()),
    READ_SCAN_DIR(ApiIdentifier.READ_SCAN_DIR.getApiName());
    private final String apiName;
    ExcelDataApiIdentifier(String name) {
        this.apiName = name;
    }
    public String getApiName() {
        return apiName;
    }

    private static final HashMap<String, ExcelDataApiIdentifier> lookup = new HashMap<>();

    static {
        for (ExcelDataApiIdentifier standAloneApiIdentifier : ExcelDataApiIdentifier.values()) {
            lookup.put(standAloneApiIdentifier.getApiName(), standAloneApiIdentifier);
        }
    }
    public static ExcelDataApiIdentifier get(String apiName) {
        if (apiName == null) {
            return null;
        }
        return lookup.get(apiName);
    }
}
