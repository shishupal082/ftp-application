package com.project.ftp.bridge.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)

public class FileMappingConfig {
    private ArrayList<String> excelFileConfigPath;
    private ArrayList<FileConfigMapping> googleConfig;
    private ArrayList<FileConfigMapping> excelConfig;
    private ArrayList<FileConfigMapping> csvConfig;
    private HashMap<String, ArrayList<String>> combineRequestIds;

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

    public HashMap<String, ArrayList<String>> getCombineRequestIds() {
        return combineRequestIds;
    }

    public void setCombineRequestIds(HashMap<String, ArrayList<String>> combineRequestIds) {
        this.combineRequestIds = combineRequestIds;
    }

    @Override
    public String toString() {
        return "FileMappingConfig{" +
                "excelFileConfigPath=" + excelFileConfigPath +
                ", googleConfig=" + googleConfig +
                ", excelConfig=" + excelConfig +
                ", csvConfig=" + csvConfig +
                ", combineRequestIds=" + combineRequestIds +
                '}';
    }
}
