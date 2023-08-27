package com.project.ftp.bridge.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class FileConfigMapping {
    private Boolean fileConfigSourceGoogle; // Default = true (For google)
    private ArrayList<String> validRequestId;
    private ArrayList<String> fileConfig;
    private ArrayList<Integer> requiredColIndex;

    public Boolean getFileConfigSourceGoogle() {
        if (fileConfigSourceGoogle == null) {
            return true;
        }
        return fileConfigSourceGoogle;
    }

    public void setFileConfigSourceGoogle(Boolean fileConfigSourceGoogle) {
        this.fileConfigSourceGoogle = fileConfigSourceGoogle;
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
                "fileConfigSourceGoogle=" + fileConfigSourceGoogle +
                ", validRequestId=" + validRequestId +
                ", fileConfig=" + fileConfig +
                ", requiredColIndex=" + requiredColIndex +
                '}';
    }
}
