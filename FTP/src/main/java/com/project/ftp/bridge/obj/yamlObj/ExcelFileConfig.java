package com.project.ftp.bridge.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class ExcelFileConfig {
    private String source;
    private String destination;
    private String copyDestination;
    private String sheetName;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCopyDestination() {
        return copyDestination;
    }

    public void setCopyDestination(String copyDestination) {
        this.copyDestination = copyDestination;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    @Override
    public String toString() {
        return "ExcelFileConfig{" +
                "source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", copyDestination='" + copyDestination + '\'' +
                ", sheetName='" + sheetName + '\'' +
                '}';
    }
}
