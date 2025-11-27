package com.project.ftp.bridge.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class ExcelDataConfig {
    private String id;
    private boolean copyOldData;
    private Boolean skipEmptyRows; //default value: True
    private String dateFormat;
    private String timeFormat;
    private String dateTimeFormat;
    private String commaReplacer;
    private String sourceApiName;
    private ArrayList<String> tableMappingIndex;
    private ArrayList<String> validFor;
    private ArrayList<String> allowedApi;
    private ArrayList<String> headingField;
    private MysqlCsvDataConfig mysqlCsvDataConfig;
    private ArrayList<CellMapping> cellMapping;
    private ArrayList<ReplaceCellDataMapping> replaceCellString;
    private ArrayList<SkipRowCriteria> skipRowCriteria;
    private ArrayList<Integer> copyCellDataIndex;
    private ArrayList<ArrayList<Integer>> skipRowIndex;
    private ArrayList<ArrayList<Integer>> appendCellDataIndex;
    private ArrayList<MergeColumnConfig> mergeColumnConfig;
    private ArrayList<Integer> removeColumnConfig;
    private ArrayList<Integer> uniqueEntry;
    //Locally generated

    private ArrayList<ExcelFileConfig> excelConfig;
    private ArrayList<ExcelFileConfig> csvConfig;
    private ArrayList<ExcelFileConfig> gsConfig;
    private ArrayList<ExcelFileConfig> apiConfig;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCopyOldData() {
        return copyOldData;
    }

    public void setCopyOldData(boolean copyOldData) {
        this.copyOldData = copyOldData;
    }

    public Boolean getSkipEmptyRows() {
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

    public String getCommaReplacer() {
        return commaReplacer;
    }

    public void setCommaReplacer(String commaReplacer) {
        this.commaReplacer = commaReplacer;
    }

    public String getSourceApiName() {
        return sourceApiName;
    }

    public void setSourceApiName(String sourceApiName) {
        this.sourceApiName = sourceApiName;
    }

    public ArrayList<String> getTableMappingIndex() {
        return tableMappingIndex;
    }

    public void setTableMappingIndex(ArrayList<String> tableMappingIndex) {
        this.tableMappingIndex = tableMappingIndex;
    }

    public ArrayList<String> getValidFor() {
        return validFor;
    }

    public void setValidFor(ArrayList<String> validFor) {
        this.validFor = validFor;
    }

    public ArrayList<String> getAllowedApi() {
        return allowedApi;
    }

    public void setAllowedApi(ArrayList<String> allowedApi) {
        this.allowedApi = allowedApi;
    }

    public ArrayList<String> getHeadingField() {
        return headingField;
    }

    public void setHeadingField(ArrayList<String> headingField) {
        this.headingField = headingField;
    }

    public MysqlCsvDataConfig getMysqlCsvDataConfig() {
        return mysqlCsvDataConfig;
    }

    public void setMysqlCsvDataConfig(MysqlCsvDataConfig mysqlCsvDataConfig) {
        this.mysqlCsvDataConfig = mysqlCsvDataConfig;
    }

    public ArrayList<CellMapping> getCellMapping() {
        return cellMapping;
    }

    public void setCellMapping(ArrayList<CellMapping> cellMapping) {
        this.cellMapping = cellMapping;
    }

    public ArrayList<ReplaceCellDataMapping> getReplaceCellString() {
        return replaceCellString;
    }

    public void setReplaceCellString(ArrayList<ReplaceCellDataMapping> replaceCellString) {
        this.replaceCellString = replaceCellString;
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

    public ArrayList<Integer> getUniqueEntry() {
        return uniqueEntry;
    }

    public void setUniqueEntry(ArrayList<Integer> uniqueEntry) {
        this.uniqueEntry = uniqueEntry;
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

    public ArrayList<ExcelFileConfig> getApiConfig() {
        return apiConfig;
    }

    public void setApiConfig(ArrayList<ExcelFileConfig> apiConfig) {
        this.apiConfig = apiConfig;
    }

    @Override
    public String toString() {
        return "ExcelDataConfig{" +
                "id='" + id + '\'' +
                ", copyOldData=" + copyOldData +
                ", skipEmptyRows=" + skipEmptyRows +
                ", dateFormat='" + dateFormat + '\'' +
                ", timeFormat='" + timeFormat + '\'' +
                ", dateTimeFormat='" + dateTimeFormat + '\'' +
                ", commaReplacer='" + commaReplacer + '\'' +
                ", sourceApiName='" + sourceApiName + '\'' +
                ", tableMappingIndex=" + tableMappingIndex +
                ", validFor=" + validFor +
                ", allowedApi=" + allowedApi +
                ", headingField=" + headingField +
                ", mysqlCsvDataConfig=" + mysqlCsvDataConfig +
                ", cellMapping=" + cellMapping +
                ", replaceCellString=" + replaceCellString +
                ", skipRowCriteria=" + skipRowCriteria +
                ", copyCellDataIndex=" + copyCellDataIndex +
                ", skipRowIndex=" + skipRowIndex +
                ", appendCellDataIndex=" + appendCellDataIndex +
                ", mergeColumnConfig=" + mergeColumnConfig +
                ", removeColumnConfig=" + removeColumnConfig +
                ", uniqueEntry=" + uniqueEntry +
                ", excelConfig=" + excelConfig +
                ", csvConfig=" + csvConfig +
                ", gsConfig=" + gsConfig +
                ", apiConfig=" + apiConfig +
                '}';
    }
}
