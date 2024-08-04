package com.project.ftp.bridge.obj;

import com.project.ftp.bridge.obj.yamlObj.ExcelDataConfig;

import java.util.ArrayList;

public class BridgeResponseSheetData {
    private boolean copyOldData;
    private String destination;
    private String copyDestination;
    private ExcelDataConfig excelDataConfigById;
    private ArrayList<ArrayList<String>> sheetData;

    public BridgeResponseSheetData(boolean copyOldData, String destination,
                                   String copyDestination, ArrayList<ArrayList<String>> sheetData) {
        this.copyOldData = copyOldData;
        this.destination = destination;
        this.copyDestination = copyDestination;
        this.sheetData = sheetData;
    }
    public boolean isCopyOldData() {
        return copyOldData;
    }

    public void setCopyOldData(boolean copyOldData) {
        this.copyOldData = copyOldData;
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

    public ExcelDataConfig getExcelDataConfigById() {
        return excelDataConfigById;
    }

    public void setExcelDataConfigById(ExcelDataConfig excelDataConfigById) {
        this.excelDataConfigById = excelDataConfigById;
    }

    public ArrayList<ArrayList<String>> getSheetData() {
        return sheetData;
    }

    public void setSheetData(ArrayList<ArrayList<String>> sheetData) {
        this.sheetData = sheetData;
    }

    @Override
    public String toString() {
        return "BridgeResponseSheetData{" +
                "copyOldData=" + copyOldData +
                ", destination='" + destination + '\'' +
                ", copyDestination='" + copyDestination + '\'' +
                ", excelDataConfigById=" + excelDataConfigById +
                ", sheetData=" + sheetData +
                '}';
    }
}
