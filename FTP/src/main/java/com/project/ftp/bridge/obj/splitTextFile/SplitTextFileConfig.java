package com.project.ftp.bridge.obj.splitTextFile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class SplitTextFileConfig {
    private String id;
    private String sourceFilePath;
    private ArrayList<String> destinationHeader;
    private ArrayList<DestinationConfig> destinationConfig;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    public ArrayList<String> getDestinationHeader() {
        return destinationHeader;
    }

    public void setDestinationHeader(ArrayList<String> destinationHeader) {
        this.destinationHeader = destinationHeader;
    }

    public ArrayList<DestinationConfig> getDestinationConfig() {
        return destinationConfig;
    }

    public void setDestinationConfig(ArrayList<DestinationConfig> destinationConfig) {
        this.destinationConfig = destinationConfig;
    }

    @Override
    public String toString() {
        return "SplitTextFileConfig{" +
                "id='" + id + '\'' +
                ", sourceFilePath='" + sourceFilePath + '\'' +
                ", destinationHeader=" + destinationHeader +
                ", destinationConfig=" + destinationConfig +
                '}';
    }
}
