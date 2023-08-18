package com.project.ftp.bridge.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class ExcelDataConfig {
    private boolean copyOldData;
    private Boolean skipEmptyRows; //default value: True
    private String dateFormat;
    private String timeFormat;
    private String dateTimeFormat;
    private ArrayList<ExcelFileConfig> fileConfig;
    private ArrayList<ExcelFileConfig> gsConfig;
    private ArrayList<CellMapping> cellMapping;
    private ArrayList<Integer> copyCellDataIndex;
    private ArrayList<ArrayList<Integer>> skipRowIndex;
    private ArrayList<ArrayList<Integer>> appendCellDataIndex;

    public boolean isCopyOldData() {
        return copyOldData;
    }

    public void setCopyOldData(boolean copyOldData) {
        this.copyOldData = copyOldData;
    }

    public Boolean isSkipEmptyRows() {
        if (skipEmptyRows == null) {
            return true;
        }
        return skipEmptyRows;
    }

    public void setSkipEmptyRows(Boolean skipEmptyRows) {
        this.skipEmptyRows = skipEmptyRows;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    public ArrayList<ExcelFileConfig> getFileConfig() {
        return fileConfig;
    }

    public void setFileConfig(ArrayList<ExcelFileConfig> fileConfig) {
        this.fileConfig = fileConfig;
    }

    public ArrayList<ExcelFileConfig> getGsConfig() {
        return gsConfig;
    }

    public void setGsConfig(ArrayList<ExcelFileConfig> gsConfig) {
        this.gsConfig = gsConfig;
    }

    public ArrayList<CellMapping> getCellMapping() {
        return cellMapping;
    }

    public void setCellMapping(ArrayList<CellMapping> cellMapping) {
        this.cellMapping = cellMapping;
    }

    public ArrayList<Integer> getCopyCellDataIndex() {
        return copyCellDataIndex;
    }

    public void setCopyCellDataIndex(ArrayList<Integer> copyCellDataIndex) {
        this.copyCellDataIndex = copyCellDataIndex;
    }

    public ArrayList<ArrayList<Integer>> getSkipRowIndex() {
        return skipRowIndex;
    }

    public void setSkipRowIndex(ArrayList<ArrayList<Integer>> skipRowIndex) {
        this.skipRowIndex = skipRowIndex;
    }

    public ArrayList<ArrayList<Integer>> getAppendCellDataIndex() {
        return appendCellDataIndex;
    }

    public void setAppendCellDataIndex(ArrayList<ArrayList<Integer>> appendCellDataIndex) {
        this.appendCellDataIndex = appendCellDataIndex;
    }

    @Override
    public String toString() {
        return "ExcelDataConfig{" +
                "copyOldData=" + copyOldData +
                ", skipEmptyRows=" + skipEmptyRows +
                ", dateFormat='" + dateFormat + '\'' +
                ", timeFormat='" + timeFormat + '\'' +
                ", dateTimeFormat='" + dateTimeFormat + '\'' +
                ", fileConfig=" + fileConfig +
                ", gsConfig=" + gsConfig +
                ", cellMapping=" + cellMapping +
                ", copyCellDataIndex=" + copyCellDataIndex +
                ", skipRowIndex=" + skipRowIndex +
                ", appendCellDataIndex=" + appendCellDataIndex +
                '}';
    }
}
