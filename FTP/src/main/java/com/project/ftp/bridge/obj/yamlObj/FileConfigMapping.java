package com.project.ftp.bridge.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class FileConfigMapping {
    private ArrayList<String> validRequestId;
    private ArrayList<String> fileConfig;
    private ArrayList<Integer> requiredColIndex;

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
                "validRequestId=" + validRequestId +
                ", fileConfig=" + fileConfig +
                ", requiredColIndex=" + requiredColIndex +
                '}';
    }
}
