package com.project.ftp.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)

public class TableConfiguration {
    private String dbType;
    private String dbIdentifier;
    private String tableConfigId;
    private String tableName;
    private String orderBy;
    private String limit;
    private String excelConfigId;
    private String defaultDeletedValue;
    private Boolean includeDeleted;
    private Boolean updateIfFound;
    private Boolean allowEmptyFilter;
    private MaintainHistory maintainHistory;
    private String joinParam;
    private HashMap<String, ArrayList<String>> defaultFilterMapping;
    private ArrayList<String> groupBy;
    private ArrayList<String> uniquePattern;
    private ArrayList<String> likeParameter;
    private ArrayList<String> filterParameter;
    private ArrayList<String> columnName;
    private ArrayList<String> updateColumnName;
    private ArrayList<String> compareBeforeUpdateColumn;
    private ArrayList<String> selectColumnName;

    public TableConfiguration() {}

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDbIdentifier() {
        return dbIdentifier;
    }

    public void setDbIdentifier(String dbIdentifier) {
        this.dbIdentifier = dbIdentifier;
    }

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

    public String getDefaultDeletedValue() {
        return defaultDeletedValue;
    }

    public void setDefaultDeletedValue(String defaultDeletedValue) {
        this.defaultDeletedValue = defaultDeletedValue;
    }

    public Boolean getIncludeDeleted() {
        return includeDeleted;
    }

    public void setIncludeDeleted(Boolean includeDeleted) {
        this.includeDeleted = includeDeleted;
    }

    public Boolean getUpdateIfFound() {
        return updateIfFound;
    }

    public void setUpdateIfFound(Boolean updateIfFound) {
        this.updateIfFound = updateIfFound;
    }

    public Boolean getAllowEmptyFilter() {
        return allowEmptyFilter;
    }

    public void setAllowEmptyFilter(Boolean allowEmptyFilter) {
        this.allowEmptyFilter = allowEmptyFilter;
    }

    public MaintainHistory getMaintainHistory() {
        return maintainHistory;
    }

    public void setMaintainHistory(MaintainHistory maintainHistory) {
        this.maintainHistory = maintainHistory;
    }

    public String getJoinParam() {
        return joinParam;
    }

    public void setJoinParam(String joinParam) {
        this.joinParam = joinParam;
    }

    public HashMap<String, ArrayList<String>> getDefaultFilterMapping() {
        return defaultFilterMapping;
    }

    public void setDefaultFilterMapping(HashMap<String, ArrayList<String>> defaultFilterMapping) {
        this.defaultFilterMapping = defaultFilterMapping;
    }

    public ArrayList<String> getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(ArrayList<String> groupBy) {
        this.groupBy = groupBy;
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

    public ArrayList<String> getCompareBeforeUpdateColumn() {
        return compareBeforeUpdateColumn;
    }

    public void setCompareBeforeUpdateColumn(ArrayList<String> compareBeforeUpdateColumn) {
        this.compareBeforeUpdateColumn = compareBeforeUpdateColumn;
    }

    public ArrayList<String> getSelectColumnName() {
        return selectColumnName;
    }

    public void setSelectColumnName(ArrayList<String> selectColumnName) {
        this.selectColumnName = selectColumnName;
    }

    @Override
    public String toString() {
        return "TableConfiguration{" +
                "dbType='" + dbType + '\'' +
                ", dbIdentifier='" + dbIdentifier + '\'' +
                ", tableConfigId='" + tableConfigId + '\'' +
                ", tableName='" + tableName + '\'' +
                ", orderBy='" + orderBy + '\'' +
                ", limit='" + limit + '\'' +
                ", excelConfigId='" + excelConfigId + '\'' +
                ", defaultDeletedValue='" + defaultDeletedValue + '\'' +
                ", includeDeleted=" + includeDeleted +
                ", updateIfFound=" + updateIfFound +
                ", allowEmptyFilter=" + allowEmptyFilter +
                ", maintainHistory=" + maintainHistory +
                ", joinParam='" + joinParam + '\'' +
                ", defaultFilterMapping=" + defaultFilterMapping +
                ", groupBy=" + groupBy +
                ", uniquePattern=" + uniquePattern +
                ", likeParameter=" + likeParameter +
                ", filterParameter=" + filterParameter +
                ", columnName=" + columnName +
                ", updateColumnName=" + updateColumnName +
                ", compareBeforeUpdateColumn=" + compareBeforeUpdateColumn +
                ", selectColumnName=" + selectColumnName +
                '}';
    }
}
