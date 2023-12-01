package com.project.ftp.bridge.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class FileConfigMapping {
    private String fileDataSource; // Default google, msExcel, csv
    private ArrayList<String> validRequestId;
    private ArrayList<String> fileConfig;
    private ArrayList<Integer> requiredColIndex;

    public String getFileDataSource() {
        return fileDataSource;
    }

    public void setFileDataSource(String fileDataSource) {
        this.fileDataSource = fileDataSource;
    }

    public ArrayList<String> getValidRequestId() {
        return validRequestId;
    }

    public void setValidRequestId(ArrayList<String> validRequestId) {
        this.validRequestId = validRequestId;
    }

    public ArrayList<String> getFileConfig() {
        return fileConfig;
    }

    public void setFileConfig(ArrayList<String> fileConfig) {
        this.fileConfig = fileConfig;
    }

    public ArrayList<Integer> getRequiredColIndex() {
        return requiredColIndex;
    }

    public void setRequiredColIndex(ArrayList<Integer> requiredColIndex) {
        this.requiredColIndex = requiredColIndex;
    }

    @Override
    public String toString() {
        return "FileConfigMapping{" +
                "fileDataSource='" + fileDataSource + '\'' +
                ", validRequestId=" + validRequestId +
                ", fileConfig=" + fileConfig +
                ", requiredColIndex=" + requiredColIndex +
                '}';
    }
}
