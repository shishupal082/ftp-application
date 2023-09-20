package com.project.ftp.bridge.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class FileMappingConfig {
    private ArrayList<String> excelFileConfigPath;
    private ArrayList<FileConfigMapping> googleConfig;
    private ArrayList<FileConfigMapping> excelConfig;
    private ArrayList<FileConfigMapping> csvConfig;

    public ArrayList<String> getExcelFileConfigPath() {
        return excelFileConfigPath;
    }

    public void setExcelFileConfigPath(ArrayList<String> excelFileConfigPath) {
        this.excelFileConfigPath = excelFileConfigPath;
    }

    public ArrayList<FileConfigMapping> getGoogleConfig() {
        return googleConfig;
    }

    public void setGoogleConfig(ArrayList<FileConfigMapping> googleConfig) {
        this.googleConfig = googleConfig;
    }

    public ArrayList<FileConfigMapping> getExcelConfig() {
        return excelConfig;
    }

    public void setExcelConfig(ArrayList<FileConfigMapping> excelConfig) {
        this.excelConfig = excelConfig;
    }

    public ArrayList<FileConfigMapping> getCsvConfig() {
        return csvConfig;
    }

    public void setCsvConfig(ArrayList<FileConfigMapping> csvConfig) {
        this.csvConfig = csvConfig;
    }

    @Override
    public String toString() {
        return "FileMappingConfig{" +
                "excelFileConfigPath=" + excelFileConfigPath +
                ", googleConfig=" + googleConfig +
                ", excelConfig=" + excelConfig +
                ", csvConfig=" + csvConfig +
                '}';
    }
}
