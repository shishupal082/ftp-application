package com.project.ftp.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class TableConfiguration {
    private String tableConfigId;
    private String tableName;
    private String orderBy;
    private String limit;
    private String excelConfigId;
    private ArrayList<String> uniquePattern;
    private ArrayList<String> likeParameter;
    private ArrayList<String> filterParameter;
    private ArrayList<String> columnName;
    private ArrayList<String> updateColumnName;
    private ArrayList<String> selectColumnName;

    public String getTableConfigId() {
        return tableConfigId;
    }

    public void setTableConfigId(String tableConfigId) {
        this.tableConfigId = tableConfigId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getExcelConfigId() {
        return excelConfigId;
    }

    public void setExcelConfigId(String excelConfigId) {
        this.excelConfigId = excelConfigId;
    }

    public ArrayList<String> getUniquePattern() {
        return uniquePattern;
    }

    public void setUniquePattern(ArrayList<String> uniquePattern) {
        this.uniquePattern = uniquePattern;
    }

    public ArrayList<String> getLikeParameter() {
        return likeParameter;
    }

    public void setLikeParameter(ArrayList<String> likeParameter) {
        this.likeParameter = likeParameter;
    }

    public ArrayList<String> getFilterParameter() {
        return filterParameter;
    }

    public void setFilterParameter(ArrayList<String> filterParameter) {
        this.filterParameter = filterParameter;
    }

    public ArrayList<String> getColumnName() {
        return columnName;
    }

    public void setColumnName(ArrayList<String> columnName) {
        this.columnName = columnName;
    }

    public ArrayList<String> getUpdateColumnName() {
        return updateColumnName;
    }

    public void setUpdateColumnName(ArrayList<String> updateColumnName) {
        this.updateColumnName = updateColumnName;
    }

    public ArrayList<String> getSelectColumnName() {
        return selectColumnName;
    }

    public void setSelectColumnName(ArrayList<String> selectColumnName) {
        this.selectColumnName = selectColumnName;
    }
}
