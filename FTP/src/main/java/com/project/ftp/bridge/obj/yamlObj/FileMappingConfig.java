package com.project.ftp.bridge.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class FileMappingConfig {
    private ArrayList<String> excelFileConfigPath;
    private ArrayList<FileConfigMapping> googleConfig;
    private ArrayList<FileConfigMapping> csvConfig;
    private ArrayList<FileConfigMapping> googleToCsvConfig;

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

    public ArrayList<FileConfigMapping> getCsvConfig() {
        return csvConfig;
    }

    public void setCsvConfig(ArrayList<FileConfigMapping> csvConfig) {
        this.csvConfig = csvConfig;
    }

    public ArrayList<FileConfigMapping> getGoogleToCsvConfig() {
        return googleToCsvConfig;
    }

    public void setGoogleToCsvConfig(ArrayList<FileConfigMapping> googleToCsvConfig) {
        this.googleToCsvConfig = googleToCsvConfig;
    }

    @Override
    public String toString() {
        return "FileMappingConfig{" +
                "excelFileConfigPath=" + excelFileConfigPath +
                ", googleConfig=" + googleConfig +
                ", csvConfig=" + csvConfig +
                ", googleToCsvConfig=" + googleToCsvConfig +
                '}';
    }
}
