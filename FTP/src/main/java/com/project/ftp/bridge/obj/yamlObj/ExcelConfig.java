package com.project.ftp.bridge.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)

public class ExcelConfig {
    private HashMap<String, ExcelDataConfig> excelDataConfig;

    public HashMap<String, ExcelDataConfig> getExcelDataConfig() {
        return excelDataConfig;
    }

    public void setExcelDataConfig(HashMap<String, ExcelDataConfig> excelDataConfig) {
        this.excelDataConfig = excelDataConfig;
    }

    @Override
    public String toString() {
        return "ExcelConfig{" +
                "excelDataConfig=" + excelDataConfig +
                '}';
    }
}
