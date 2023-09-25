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
    private ArrayList<ExcelFileConfig> excelConfig;
    private ArrayList<ExcelFileConfig> csvConfig;
    private ArrayList<ExcelFileConfig> gsConfig;
    private ArrayList<CellMapping> cellMapping;
    private ArrayList<SkipRowCriteria> skipRowCriteria;
    private ArrayList<Integer> copyCellDataIndex;
    private ArrayList<ArrayList<Integer>> skipRowIndex;
    private ArrayList<ArrayList<Integer>> appendCellDataIndex;
    private ArrayList<MergeColumnConfig> mergeColumnConfig;
    private ArrayList<Integer> removeColumnConfig;
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

    public ArrayList<ExcelFileConfig> getExcelConfig() {
        return excelConfig;
    }

    public void setExcelConfig(ArrayList<ExcelFileConfig> excelConfig) {
        this.excelConfig = excelConfig;
    }

    public ArrayList<ExcelFileConfig> getCsvConfig() {
        return csvConfig;
    }

    public void setCsvConfig(ArrayList<ExcelFileConfig> csvConfig) {
        this.csvConfig = csvConfig;
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

    public ArrayList<SkipRowCriteria> getSkipRowCriteria() {
        return skipRowCriteria;
    }

    public void setSkipRowCriteria(ArrayList<SkipRowCriteria> skipRowCriteria) {
        this.skipRowCriteria = skipRowCriteria;
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

    public ArrayList<MergeColumnConfig> getMergeColumnConfig() {
        return mergeColumnConfig;
    }

    public void setMergeColumnConfig(ArrayList<MergeColumnConfig> mergeColumnConfig) {
        this.mergeColumnConfig = mergeColumnConfig;
    }

    public ArrayList<Integer> getRemoveColumnConfig() {
        return removeColumnConfig;
    }

    public void setRemoveColumnConfig(ArrayList<Integer> removeColumnConfig) {
        this.removeColumnConfig = removeColumnConfig;
    }

    @Override
    public String toString() {
        return "ExcelDataConfig{" +
                "copyOldData=" + copyOldData +
                ", skipEmptyRows=" + skipEmptyRows +
                ", dateFormat='" + dateFormat + '\'' +
                ", timeFormat='" + timeFormat + '\'' +
                ", dateTimeFormat='" + dateTimeFormat + '\'' +
                ", excelConfig=" + excelConfig +
                ", csvConfig=" + csvConfig +
                ", gsConfig=" + gsConfig +
                ", cellMapping=" + cellMapping +
                ", skipRowCriteria=" + skipRowCriteria +
                ", copyCellDataIndex=" + copyCellDataIndex +
                ", skipRowIndex=" + skipRowIndex +
                ", appendCellDataIndex=" + appendCellDataIndex +
                ", mergeColumnConfig=" + mergeColumnConfig +
                ", removeColumnConfig=" + removeColumnConfig +
                '}';
    }
}
