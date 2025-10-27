package com.project.ftp.bridge.config;

import com.project.ftp.config.ApiIdentifier;

import java.util.HashMap;

public enum StandAloneApiIdentifier {
    UPDATE_EXCEL_DATA(ApiIdentifier.UPDATE_EXCEL_DATA.getApiName()),
    UPDATE_EXCEL_DATA_V2(ApiIdentifier.UPDATE_EXCEL_DATA_V2.getApiName()),
    UPDATE_MYSQL_TABLE_DATA_FROM_CSV(ApiIdentifier.UPDATE_MYSQL_TABLE_DATA_FROM_CSV.getApiName()),
    SPLIT_FILE(ApiIdentifier.SPLIT_FILE.getApiName());
    private final String apiName;
    StandAloneApiIdentifier(String name) {
        this.apiName = name;
    }
    public String getApiName() {
        return apiName;
    }

    private static final HashMap<String, StandAloneApiIdentifier> lookup = new HashMap<>();

    static {
        for (StandAloneApiIdentifier standAloneApiIdentifier : StandAloneApiIdentifier.values()) {
            lookup.put(standAloneApiIdentifier.getApiName(), standAloneApiIdentifier);
        }
    }
    public static StandAloneApiIdentifier get(String apiName) {
        if (apiName == null) {
            return null;
        }
        return lookup.get(apiName);
    }
}
