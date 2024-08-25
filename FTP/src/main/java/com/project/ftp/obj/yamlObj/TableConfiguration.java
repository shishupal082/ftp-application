package com.project.ftp.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class TableConfiguration {
    private String tableConfigId;
    private String tableName;
    private ArrayList<String> columnName;

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

    public ArrayList<String> getColumnName() {
        return columnName;
    }

    public void setColumnName(ArrayList<String> columnName) {
        this.columnName = columnName;
    }
}
